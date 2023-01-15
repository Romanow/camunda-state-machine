# State Machine using Camunda

[![Build project](https://github.com/Romanow/camunda-state-machine/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Romanow/camunda-state-machine/actions/workflows/build.yml)

### Разработка

1. Camunda не предоставляет возможности получить ответ об успешном завершении action, отправка события всегда
   возвращает `200 ОК`, а статус расчета возвращается в body.
2. Идентификатор расчета `calculationUid` связывается с расчетом как `businessKey`.
   ```kotlin
   val instance = processEngine
       .runtimeService
       .createProcessInstanceQuery()
       .processInstanceBusinessKey(calculationUid.toString())
       .active()
       .singleResult()
       .processInstanceId
   ```
3. Для изменения статуса расчета в событиях и действиях явным образом
   прописан [Execution Listener](src/main/java/ru/romanow/camunda/service/ProcessListener.kt), который принимает как
   параметр новый статус расчета.
4. Так же там передается маркировочный параметр `needOperationUid`, он нужен, чтобы для определенных действий записывать
   в статус `opearionUid`, само значение параметра `operationUid` берется из переменных. Так было реализовано потому что
   переменные принадлежит всему процессу, следовательно, если в одном состоянии задать переменную, то она будет видна
   для всех последующих состояний. Локальные переменные видны внутри процесса (subprocess).
5. Для мокирования внешних запросов используется [WireMock](https://wiremock.org/) и [stubs](stubs/mappings/stubs.json).

BPMN схема [Cash Flow](src/main/resources/bpmn/CashFlowProcess.bpmn).

Получить последний статус расчета.

```sql
SELECT c.uid           AS uid
     , c.name          AS name
     , cs.status       AS status
     , cs.created_date AS status_date
FROM calculation c
    JOIN LATERAL (SELECT *
                  FROM calculation_status cs
                  WHERE cs.calculation_id = c.id
                  ORDER BY cs.created_date DESC
                  LIMIT 1) cs
         ON TRUE;
```

### Правила именования объектов

Для именования заданий нужно применять объектно-ориентированный шаблон _глагол_ + _объект_. Правильное название
задания _приобрести продукты_, а не _сначала позаботиться о покупке продуктов_.

События относятся к чему-то, что уже случилось независимо от процесса (если перехыватываем события) или как результат
выполнения процесса (если он генерирует события). Поэтому в именовании объектов используется шаблон _объект_ + _глагол в
пассиве_, например, _почувствовать голод_.

## Локальная установка и запуск

```shell
$ ./gradlew clean build

$ docker compose up postgres wiremock -d

$ ./gradlew bootRun --args='--spring.profiles.active=local'  
```

Запускаем end-to-end тесты (папка [`tests/postman`](tests/postman)):

```shell
$ newman run -e local-environment.json collection.json
```

Админка доступна по
адресу [http://localhost:8080/camunda/app/cockpit/default/#/](http://localhost:8080/camunda/app/cockpit/default/#/),
пользователь `admin`:`admin`.

Для управления расчетом через REST API можно использовать [коллекцию](tests/postman/collection.json) postman
и [переменные](tests/postman/local-environment.json).

## Нагрузочное тестирование

Запускаем локальный кластер k8s в docker (папка [`k8s`](k8s)):

```shell
$ kind create cluster --config kind.yml
$ kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
```

Загружаем docker images в кластер `kind` (папка [`scripts`](scripts)):

```shell
$ ./scripts/load-images.sh
```

Загружаем репозитории:

```shell
$ helm repo add romanow https://romanow.github.io/helm-charts/

$ helm repo add prometheus-community https://prometheus-community.github.io/helm-charts

$ helm repo update
```

Устанавливаем инструменты для мониторинга кластера:

```shell
$ helm install kube-state-metrics prometheus-community/kube-state-metrics --set image.tag=v2.6.0 

$ helm install prometheus -f prometheus/values.yaml romanow/prometheus

$ helm install influxdb -f influxdb/values.yaml romanow/influxdb

$ helm install grafana -f grafana/values.yaml romanow/grafana
```

Устанавливаем Camunda State Machine:

```shell
$ helm install postgres -f postgres/values.yaml romanow/postgres

$ helm install camunda-state-machine -f camunda/values.yaml romanow/java-service
```

Запускаем end-to-end тесты (папка [`tests/postman`](tests/postman)):

```shell
$ newman run -e kind-environment.json collection.json
```

Запускаем нагрузочное тестирование (папка [`tests/load`](tests/load)):

```shell
$ brew install k6
$ k6 run \
    --out influxdb=http://localhost:32086/k6 \
    -e HOSTNAME=localhost:32080 \
    k6-load.js
```

### Технические вопросы

* [ ] Админка и управление расчетом (Observability, Grafana).

## Ссылки

1. [Camunda User Guide](https://docs.camunda.org/manual/latest/user-guide/)
2. [BPMN overview](https://camunda.com/bpmn/reference/)
3. [Process Variables](https://docs.camunda.org/manual/7.16/user-guide/process-engine/variables/)
4. [Delegation Code](https://docs.camunda.org/manual/7.16/user-guide/process-engine/delegation-code/)
5. [Error Handling](https://docs.camunda.org/manual/7.16/user-guide/process-engine/error-handling/)