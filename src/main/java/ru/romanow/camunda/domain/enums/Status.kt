package ru.romanow.camunda.domain.enums

enum class Status {
    CALCULATION_STARTED,        // CashFlowCalculationStart
    DATA_COPIED_TO_STAGED,      // CopyDataToStageAction

    ETL_START,                  // StartEtlProcess
    ETL_SENT_TO_DRP,            // StartEtlAction
    ETL_COMPLETED,              // FinishEtlProcess

    CALCULATION_START,          // StartCalculationProcess
    CALCULATION_SENT_TO_DRP,    // StartCalculationAction
    CALCULATION_COMPLETED,      // FinishCalculationProcess

    REVERSED_ETL_START,         // StartReverseEtlProcess
    REVERSED_ETL_SENT_TO_DRP,   // StartReverseEtlAction
    REVERSED_ETL_COMPLETED,     // FinishReverseEtlProcess

    DATA_COPIED_FROM_STAGED,    // CopyDataFromStageAction

    CALCULATION_FINISHED,       // CashFlowCalculationFinish

    CALCULATION_ERROR,          // ErrorAction
}