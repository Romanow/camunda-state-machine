# Перевод расчета Cash Flow на Camunda

## Цель

Эта задача является первым шагом по переходу от Spring State Machine на BPMN.

Причины:

* Т.к. задача СУБО – автоматизация бизнес-процессов, нотация BPMN для этого подходит лучше и имеет больший функционал.
* Доработка State Machine Engine (существующее решение) на основе Spring State Machine является не целесообразным, т.к.
  требует выделения ресурсов на поддержку и развитие решения.
* Camunda является распространенным решением для автоматизации бизнес процессов, следовательно, оно более надежное.
* Camunda имеет хорошую документацию.

В [текущем проекте](https://github.com/Romanow/camunda-state-machine) реализована Camunda как движок расчета Cash Flow.

BPMN схема [Cash Flow](src/main/resources/bpmn/CashFlowProcess.bpmn).

### Особенности реализации

Проект реализован на Kotlin для ускорения разработки, реализованы интеграционные тесты в
коде ([test](src/test/java/ru/romanow/camunda)) и end-to-end сценарий с мокированием внешних вызовов
через [WireMock](stubs/mappings/stubs.json).

Для ускорения разработки была убрана сущности `CalculationVersion`, и в роли UID расчета
использовался `calculationUid` (в Camunda он связан со внутренним ID как `businessKey`).

### Запуск проекта и прогон тестов

```shell
$ ./gradlew clean build

$ docker compose up postgres wiremock -d

$ ./gradlew bootRun --args='--spring.profiles.active=local'

$ newman run -e postman/local-environment.json postman/collection.json
```

### Шаги реализации в calm-core

1. Добавить в проект зависимости:
    * group: `org.camunda.bpm.springboot`, module: `camunda-bpm-spring-boot-starter-webapp`, version: `7.16.0`
    * group: `org.camunda.bpm.springboot`, module: `camunda-bpm-spring-boot-starter-rest`, version: `7.16.0`
2. Добавить [BPMN диаграмму](src/main/resources/bpmn/CashFlowProcess.bpmn) в соответствующую
   папку `src/main/resources/bpmn`.
3. При реализации прототипа были скопированы action (и для скорости и понятности конвертированы в Kotlin), но сама
   бизнес-логика в них не менялась:
    * `CopyDataToStagedAction`
      -> [`CopyDataToStageAction`](src/main/java/ru/romanow/camunda/actions/CopyDataToStageAction.kt)
    * `UploadCalculationParametersAction`
      -> [`StartEtlAction`](src/main/java/ru/romanow/camunda/actions/StartEtlAction.kt)
    * `StartCalculationAction`
      -> [`StartCalculationAction`](src/main/java/ru/romanow/camunda/actions/StartCalculationAction.kt)
    * `FinishCalculationAction`
      -> [`StartReverseEtlAction`](src/main/java/ru/romanow/camunda/actions/StartReverseEtlAction.kt)
    * `ResultUploadedAction`
      -> [`CopyDataFromStageAction`](src/main/java/ru/romanow/camunda/actions/CopyDataFromStageAction.kt)
    * `ErrorAction` -> [`ErrorAction`](src/main/java/ru/romanow/camunda/actions/ErrorAction.kt)
4. Удалить `CashFlowStateMachine` и связанные с ним сущности.

### Критерии приема

1. Написаны интеграционные тесты (по
   примеру [CashFlowProcessTest](src/test/java/ru/romanow/camunda/cashflow/CashFlowProcessTest.kt)) и актуализированы
   контрактные (если будут слиты в dev).
2. В рамках задачи мы меняем движок перехода между состояниями, но _не затрагиваем_ сам расчет Cash Flow, значит он
   должен
   работать так же, как при использовании Spring State Machine.
3. Возможные изменения:
    * Для BPMN CashFlow были убраны часть ненужных статусов (
      см. [Status](src/main/java/ru/romanow/camunda/domain/enums/Status.kt)).
    * Убрать наследование `CommonAction`, т.к. метод `sendEvent` больше не требуется.