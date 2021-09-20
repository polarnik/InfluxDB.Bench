---
marp: true
title: Из слона (PostgreSQL) муху (баги)
description: Мониторинг, анализ и оптимизация SQL-запросов
theme: vtb
template: bespoke
paginate: true
_paginate: false

---

<!-- _class: lead12
_footer: 'Cсылка на [слайды](https://polarnik.github.io/InfluxDB.Bench/), ссылка [на бенчмарк](https://github.com/polarnik/InfluxDB.Bench)'
-->

# Как ускорить запросы к InfluxDB

## __Смирнов Вячеслав, 2021__

<!--

-->

---

<!-- _class: title-->

# Тестирую и ускоряю ДБО для юридических лиц в банке ВТБ
## __Развиваю @qa_load__

![bg cover](img/omsk.jpg)

<!-- 
Повышаю качество более десяти лет. Занимаюсь системой дистанционного банковского обслуживания юридических лиц. Основной профиль моей работы — тестирование производительности. Развиваю сообщество инженеров по тестированию производительности, помогая коллегам в telegram чате «QA — Load & Performance».
-->

---
<!-- _class: main
-->

# Об ускорении InfluxDB глазами инженера по нагрузке. О Continuous Queries и подготовке данных для запросов



---
<!-- class: head2 -->

# Содержание

1) Когда оптимизация InfluxDB важна
2) Ресурсы для InfluxDB и их мониторинг
3) Ограничение интенсивности и параллельности запросов
5) Замер длительности запроса из Grafana в InfluxDB
3) Разделение баз данных по серверам
2) Уменьшение Shared Size для экономии оперативной памяти
3) Изменение типа индекса с memory на tsi1
6) Удаление значений тегов с низкой селективностью
4) Увеличение гранулярности хранения метрик
5) Подготовка данных для ответа с помощью Continuous Queries
5) Быстрые фильтры по тегам: метаданные и данные
5) InfluxQL и Flux
5) InfluxDB v1, InfluxDB v2 и VictoriaMetrics
5) Итоги

---
<!-- _class: main
-->

# Когда оптимизация InfluxDB важна

---

# Большое количество тестов производительности

## __JMeter, Gatling, Perfomance Center, Tank__

## __Плюс мониторинг в InfluxDB__

**_![  ](img/tools.png)_**

---

# Тяжелые отчеты и конкурентные запросы

**_![  ](img/reports.png)_**

---

# Нехватка памяти для InfluxDB и перезапуски

**_![  ](img/memory.limit.png)_**

---
<!-- _class: main
-->

# Ресурсы для InfluxDB и их мониторинг

---

# Оперативная память сервера

**_![  ](img/memory.limit.png)_**

---

# Количество конкурентных запросов

**_![  ](img/influxdb.requests.png)_**

---

# Размеры шард

**_![  ](img/influxdb.requests.png)_**

---

# Настроенный мониторинг (доска Grafana)

**_![  ](img/influxdb.requests.png)_**


---
<!-- _class: main
-->

# Ограничение интенсивности и параллельности запросов в [coordinator]

---

# Оцениваем предельное количество запросов

**_![  ](img/influxdb.requests.png)_**

---

# Ограничиваем coordinator/max-concurrent-queries

```yaml
[coordinator]
  # The maximum number of concurrent queries 
  # allowed to be executing at one time.  
  # If a query is executed and exceeds this limit, 
  # an error is returned to the caller.  
  # This limit can be disabled by setting it to 0.
  max-concurrent-queries = 5
```

---

<!-- _class: error
-->

# Ошибка при достижении max-concurrent-queries

**_![  ](img/max-concurrent-queries.error.png)_**


---

# Оцениваем предельную длительность запросов

**_![  ](img/query-timeout.png)_**

---

# Ограничиваем coordinator/query-timeout

```yaml
[coordinator]
  # The maximum time a query will is allowed 
  # to execute before being killed by the system.  
  # This limit can help prevent run away queries.  
  # Setting the value to 0 disables the limit.
  query-timeout = "90s"
```

---

<!-- _class: error
-->

# Ошибка при достижении query-timeout

**_![  ](img/query-timeout.error.png)_**


---

# Оцениваем предельное количество тегов ответа

**_![  ](img/influxdb.series.png)_**


---

# Ограничиваем coordinator/max-select-series

```yaml
[coordinator]
  # The maximum number of series a SELECT can run. 
  # A value of 0 will make the maximum series
  # count unlimited.
  
  # max-select-series = 0
  max-select-series = 1000
```

---

<!-- _class: error
-->

# Ошибка при достижении max-select-series

**_![  ](img/max-select-series.error.png)_**

---

# Оцениваем предел группировки по времени

**_![  ](img/influxdb.series.png)_**

---

# Ограничиваем coordinator/max-select-buckets

```yaml
[coordinator]
  # The maxium number of group by 
  # time bucket a SELECT can create.
  # A value of zero will max the maximum 
  # number of buckets unlimited.
  
  # max-select-buckets = 0
  max-select-buckets = 1900
```
---

<!-- _class: error
-->

# Ошибка при достижении max-select-buckets

**_![  ](img/max-select-buckets.error.png)_**

---

# Оцениваем предельное количество точек ответа

**_![  ](img/influxdb.requests.png)_**

Предельное значение:

```
Группы по тегам x 
Группы по времени x
Количество полей
```

---

# Количество точек ответа и размер ответа

**_![  ](img/influxdb.requests.png)_**

---

# Количество точек ответа и исходящий трафик

**_![  ](img/influxdb.requests.png)_**


---

# Ограничиваем coordinator/max-select-point

```yaml
[coordinator]
  # The maximum number of points a SELECT can process.  
  # A value of 0 will make the maximum point count unlimited.  
  # This will only be checked every second so queries will not
  # be aborted immediately when hitting the limit.
  
  # max-select-point = 0
  max-select-point = 20000
```

---

<!-- _class: error
-->

# Ошибка при достижении max-select-point

**_![  ](img/max-select-point.error.png)_**

---
<!-- _class: main
-->

# Замер длительности запроса из Grafana в InfluxDB

---

# Замер средней длительности

**_![  ](img/influxdb.requests.png)_**

---

# Логирование медленных запросов

```yaml
[coordinator]
  # The time threshold when a query will be 
  # logged as a slow query.  
  # This limit can be set to help
  # discover slow or resource intensive queries.  
  # Setting the value to 0 disables the slow query logging.
  log-queries-after = "5s"
```

---

# Замер длительности конкретных запросов

## __По HAR-логу с POST-запросами__

**_![  ](img/influxdb.requests.png)_**

---

# Анализ плана запроса к InfluxDB


---
<!-- _class: main
-->

# Разделение баз данных по серверам

---
<!-- _class: main
-->

# Уменьшение Shared Size для экономии оперативной памяти

---
<!-- _class: main
-->

# Изменение типа индекса с memory на tsi1

---
<!-- _class: main
-->

# Удаление значений тегов с низкой селективностью

---
<!-- _class: main
-->

# Увеличение гранулярности хранения метрик


---
<!-- _class: main
-->

# Подготовка данных для ответа с помощью Continuous Queries

---
<!-- _class: main
-->

# Быстрые фильтры по тегам: метаданные и данные

---
<!-- _class: main
-->

# InfluxQL и Flux

---
<!-- _class: main
-->

# InfluxDB v1, InfluxDB v2 и VictoriaMetrics

---
<!-- _class: main
-->

# Итоги


---

<!-- _class: lead12
_footer: 'Cсылка на [слайды](https://polarnik.github.io/InfluxDB.Bench/), ссылка [на бенчмарк](https://github.com/polarnik/InfluxDB.Bench)'
-->

# Вопросы/ответы<br> Как ускорить запросы к InfluxDB

## __Смирнов Вячеслав, @qa_load__


