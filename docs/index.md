---
marp: true
title: Как ускорить запросы к InfluxDB
description: Как ускорить запросы к InfluxDB с помощью InfluxQL Continuous Queries и разделения данных 
theme: tdconf
template: bespoke
paginate: true
_paginate: false

---

<!-- _class: lead
-->

# Как ускорить запросы к InfluxDB с помощью InfluxQL Continuous Queries и разделения данных

## Смирнов Вячеслав

### Miro 

<!--

-->

---
<!-- _class: title -->

# Ускоряю и тестирую инфраструктурные сервисы в Miro
## Развиваю сообщество нагрузочников
### @qa_load

![bg cover](img/omsk.jpg)

<!-- 
Повышаю качество более десяти лет. Занимаюсь системой дистанционного банковского обслуживания юридических лиц. Основной профиль моей работы — тестирование производительности. Развиваю сообщество инженеров по тестированию производительности, помогая коллегам в telegram чате «QA — Load & Performance».
-->

---
<!-- _class: main
-->

# Об ускорении __InfluxDB__ 
# глазами инженера по нагрузке. 

# О __Continuous Queries__ и 
# подготовке данных для запросов


---
# Содержание

1) ⁉️ Когда оптимизация __InfluxDB__ важна
2) 📊 Мониторинг производительности __InfluxDB__
3) ⚙️ Разделение данных на разные базы и серверы __InfluxDB__
3) ⚙️ Конфигурирование сервера __InfluxDB__
3) ⚙️ Кеширование ответов на запросы от __Grafana__ к __InfluxDB__ с __NGINX__
3) ⚙️ «Архивирование» медленной базы __InfluxDB__ в __Grafana__
3) 🛠️ Сокращение количества и ускорение фильтров по тегам в __Grafana__
3) 🛠️ Удаление значений тегов с низкой селективностью из __InfluxDB__
4) ⚙️ Увеличение интервала сохранения метрик из __JMeter__, __Gatling__, __Telegraf__, ...
5) 🔬 Замер длительности ответа на запрос из __Grafana__ в __InfluxDB__
5) 🚀 Подготовка данных для ответа с помощью __Continuous Queries__
5) ‼️ Сокращение количества __Continuous Queries__
5) 🛠️ Переход с __InfluxQL__ на __Flux__
5) ⚙️ Смена БД __InfluxDB__ v1.8, __InfluxDB__ v2, __VictoriaMetrics__ или __ClickHouse__


---
# __1.__ ⁉️ Когда оптимизация __InfluxDB__ важна

- Большой продукт, как __Miro__

- Вы отвечаете за инфраструктуру тестирования
- Много тестов производительности с __k6__, __JMeter__, __Gatling__, __Yandex.Tank__, ...
- Хранятся данные за годы и строятся тренды
- И вы хотите ускорить __InfluxDB__ OSS v1.8.10 или __InfluxDB__ OSS v2.2.0
- Оптимизация записи метрик в __InfluxDB__ c __Telegraf__ v1.22.4
- Оптимизация выборки метрик c __InfluxDB__ и __Grafana__ OSS v8.5.4
- Оптимизация аггрегации метрик c __Continuous Queries__

---

# Большой продукт, как __Miro__

![bg cover](img/miro.png)

---

# Вы отвечаете за инфраструктуру тестирования

![bg height:75%](img/infra.team.png)

---

# Много тестов производительности с __k6__, __JMeter__, __Gatling__, __Yandex.Tank__, ...

![bg height:75%](img/infra.tools.png)

---

# Хранятся данные за годы и строятся тренды

![bg 70%](img/trend.png)

---

# Вы хотите ускорить __InfluxDB__ OSS v1.8 или v2.2

![bg 100%](img/influxdb.versions.png)

---
# Одновременная запись метрик может тормозить

![bg 90%](img/wo.telegraf.png)

---
# Оптимизация записи метрик в __InfluxDB__ c __Telegraf__

![bg 90%](img/telegraf.for.write.png)


---
# Оптимизация выборки c __InfluxDB__ и __Grafana__ OSS

![bg 75%](img/grafana.optimisation.png)


---
# Аггрегировать метрики на лету сложно

![bg 80%](img/Complex.query.png)

---
# Предрасчитаем результат с __Continuous Queries__

![bg 80%](img/Continuous.query.png)


---
<!-- _class: invert -->
# __1.__ ⁉️ Когда оптимизация __InfluxDB__ важна

- Большой продукт, как __Miro__

- Вы отвечаете за инфраструктуру тестирования
- Много тестов производительности с __k6__, __JMeter__, __Gatling__, __Yandex.Tank__, ...
- Хранятся данные за годы и строятся тренды
- И вы хотите ускорить __InfluxDB__ OSS v1.8.10 или __InfluxDB__ OSS v2.2.0
- Оптимизация записи метрик в __InfluxDB__ c __Telegraf__ v1.22.4
- Оптимизация выборки метрик c __InfluxDB__ и __Grafana__ OSS v8.5.4
- Оптимизация аггрегации метрик c __Continuous Queries__


---
# __2.__ 📊 Мониторинг производительности __InfluxDB__

- Мониторинг общесистемных метрик

- Мониторинг метрик процесса __InfluxDB__
  - Нехватка памяти для __InfluxDB__ и перезапуски
- Мониторинг внутренних метрик __InfluxDB__ из базы данных **__internal**
  - Размеры шард __InfluxDB__
  - Количество конкурентных запросов к __InfluxDB__
- Мониторинг логов __InfluxDB__ (тяжелые запросы)
- Мониторинг логов __Grafana__ (тяжелые отчеты)

---
# Мониторинг общесистемных метрик

---
# Мониторинг метрик процесса __InfluxDB__

---
# Нехватка памяти для __InfluxDB__ и перезапуски

---
# Мониторинг внутренних метрик __InfluxDB__ из базы данных **__internal**

---
# Размеры шард __InfluxDB__

---
# Количество конкурентных запросов к __InfluxDB__

---
# Мониторинг логов __InfluxDB__ (тяжелые запросы)

---
# Мониторинг логов __Grafana__ (тяжелые отчеты)

---
# Проект __influxdb-bench__ на __github__, где настроен мониторинг

## `https://github.com/polarnik/influxdb-bench`

---
<!-- _class: invert -->
# __2.__ 📊 Мониторинг производительности __InfluxDB__

- Мониторинг общесистемных метрик

- Мониторинг метрик процесса __InfluxDB__
  - Нехватка памяти для __InfluxDB__ и перезапуски
- Мониторинг внутренних метрик __InfluxDB__ из базы данных **__internal**
  - Размеры шард __InfluxDB__
  - Количество конкурентных запросов к __InfluxDB__
- Мониторинг логов __InfluxDB__ (тяжелые запросы)
- Мониторинг логов __Grafana__ (тяжелые отчеты)



---
# __3.__ ⚙️ Разделение данных на разные базы и серверы __InfluxDB__

- Разные базы данных для команд и стендов

- Разные серверы для команд и стендов
- Разные серверы для архивных и новых данных
- Построение трендов по метрикам из одной базы данных
- Использование отдельной базы данных для хранения общей статистики


---
# __Разные базы данных__ для команд и стендов

![bg fit](img/big.db.png)



---
# __Разные базы данных__ для команд и стендов

![bg fit](img/big.db.png)

## __Если данные в одной БД__:

- База данных большая
- Нужна фильтрация данных
- Высокий риск повреждения данных



---
# __Разные базы данных__ для команд и стендов

![bg fit](img/big.db.png)

## Если данные в одной БД:

- База данных большая
- Нужна фильтрация данных
- Высокий риск повреждения данных

## __Если данные в разных БД__:

- Бекапы проще
- Фильтрация не нужна
- Сложнее замедлить работу всех БД

---
#  Для __новых__ и архивных данных разные серверы

![bg fit](img/old.new.data.png)

---
# __Два сервиса__ c одной активной БД быстрее одного
![bg fit](img/server.split.png)

---
# __Семь контейнеров__* быстрее одного большого
![bg fit](img/docker.split.png)


---
#  Построение трендов по метрикам из одной базы данных

---
#  Использование отдельной базы данных для хранения общей статистики

---
<!-- _class: invert -->
# __3.__ ⚙️ Разделение данных на разные базы и серверы __InfluxDB__

- Разные базы данных для команд и стендов

- Разные серверы для команд и стендов
- Разные серверы для архивных и новых данных
- Построение трендов по метрикам из одной базы данных
- Использование отдельной базы данных для хранения общей статистики



---
# __4.__ ⚙️ Конфигурирование сервера __InfluxDB__

- Ограничиваем __`max-concurrent-queries`__
  - Ограничиваем __`max_conns`__ в __nginx__ upstream
- Ограничиваем __`query-timeout`__
  - Ограничиваем __`fail_timeout`__ в __nginx__ upstream
- Ограничиваем __`max-select-series`__
  - Ограничиваем __`Max series`__ в __Grafana__ DataSource
- Ограничиваем __`max-select-buckets`__
- Ограчиниваем __`max-select-point`__
  - Ограничиваем __Min time interval__ в __Grafana__ DataSource
  - Ограничиваем __Max data points__ в __Grafana__ Query options
- Изменение типа индекса с __`memory`__ на __`tsi1`__
- Логирование медленных запросов c __`log-queries-after`__


---
<!-- _class: main
-->

# Ограничение интенсивности и параллельности запросов в __`[coordinator]`__

---

# Оцениваем предельное количество запросов

**_![  ](img/influxdb.requests.png)_**

---

# Ограничиваем __max-concurrent-queries__

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
<!-- _class: error -->

# Ошибка при достижении __max-concurrent-queries__

**_![  ](img/max-concurrent-queries.error.png)_**


---
# Ограничиваем __max_conns__ в __nginx__ upstream

---

# Оцениваем предельную длительность запросов

**_![  ](img/query-timeout.png)_**

---

# Ограничиваем __query-timeout__

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

# Ошибка при достижении __query-timeout__

**_![  ](img/query-timeout.error.png)_**


---

# Оцениваем предельное количество тегов ответа

**_![  ](img/influxdb.series.png)_**


---

# Ограничиваем __`max-select-series`__

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

# Ошибка при достижении __`max-select-series`__

**_![  ](img/max-select-series.error.png)_**

---

# Оцениваем предел группировки по времени

**_![  ](img/influxdb.series.png)_**

---

# Ограничиваем __`max-select-buckets`__

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

# Ошибка при достижении __`max-select-buckets`__

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

# Ограничиваем __`max-select-point`__

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

# Ошибка при достижении __`max-select-point`__

**_![  ](img/max-select-point.error.png)_**


---
# Изменение типа индекса с __`memory`__ на __`tsi1`__

---

# Логирование медленных запросов c __`log-queries-after`__

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
<!-- _class: invert -->
# __4.__ ⚙️ Конфигурирование сервера __InfluxDB__

- Ограничиваем __`max-concurrent-queries`__
  - Ограничиваем __`max_conns`__ в __nginx__ upstream
- Ограничиваем __`query-timeout`__
  - Ограничиваем __`fail_timeout`__ в __nginx__ upstream
- Ограничиваем __`max-select-series`__
  - Ограничиваем __`Max series`__ в __Grafana__ DataSource
- Ограничиваем __`max-select-buckets`__
- Ограчиниваем __`max-select-point`__
  - Ограничиваем __Min time interval__ в __Grafana__ DataSource
  - Ограничиваем __Max data points__ в __Grafana__ Query options
- Изменение типа индекса с __`memory`__ на __`tsi1`__
- Логирование медленных запросов c __`log-queries-after`__

---
# __5.__ ⚙️ Кеширование ответов на запросы от __Grafana__ к __InfluxDB__ с __NGINX__

- Кеширование __GET__-запросов в __NGINX__

- Кеширование __POST__-запросов в __NGINX__
- Влияние интервалов времени __Grafana__ на кеширование
- Замена динамического интервала вида __`now()-6h...now()`__ статитическим

---
<!-- _class: invert -->
# __5.__ ⚙️ Кеширование ответов на запросы от __Grafana__ к __InfluxDB__ с __NGINX__

- Кеширование __GET__-запросов в __NGINX__

- Кеширование __POST__-запросов в __NGINX__
- Влияние интервалов времени __Grafana__ на кеширование
- Замена динамического интервала вида __`now()-6h...now()`__ статитическим


---
# __6.__ ⚙️ «Архивирование» медленной базы __InfluxDB__ в __Grafana__

- __Grafana__ как реестр активных баз данных

- Переименование медленной базы данных в __{name}-{from}-{to}__
- Перенос файлов базы данных __{name}-{from}-{to}__ на архивный сервер
- Создание пустой базы данных __{name}-{from}__
- Настройка __Grafana DataSource__ с именем __{name}__ на новую базу данных
- Создание __Grafana DataSource__ для архивной базы данных

---
<!-- _class: invert -->
# __6.__ ⚙️ «Архивирование» медленной базы __InfluxDB__ в __Grafana__

- __Grafana__ как реестр активных баз данных

- Переименование медленной базы данных в __{name}-{from}-{to}__
- Перенос файлов базы данных __{name}-{from}-{to}__ на архивный сервер
- Создание пустой базы данных __{name}-{from}__
- Настройка __Grafana DataSource__ с именем __{name}__ на новую базу данных
- Создание __Grafana DataSource__ для архивной базы данных



---
# __7.__ 🛠️ Сокращение количества и ускорение фильтров по тегам в __Grafana__

---
# __8.__ 🛠️ Удаление значений тегов с низкой селективностью из __InfluxDB__

---
# __9.__ ⚙️ Увеличение интервала сохранения метрик из __JMeter__, __Gatling__, __Telegraf__, ...

---
# __10.__ 🔬 Замер длительности ответа на запрос из __Grafana__ в __InfluxDB__

---
# __11.__ 🚀 Подготовка данных для ответа с помощью __Continuous Queries__

---
# __12.__ ‼️ Сокращение количества __Continuous Queries__

---
# __13.__ 🛠️ Переход с __InfluxQL__ на __Flux__

---
# __14.__ ⚙️ Смена БД __InfluxDB__ v1.8, __InfluxDB__ v2, __VictoriaMetrics__ или __ClickHouse__

---
<!-- class: head2 -->

# Содержание

1) Когда оптимизация __InfluxDB__ важна
2) Ресурсы для __InfluxDB__ и их мониторинг
3) Ограничение интенсивности и параллельности запросов
5) Замер длительности запроса из __Grafana__ в __InfluxDB__
3) Разделение баз данных по серверам
2) Уменьшение __Shared Size__ для экономии оперативной памяти
3) Изменение типа индекса с __memory__ на __tsi1__
6) Удаление значений тегов с низкой селективностью
4) Увеличение гранулярности хранения метрик
5) Подготовка данных для ответа с помощью __Continuous Queries__
5) Быстрые фильтры по тегам: метаданные и данные
5) Переход с __InfluxQL__ на __Flux__
5) Смена БД __InfluxDB__ v1, __InfluxDB__ v2, __VictoriaMetrics__, __ClickHouse__
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

# Замер длительности запроса из Grafana в InfluxDB

---

# Замер средней длительности

**_![  ](img/influxdb.requests.png)_**


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

# Переход с InfluxQL на Flux

---
<!-- _class: main
-->

# Смена БД InfluxDB v1, InfluxDB v2, VictoriaMetrics, ClickHouse

---
<!-- _class: main
-->

# Итоги


---

<!-- _class: lead12
_footer: 'Cсылка на [слайды](https://polarnik.github.io/influxdb-bench/), ссылка [на бенчмарк](https://github.com/polarnik/influxdb-bench)'
-->

# Вопросы/ответы

# Как ускорить запросы к InfluxDB

## Смирнов Вячеслав

### @qa_load



