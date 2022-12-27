package ru.romanow.camunda.cashflow

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.util.UriComponentsBuilder.fromPath
import ru.romanow.camunda.config.DatabaseTestConfiguration
import ru.romanow.camunda.config.properties.ReverseEtlProperties
import ru.romanow.camunda.config.properties.SettingsProperties
import ru.romanow.camunda.domain.enums.Status
import ru.romanow.camunda.models.*
import ru.romanow.camunda.repository.CalculationStatusRepository
import ru.romanow.camunda.utils.SUCCESS
import ru.romanow.camunda.utils.UPLOADED
import ru.romanow.camunda.utils.UPLOAD_FINISHED
import ru.romanow.camunda.utils.UUIDGenerator
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
@Import(DatabaseTestConfiguration::class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class CashFlowProcessTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var calculationStatusRepository: CalculationStatusRepository

    @Autowired
    private lateinit var settingsProperties: SettingsProperties

    @Autowired
    private lateinit var reverseEtlProperties: ReverseEtlProperties

    @MockBean
    private lateinit var generator: UUIDGenerator

    private var objectMapper = ObjectMapper()

    init {
        objectMapper.registerModules(JavaTimeModule())
    }

    @Test
    @Order(1)
    fun createCalculationAndStartProcess() {
        val period = PeriodRequest(
            startDate = START_DATE,
            endDate = START_DATE.plusDays(7),
            serialNumber = 1,
            mark = MARK,
        )
        val request = CreateCalculationRequest(
            name = randomAlphabetic(8),
            periods = listOf(period),
            startDate = FACT_DATE.atStartOfDay(),
            macroUid = MACRO_UID,
            transferRateUid = TRANSFER_RATE_UID,
            productScenarioUid = PRODUCT_SCENARIO_UID,
        )

        Mockito.`when`(generator.generate()).thenReturn(CALCULATION_UID)

        val migrationRequest = listOf(period)
        val migrationResponse = EtlResponse(listOf(EtlTableResponse(DATABASE, SCHEMA, TABLE)))

        prepareStubForMacroMigrationToStage(
            CALCULATION_UID,
            MACRO_UID,
            migrationRequest,
            migrationResponse
        )

        prepareStubForTransferRateMigrationToStage(
            CALCULATION_UID,
            TRANSFER_RATE_UID,
            migrationRequest,
            migrationResponse
        )

        prepareStubForProductScenarioMigrationToStage(
            CALCULATION_UID,
            PRODUCT_SCENARIO_UID,
            migrationRequest,
            migrationResponse
        )

        prepareStubForUploadParams(CALCULATION_UID)

        // When
        val result = mockMvc
            .perform(MockMvcRequestBuilders.post("/api/v1/cashflow/calculation/")
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
            .andReturn()

        // Then
        val location = result.response.getHeader(HttpHeaders.LOCATION)!!
        assertThat(UUID.fromString(location.substring(location.lastIndexOf("/") + 1))).isEqualTo(CALCULATION_UID)

        val statuses = calculationStatusRepository
            .getStatuses(CALCULATION_UID, Pageable.unpaged())
            .map { it.status }

        assertThat(statuses)
            .containsExactly(
                Status.ETL_SENT_TO_DRP,
                Status.ETL_START,
                Status.DATA_COPIED_TO_STAGED,
                Status.CALCULATION_STARTED
            )
    }

    @Test
    @Order(2)
    fun startCalculation() {
        // Given
        prepareStubForDrpStartCalculation(CALCULATION_UID)

        // When
        val request = AirflowResponse(
            solveId = CALCULATION_UID,
            status = UPLOADED,
            aggReportTableName = "$DATABASE.$SCHEMA.$TABLE",
            calculationParametersTables = mapOf()
        )
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cashflow/calculation/$CALCULATION_UID/answer-from-drp")
            .accept(APPLICATION_JSON_VALUE)
            .contentType(APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.uid").value(CALCULATION_UID.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.CALCULATION_SENT_TO_DRP.name))

        // Then
        val statuses = calculationStatusRepository
            .getStatuses(CALCULATION_UID, Pageable.unpaged())
            .map { it.status }

        assertThat(statuses)
            .containsExactly(
                Status.CALCULATION_SENT_TO_DRP,
                Status.CALCULATION_START,
                Status.ETL_COMPLETED,
                Status.ETL_SENT_TO_DRP,
                Status.ETL_START,
                Status.DATA_COPIED_TO_STAGED,
                Status.CALCULATION_STARTED
            )
    }

    @Test
    @Order(3)
    fun startReverseEtl() {
        // Given
        prepareStubForDrpLoadResult(CALCULATION_UID, TABLE, reverseEtlProperties)

        // When
        val request = AirflowResponse(
            solveId = CALCULATION_UID,
            status = SUCCESS,
            aggReportTableName = TABLE
        )
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cashflow/calculation/$CALCULATION_UID/answer-from-drp")
            .accept(APPLICATION_JSON_VALUE)
            .contentType(APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.uid").value(CALCULATION_UID.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.REVERSED_ETL_SENT_TO_DRP.name))

        // Then
        val statuses = calculationStatusRepository
            .getStatuses(CALCULATION_UID, Pageable.unpaged())
            .map { it.status }

        assertThat(statuses)
            .containsExactly(
                Status.REVERSED_ETL_SENT_TO_DRP,
                Status.REVERSED_ETL_START,
                Status.CALCULATION_COMPLETED,
                Status.CALCULATION_SENT_TO_DRP,
                Status.CALCULATION_START,
                Status.ETL_COMPLETED,
                Status.ETL_SENT_TO_DRP,
                Status.ETL_START,
                Status.DATA_COPIED_TO_STAGED,
                Status.CALCULATION_STARTED
            )
    }

    @Test
    @Order(4)
    fun finishCalculation() {
        // Given
        prepareStubForBalanceMigrationFromStage(CALCULATION_UID)

        // When
        val request = AirflowResponse(
            solveId = CALCULATION_UID,
            status = UPLOAD_FINISHED
        )
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cashflow/calculation/$CALCULATION_UID/answer-from-drp")
            .accept(APPLICATION_JSON_VALUE)
            .contentType(APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.uid").value(CALCULATION_UID.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.CALCULATION_FINISHED.name))

        // Then
        val statuses = calculationStatusRepository
            .getStatuses(CALCULATION_UID, Pageable.unpaged())
            .map { it.status }

        assertThat(statuses)
            .containsExactly(
                Status.CALCULATION_FINISHED,
                Status.DATA_COPIED_FROM_STAGED,
                Status.REVERSED_ETL_COMPLETED,
                Status.REVERSED_ETL_SENT_TO_DRP,
                Status.REVERSED_ETL_START,
                Status.CALCULATION_COMPLETED,
                Status.CALCULATION_SENT_TO_DRP,
                Status.CALCULATION_START,
                Status.ETL_COMPLETED,
                Status.ETL_SENT_TO_DRP,
                Status.ETL_START,
                Status.DATA_COPIED_TO_STAGED,
                Status.CALCULATION_STARTED
            )
    }

    private fun prepareStubForMacroMigrationToStage(
        calculationUid: UUID,
        macroUid: UUID,
        migrationRequest: List<PeriodRequest>,
        migrationResponse: EtlResponse,
    ) {
        val uri = fromPath("/api/v1/macro/migration-to-staged")
            .queryParam("calculationUid", calculationUid)
            .queryParam("calculationVersionUid", calculationUid)
            .queryParam("macroUid", macroUid)
            .build()
            .toUriString()
        wireMock.stubFor(post(uri)
            .withRequestBody(equalTo(objectMapper.writeValueAsString(migrationRequest)))
            .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
            .willReturn(ok().withBody(objectMapper.writeValueAsString(migrationResponse))
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)))
    }

    private fun prepareStubForTransferRateMigrationToStage(
        calculationUid: UUID,
        transferRateUid: UUID,
        migrationRequest: List<PeriodRequest>,
        migrationResponse: EtlResponse,
    ) {
        val uri = fromPath("/api/v1/transfer-rate/migration-to-staged")
            .queryParam("calculationUid", calculationUid)
            .queryParam("calculationVersionUid", calculationUid)
            .queryParam("transferRateUid", transferRateUid)
            .build()
            .toUriString()
        wireMock.stubFor(post(uri)
            .withRequestBody(equalTo(objectMapper.writeValueAsString(migrationRequest)))
            .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
            .willReturn(ok().withBody(objectMapper.writeValueAsString(migrationResponse))
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE))
        )
    }

    private fun prepareStubForProductScenarioMigrationToStage(
        calculationUid: UUID,
        productScenarioUid: UUID,
        migrationRequest: List<PeriodRequest>,
        migrationResponse: EtlResponse,
    ) {
        val uri = fromPath("/api/v1/product-scenario/migration-to-staged")
            .queryParam("calculationVersionUid", calculationUid)
            .queryParam("scenarioUid", productScenarioUid)
            .build()
            .toUriString()

        wireMock.stubFor(post(uri)
            .withRequestBody(equalTo(objectMapper.writeValueAsString(migrationRequest)))
            .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
            .willReturn(ok().withBody(objectMapper.writeValueAsString(migrationResponse))
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)))
    }

    private fun prepareStubForUploadParams(calculationUid: UUID) {
        val uploadRequest = UploadCashflowParametersCommand(
            solveId = calculationUid,
            calculationSourceTables = listOf(
                "$DATABASE.$SCHEMA.$TABLE",
                "$DATABASE.$SCHEMA.$TABLE",
                "$DATABASE.$SCHEMA.$TABLE"
            )
        )

        wireMock.stubFor(
            post("/api/v1/drp/cash-flow/copy-params")
                .withRequestBody(equalTo(objectMapper.writeValueAsString(uploadRequest)))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .willReturn(ok().withBody(objectMapper.writeValueAsString(UUID.randomUUID()))
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)))
    }

    private fun prepareStubForDrpStartCalculation(calculationUid: UUID) {
        val balanceProductConfigs: List<BalanceProductConfig> = settingsProperties
            .balanceProductConfig!!
            .filter { StringUtils.isNotEmpty(it) }
            .map { BalanceProductConfig(it) }

        val timeBucketSystemCustom = TimeBucketSystemCustom(
            tbsType = settingsProperties.timeBucketSystemType,
            tbsCodes = listOf("0D-$MARK", "$MARK-INF")
        )

        val settings = StartCalculationSettings(
            balanceProductConfigs = balanceProductConfigs,
            dealAttributes = settingsProperties.dealAttributes,
            reportAttributes = settingsProperties.reportAttributes,
            reportDate = FACT_DATE.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            balanceFlag = settingsProperties.balanceFlag,
            timeBucketSystemCustom = timeBucketSystemCustom
        )
        val tables = CalculationParametersTables(
            macro = mapOf(),
            transferRates = mapOf(),
            products = mapOf()
        )
        val calculationStartRequest = StartCalculationCommand(
            solveId = calculationUid,
            settings = settings,
            calculationParametersTables = tables
        )

        wireMock.stubFor(
            post("/api/v1/drp/cash-flow/start-calc")
                .withRequestBody(equalTo(objectMapper.writeValueAsString(calculationStartRequest)))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .willReturn(ok().withBody(objectMapper.writeValueAsString(UUID.randomUUID()))
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)))
    }

    private fun prepareStubForDrpLoadResult(
        calculationUid: UUID,
        reportTableName: String,
        reverseEtlProperties: ReverseEtlProperties,
    ) {
        val request = LoadCalculationResultCommand(
            solveId = calculationUid,
            aggregationReportTableName = reportTableName,
            targetReportTableName = "${reverseEtlProperties.database}.${reverseEtlProperties.schema}.${reverseEtlProperties.table}"
        )

        wireMock.stubFor(
            post("/api/v1/drp/cash-flow/load-calc-result")
                .withRequestBody(equalTo(objectMapper.writeValueAsString(request)))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .willReturn(ok().withBody(objectMapper.writeValueAsString(UUID.randomUUID()))
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)))
    }

    private fun prepareStubForBalanceMigrationFromStage(calculationUid: UUID) {
        val uri = fromPath("/api/v1/balance/migration")
            .queryParam("solveId", calculationUid)
            .build()
            .toUriString()
        wireMock.stubFor(post(uri)
            .willReturn(ok().withBody(objectMapper.writeValueAsString(UUID.randomUUID()))
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)))
    }

    companion object {
        private val CALCULATION_UID = UUID.fromString("07dabafa-529d-4da4-bab5-a6359313c064")
        private val PRODUCT_SCENARIO_UID = UUID.fromString("639cb402-3ae4-4ff4-ab1a-d70eaa661334")
        private val MACRO_UID = UUID.fromString("639cb402-3ae4-4ff4-ab1a-d70eaa661334")
        private val TRANSFER_RATE_UID = UUID.fromString("639cb402-3ae4-4ff4-ab1a-d70eaa661334")

        private val START_DATE = LocalDate.of(2022, Month.NOVEMBER, 1)
        private val FACT_DATE = LocalDate.of(2022, Month.OCTOBER, 31)

        private const val DATABASE = "cashflow"
        private const val SCHEMA = "public"
        private const val TABLE = "data"
        private const val MARK = "1M"

        @JvmStatic
        @RegisterExtension
        private var wireMock = WireMockExtension
            .newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build()

        @JvmStatic
        @DynamicPropertySource
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("services.macro-scenario-url") { "http://localhost:${wireMock.runtimeInfo.httpPort}" }
            registry.add("services.transfer-rate-url") { "http://localhost:${wireMock.runtimeInfo.httpPort}" }
            registry.add("services.product-scenario-url") { "http://localhost:${wireMock.runtimeInfo.httpPort}" }
            registry.add("services.drp-command-url") { "http://localhost:${wireMock.runtimeInfo.httpPort}" }
            registry.add("services.balance-result-url") { "http://localhost:${wireMock.runtimeInfo.httpPort}" }
        }
    }
}