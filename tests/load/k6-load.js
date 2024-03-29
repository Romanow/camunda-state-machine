import {randomString, uuidv4} from "https://jslib.k6.io/k6-utils/1.4.0/index.js";
import {describe, expect} from "https://jslib.k6.io/k6chaijs/4.3.4.2/index.js";
import http from "k6/http";

export const options = {
    stages: [
        {duration: "20s", target: 10},
        {duration: "20s", target: 20},
        {duration: "60s", target: 50},
        {duration: "10s", target: 10},
    ],
};

export default function () {
    const hostname = `${__ENV.HOSTNAME}`
    const params = {
        headers: {
            "Content-Type": "application/json"
        }
    };
    let calculationUid = null;

    // =====================================================
    // =============== Create Calculation ==================
    // =====================================================
    describe("Create Calculation", () => {
        // given
        const body = {
            "name": randomString(8),
            "startDate": "01-10-2022",
            "macroUid": uuidv4(),
            "transferRateUid": uuidv4(),
            "productScenarioUid": uuidv4(),
            "periods": [
                {
                    "startDate": "01-10-2022",
                    "endDate": "01-12-2022",
                    "mark": "M1",
                    "serialNumber": 1
                }
            ]
        };

        // when
        const url = `http://${hostname}/api/v1/cashflow/calculation`;
        const response = http.post(url, JSON.stringify(body), params);

        // then
        expect(response.status, "response status").to.eq(200);
        expect(response).to.have.validJsonBody();
        calculationUid = response.json().uid;
    });

    // =====================================================
    // ================= ETL Completed =====================
    // =====================================================
    describe("ETL completed", () => {
        // given
        const body = {
            "solveId": `${calculationUid}`,
            "status": "UPLOADED",
            "calculationParametersTables": {}
        };

        // when
        const url = `http://${hostname}/api/v1/cashflow/calculation/${calculationUid}/answer-from-drp`;
        const response = http.post(url, JSON.stringify(body), params);

        // then
        expect(response.status, "response status").to.eq(200);
        expect(response).to.have.validJsonBody();
    })

    // =====================================================
    // ============== Calculation Completed ================
    // =====================================================
    describe("Calculation completed", () => {
        // given
        const body = {
            "solveId": `${calculationUid}`,
            "status": "SUCCESS",
            "aggReportTableName": "data",
            "calculationParametersTables": {}
        }

        // when
        let url = `http://${hostname}/api/v1/cashflow/calculation/${calculationUid}/answer-from-drp`;
        const response = http.post(url, JSON.stringify(body), params);

        // then
        expect(response.status, "response status").to.eq(200)
        expect(response).to.have.validJsonBody()
    })

    // =====================================================
    // ============== Reverse ETL Completed ================
    // =====================================================
    describe("Reverse ETL completed", () => {
        // given
        const body = {
            "solveId": `${calculationUid}`,
            "status": "UPLOAD_FINISHED",
            "aggReportTableName": "data",
            "calculationParametersTables": {}
        }

        // when
        const url = `http://${hostname}/api/v1/cashflow/calculation/${calculationUid}/answer-from-drp`;
        const response = http.post(url, JSON.stringify(body), params);

        // then
        expect(response.status, "response status").to.eq(200)
        expect(response).to.have.validJsonBody()
    })
};