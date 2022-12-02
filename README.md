# State Machine using Camunda

[![Build project](https://github.com/Romanow/camunda-state-machine/actions/workflows/build.yml/badge.svg)](https://github.com/Romanow/camunda-state-machine/actions/workflows/build.yml)

Технические вопросы:

* [ ] Сохранение статуса выполнения в БД (Process Listener для обновления CalculationStatus).
* [x] Передача параметров для старта и продолжения расчета.
* [x] Параллельные расчеты.
* [ ] Таймауты, внешние события.
* [x] Возобновление Manual Process через API.

Архитектурные вопросы:

* [ ] Потребление памяти.
* [ ] Админка и управление расчетом (Observability, Grafana (?)).
* [ ] Выделение общих функциональных блоков.
* [ ] Параллелизация выполнения BPMN.

Админка доступна по
адресу [http://localhost:8080/camunda/app/cockpit/default/#/](http://localhost:8080/camunda/app/cockpit/default/#/),
пользователь `admin`:`admin`.

Для управления расчетом через REST API можно использовать [коллекцию](postman/collection.json) postman
и [переменные](postman/environment.json).

## Ссылки

1. [Camunda User Guide](https://docs.camunda.org/manual/latest/user-guide/)
2. [BPMN overview](https://camunda.com/bpmn/reference/)