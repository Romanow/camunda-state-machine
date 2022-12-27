# State Machine using Camunda

[![Build project](https://github.com/Romanow/camunda-state-machine/actions/workflows/build.yml/badge.svg)](https://github.com/Romanow/camunda-state-machine/actions/workflows/build.yml)

### Разработка

Получить список статусов по всем расчетам.

```sql
SELECT c.uid                AS uid
     , c.name               AS name
     , ARRAY_AGG(cs.status) AS statuses
FROM calculation_status cs
    INNER JOIN calculation c ON c.id = cs.calculation_id
GROUP BY c.uid, c.name;
```

### Архитектурные вопросы

* [ ] Потребление памяти.
* [ ] Админка и управление расчетом (Observability, Grafana).

## Установка и запуск

Требуется:

* OpenJDK 11
* Docker
* Postman

```shell
$ ./gradlew clean build
$ docker compose up -d
$ ./gradlew bootRun --args='--spring.profiles.active=local'
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