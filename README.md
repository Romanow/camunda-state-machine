# State Machine using Camunda

[![Build project](https://github.com/Romanow/camunda-state-machine/actions/workflows/build.yml/badge.svg)](https://github.com/Romanow/camunda-state-machine/actions/workflows/build.yml)

### Разработка

1. Заводим `Calculation`, `CalculationStatus`, создаем API для создания `Calculation`
   ```http request
   POST /api/v1/cashflow/calculation
   ```
2. После создания в БД `Calculation` запускам Camunda, в variables прописываем `calculationUid`.
3. В BPMN при старте описываем `variables`:
    * `calculationVersionUid` – по ней будем искать `processInstanceId`;
4. Создаем actions:
    1. `CopyDataToStagedAction` (ID: `CopyDataToStageAction`) – вызов к сервисам _MacroScenarioService_,
       _TransferRateService_, _ProductScenarioService_.

       Условие: безусловный переход.

       Входные параметры:
        * `macroUid`;
        * `transferRateUid`;
        * `productScenarioUid`.

       Выходные параметры:
        * `macroTables`;
        * `transferRateTables`;
        * `productScenarioTables`.

       По окончанию операции переход к `UploadCalculationParametersAction`.
    2. `UploadCalculationParametersAction` (ID: `StartEtlAction`) – вызов _DrpCommand_ для запуска ETL процесса для
       отправки данных в DRP.

       Условие: безусловный переход.

       Входные параметры:
        * `macroTables`;
        * `transferRateTables`;
        * `productScenarioTables`.

       Выходные параметры:
        * `operationUid` – UID операции в DRP.

       По окончанию операции ждем внешнего события от DRP, запрос:
       ```http request
       POST /api/v1/cashflow/calculation/answer-from-drp
       ```
    3. `StartCalculationAction` (ID: `EtlCompletedAction`) – вызов _DrpCommand_ для запуска расчета.

       Условие: событие `etl result event`.

       Входные параметры:
        * `airflowResponse` – ответ от DRP.

       Выходные параметры:
        * `operationUid` – UID операции в DRP.

       По окончанию операции ждем внешнего события от DRP, запрос:
        ```http request
        POST /api/v1/cashflow/calculation/answer-from-drp
        ```
    4. `FinishCalculationAction` – окончание расчета в DRP, вызов _DrpCommand_ для запуска обратного ETL процесса.

       Условие: событие `calculation result event`.

       Входные параметры:
        * `airflowResponse` – ответ от DRP.

       Выходные параметры:
        * `operationUid` – UID операции в DRP.

       По окончанию операции ждем внешнего события от DRP, запрос:
       ```http request
       POST /api/v1/cashflow/calculation/answer-from-drp
       ```
    5. `ResultUploadedAction` – окончание обратного ETL процесса, запрос к _BalanceResultHolder_ для перекладки данных
       из `staged` в `public`.

       Условие: событие `reverse etl result event`.

       Входные параметры:
        * `airflowResponse` – ответ от DRP.

       Выходные параметры:
        * `operationUid` – UID операции в DRP.

       Завершение процесса.
    6. `ErrorAction` (ID: `ErrorAction`) – записываем статус `CALCULATION_ERROR` переводим процесс в конечное состояние.

       Условие: событие `calculation error`.
5. Описываем внешние события:
    * `etl result event` – завершение ETL процесса.
    * `calculation result event` – завершение расчета.
    * `reverse etl result event` – завершение ETL процесса.
    * `calculation error` – глобальная ошибка расчета.

### Архитектурные вопросы

* [ ] Потребление памяти.
* [ ] Админка и управление расчетом (Observability, Grafana (?)).

## Установка и запуск

Требуется:

* OpenJDK 11
* Docker
* Postman

```shell
# сборка
$ ./gradlew clean build
# запуск PostgreSQL 15 в docker
$ docker compose up -d
# запуск приложения 
$ ./gradlew bootRun
```

Админка доступна по
адресу [http://localhost:8080/camunda/app/cockpit/default/#/](http://localhost:8080/camunda/app/cockpit/default/#/),
пользователь `admin`:`admin`.

Для управления расчетом через REST API можно использовать [коллекцию](postman/collection.json) postman
и [переменные](postman/environment.json).

## Техническое описание

When naming tasks, we try to adhere to the object-oriented design principle of using the `verb` + `object` pattern. We
would say "acquire groceries," for example, not "first take care of shopping for groceries."

Events refer to something that has already happened regardless of the process (if they are catching events) or as a
result of the process (if they are throwing events). For this reason, we use the `object` and make the `verb` passive in
voice, so we write "hunger noticed".

## Ссылки

1. [Camunda User Guide](https://docs.camunda.org/manual/latest/user-guide/)
2. [BPMN overview](https://camunda.com/bpmn/reference/)