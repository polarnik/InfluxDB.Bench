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

### ![h:35](themes/img/lead/miro.svg) Miro 

<!--

-->

---
<!-- _class: title -->

# Ускоряю и тестирую инфраструктурные сервисы в ![h:35](themes/img/lead/miro.svg) Miro
## Развиваю сообщество нагрузочников
### @qa_load

![bg cover](img/omsk.jpg)

<!-- 
Повышаю качество более десяти лет. Занимаюсь системой дистанционного банковского обслуживания юридических лиц. Основной профиль моей работы — тестирование производительности. Развиваю сообщество инженеров по тестированию производительности, помогая коллегам в telegram чате «QA — Load & Performance».
-->

---
<!-- _class: main -->

# Об ускорении __InfluxDB__ глазами инженера по нагрузке 


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

<!-- _class: main -->

# Когда и для кого оптимизация __InfluxDB__ важна

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

# Много тестов производительности с __Yandex.Tank__, __k6__, __JMeter__, __Gatling__

![bg height:75%](img/infra.tools.png)

---

# Хранятся данные за годы и строятся тренды

![bg 70%](img/trend.png)

---

# Вы хотите ускорить __InfluxDB__ OSS v1.8 или v2.2

![bg 100%](img/influxdb.versions.png)


---

# Поменяем разные настройки в разных местах

![bg 60%](img/routine.png)

---
# Оптимизируем запись в __InfluxDB__ c __Telegraf__ и __MQ__


![bg 90%](img/telegraf.for.write.png)


---
# Оптимизируем выборку c __Grafana__ и __nginx__

![bg 90%](img/grafana.optimisation.png)


---
# Вычислим медленные запросы

![bg 80%](img/Complex.query.png)

---
# И закешируем их с __Continuous Queries__

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

<!-- _class: main -->

# Мониторинг

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

<!-- _class: main -->

# Простой способ ускорения — разделить индексы

---
# __3.__ ⚙️ Разделение данных на разные базы и серверы __InfluxDB__

- __Разные базы данных__ для команд и стендов

- __Разные серверы__ для новых и архивных данных 
- __Разные сервисы__ для разных данных
- __Получаем__ скорость, __терям__ простоту построения трендов по всем данным


---
# __Разные базы данных__ для команд и стендов

![bg fit](img/big.db.png)



---
# __Разные базы данных__ для команд и стендов

![bg fit brightness:0.4](img/big.db.png)


# __Если данные в одной БД__:

- База данных большая
- Нужна фильтрация данных
- Высокий риск повреждения данных



---
# __Разные базы данных__ для команд и стендов

![bg fit brightness:0.4](img/big.db.png)

# Если данные в одной БД:

- База данных большая
- Нужна фильтрация данных
- Высокий риск повреждения данных

# __Если данные в разных БД__:

- Бекапы проще
- Фильтрация не нужна
- Сложнее замедлить работу всех БД

---
#  __Разные серверы__ для новых и архивных данных 

![bg fit](img/old.new.data.png)

---
# __Два сервиса__ c одной активной БД быстрее одного
![bg fit](img/server.split.png)

---
# __Семь контейнеров__ быстрее одного большого?
![bg fit](img/docker.split.png)

---
# __Семь контейнеров__ быстрее одного большого?
![bg fit opacity:0.4](img/docker.split.png)

# Да, __быстрее__, 
# но если
# используются не
# все одновременно

---
#  Построение трендов по метрикам из одной БД

![bg 90% ](img/big.db.2.png)

## Простые запросы
## для трендов
## по всем метрикам
## с фильтрами и без них

---
#  Скрипты и отдельная БД для общих трендов

## Все еще простые запросы
## для трендов по новым
## метрикам в одной БД.

# __Но, сложные механизмы__
## __для трендов по всем__
## __метрикам всех БД.__

![bg 90% ](img/small.db.2.png)

---
<!-- _class: invert -->
# __3.__ ⚙️ Разделение данных на разные базы и серверы __InfluxDB__

- __Разные базы данных__ для команд и стендов

- __Разные серверы__ для новых и архивных данных 
- __Разные сервисы__ для разных данных
- __Получаем__ скорость, __терям__ простоту построения трендов по всем данным

---
# __4.__ ⚙️ «Архивирование» медленной базы __InfluxDB__ в __Grafana__

- __Grafana__ как реестр активных баз данных

- Перенос файлов базы данных __{name}__ на архивный сервер
- Создание пустой базы данных __{name}-{from}__
- Настройка __Grafana DataSource__ с именем __{name}__ на новую базу данных
- Создание __Grafana DataSource__ для архивной базы данных

---

# __Grafana__ как реестр активных баз данных

![bg 85%](img/grafana-datasources.png)

---

# Имеем __DataSource__ с именем JMeter в __Grafana__ 

![bg 85%](img/grafana-slow-db.png)

---

# Переключаем __DataSource__ на новую базу данных

![bg 85%](img/grafana-two-db.png)

---
<!-- _class: invert -->
# __4.__ ⚙️ «Архивирование» медленной базы __InfluxDB__ в __Grafana__

- __Grafana__ как реестр активных баз данных

- Перенос файлов базы данных __{name}__ на архивный сервер
- Создание пустой базы данных __{name}-{from}__
- Настройка __Grafana DataSource__ с именем __{name}__ на новую базу данных
- Создание __Grafana DataSource__ для архивной базы данных

---

<!-- _class: main -->

# Ускорение записи в __InfluxDB__ со стороны источника метрик

---
# Одновременная запись метрик может тормозить

# 🐌 🤔

![bg 90%](img/wo.telegraf.png)

---
# Оптимизируем запись в __InfluxDB__ c __Telegraf__ и __MQ__

# 🚀 😀 

![bg 90%](img/telegraf.for.write.png)


---
# __5.__ ⚙️ Сокращение запросов из __JMeter__, __Gatling__,  ...

- __Batch, batch, batch...__ увеличиваем размер batch-а при вставке

- Не используем переменные в имени запроса

- Замена интервала в __1 секунду__ на __1 минуту__ для тестов дольше __5 минут__

- Во время теста достаточно уровня детализации __summary__

- __SLA__ можно проверять по % ошибок, а __Assertion__ вставить в тест

- Детальный отчет можно формировать после теста


---

# Настройки __Gatling__ по умолчанию

```yaml
gatling {
  data {
    writers = [console, file]

    graphite {
      host = "localhost"
      port = 2003
      #light = false              # only send the all* stats
      #protocol = "tcp"           # "tcp", "udp"
      #rootPathPrefix = "gatling" # the root prefix of the metrics
      #bufferSize = 8192          # internal data buffer size, in bytes
      #writePeriod = 1            # write period, in seconds
    }
  }
}
```

---

# __Batch, batch, batch...__ увеличиваем размер batch-а

# Пусть копим метрики __60 сек__

```yaml
  writers = [console, file, graphite] # +graphite
  graphite {
    host = "localhost"
    port = 2003
    #light = false           
    #protocol = "tcp"        
    bufferSize = 65000       # 🚀 TCP limit 64k (было 8192)
    writePeriod = 60         # 🚀 Batch (было 1) 
  }
```

---

# __Batch, batch, batch...__ 

# Копим метрики __60 сек__, без деталей

```yaml
  writers = [console, file, graphite] # +graphite
  graphite {
    host = "localhost"
    port = 2003
    light = true             # 🚀 без деталей (было false)
    #protocol = "tcp"        
    bufferSize = 65000       
    writePeriod = 60          
  }
```

---

# __Batch, batch, batch...__ 

# Копим метрики __60 сек__, без деталей, с потерями

```yaml
  writers = [console, file, graphite] # +graphite
  graphite {
    host = "localhost"
    port = 2004              # udp-порт (было 2003)
    light = true             
    protocol = "udp"         # 🚀 с потерями (было tcp)
    bufferSize = 65000       
    writePeriod = 60          
  }
```
---

# Настройки __JMeter__ по умолчанию

```yaml
QUEUE_SIZE=5000 # Async Queue size
# Backend metrics window mode (fixed=fixed-size window, timed=time boxed)
backend_metrics_window_mode=fixed
# Backend metrics sliding window size for Percentiles, Min, Max
backend_metrics_window=100

# Backend metrics sliding window size for Percentiles, Min, Max
# when backend_metrics_window_mode is timed
# Setting this value too high can lead to OOM
# backend_metrics_large_window=5000
# Send interval in second
# Defaults to 5 seconds
backend_influxdb.send_interval=5
```

---

# __Batch, batch, batch...__ увеличиваем размер batch-а

# Пусть у нас __1200 rps__ и копим метрики __60 сек__

```yaml
QUEUE_SIZE=72000 # 1200x60, было 5000, Async Queue size
```
Конфиг для `backend_metrics_window_mode=fixed` __(не очищается)__:
```yaml
backend_influxdb.send_interval=60 # было 5
backend_metrics_window=72000 # 1200x60, было 100
```
Конфиг для `backend_metrics_window_mode=timed` __(очищается 🚀)__:
```yaml
backend_influxdb.send_interval=60 # было 5
backend_metrics_large_window=72000 # 1200x60, было 5000
```



---

# __JMeter__-сценарий, с медленной статистикой

```java
var plan =  scenario.sendHttp(
  testId: getTestId(linksCount), 
  linksCount: linksCount, 
  nameOfSampler: "web?id=${id} (GET)" //переменная ${id} в имени
);
```
## Получим много тегов в __InfluxDB__ и большой индекс:

- `web?id=0 (GET)`
- `web?id=1 (GET)`
- ...
- `web?id=1000000 (GET)`

---
<!-- _class: error  -->

# Или вообще ничего  не запишем: __ERROR__ org.apache.jmeter.visualizers.backend.influxdb

```
failed to send data to influxDB server.

Error writing metrics to influxDB Url: 
http://influxdb:8086/write?db=jmeter100000, 
responseCode: 400, responseBody:  {"error": "partial write:
  max-values-per-tag limit exceeded (100000/100000): 
  measurement=\"jmeter\" tag=\"transaction\" 
  value=\"web?id=19806 (GET)\" dropped=2"}

Error writing metrics to influxDB Url: 
http://influxdb:8086/write?db=jmeter100000, 
responseCode: 413, responseBody: {"error":"Request Entity Too Large"}
```
---

# Не используем переменные в имени запроса


```java
var plan =  scenario.sendHttp(
  testId: getTestId(linksCount), 
  linksCount: linksCount, 
  nameOfSampler: "web?id={id} (GET)" //вот так быстрее
);
```
## Получим один тег в __InfluxDB__ и малый индекс:

- `web?id={id} (GET)`

---

# __SLA__ можно проверять по % ошибок (в __summury__)
# __Assertion__ на длительность вставить в тест

- для запросов в JMeter: [__Duration Assertion__](https://jmeter.apache.org/usermanual/component_reference.html#Duration_Assertion)
- для транзакций в JMeter: [__JSR-223 Listener__](https://gist.github.com/polarnik/7f5fdc5c70809c879dd42904b8639f31)
```
jmeter.apache.org/usermanual/component_reference.html#Duration_Assertion
gist.github.com/polarnik/7f5fdc5c70809c879dd42904b8639f31
```
- для запросов в Gatling: [__`.check(responseTimeInMillis.lte(100))`__](https://gatling.io/docs/gatling/reference/current/core/check/#responsetimeinmillis)
- для теста в Gatling: [__Assertions__](https://gatling.io/docs/gatling/reference/current/core/assertions/)

```
gatling.io/docs/gatling/reference/current/core/check/#responsetimeinmillis
gatling.io/docs/gatling/reference/current/core/assertions/
```

---

<!-- _class: invert  -->

# __5.__ ⚙️ Сокращение запросов из __JMeter__, __Gatling__, ...

- __Batch, batch, batch...__ увеличиваем размер batch-а при вставке

- Замена интервала в __1 секунду__ на __1 минуту__ для тестов дольше __5 минут__

- Во время теста достаточно уровня детализации __summary__

- __SLA__ можно проверять по % ошибок, а __Assertion__ вставить в тест

- Детальный отчет можно формировать после теста


---

<!-- _class: main -->

# Ускорение выборки из __InfluxDB__ через __Grafana__ с __nginx__

---
# __5.__ ⚙️ Кеширование ответов на запросов от __Grafana__ к __InfluxDB__ с __NGINX__

- Кеширование __GET__-запросов в __NGINX__ (нужно)

- Кеширование __POST__-запросов в __NGINX__ (не нужно)
- Влияние интервалов времени __Grafana__ на кеширование
- Замена динамического интервала вида __`now()-6h...now()`__ статитическим

---

![bg 90%](img/grafana.optimisation.png)

---

![bg 90%](img/nginx_boost.png)

---

# __InfluxDB__ endpoints: кешируем запросы

- __`https://docs.influxdata.com/influxdb/v1.8/tools/api/`__
- /debug/pprof (GET)
- /debug/requests (GET)
- /debug/vars (GET)
- /ping (GET, HEAD)
- __/query (GET, POST)__ ‼️
- /write (POST)
- /metrics (GET)
- /api/v2/query (POST)
- /api/v2/write (POST)
- /health (GET)

---

# __InfluxDB__ endpoint __/query__ (GET, POST)

- GET:
  - __SELECT__ ‼️
  - __SHOW__ ‼️

- POST:
  - SELECT INTO
  - ALTER
  - CREATE
  - DELETE
  - DROP
  - GRANT
  - KILL
  - REVOKE

---

# Как-бы статический интервал __Last 6 hours__ UTC:

```bash
curl -G 'http://influxdb:8086/query?db=mydb' --data-urlencode \
  'q=SELECT * FROM "metrics" \
  where time>1655827200000 AND time<1655848800000'
```

# Спустя 30 секунд __Grafana__ сформирует запрос:
```bash
curl -G 'http://influxdb:8086/query?db=mydb' --data-urlencode \
  'q=SELECT * FROM "metrics" \
  where time>1655827230023 AND time<1655848830023'
```
# А это уже новый URL, новый ключ кеширования

---

# Важен __статический интервал__ для кеширования

# __Абсолютный:__

![width:1150](img/inverval.ok.png)

# Не относительный: 

![width:1150](img/inverval.ko.png)

---


# Ссылка в __Grafana Text (HTML)__ для автоматизации

```html
<h2> 
<a href="/d/UID/?from=${__from}&to=${__to}&${db:queryparam}">
Select static time interval
</a>
</h2>
```

![width:1150](img/static.time.link.png)

---
<!-- _class: invert -->
# __5.__ ⚙️ Кеширование ответов на запросы от __Grafana__ к __InfluxDB__ с __NGINX__

- Кеширование __GET__-запросов в __NGINX__

- Кеширование __POST__-запросов в __NGINX__
- Влияние интервалов времени __Grafana__ на кеширование
- Замена динамического интервала вида __`now()-6h...now()`__ статитическим

---

<!-- _class: main -->

# Конфигурирование ограничений в __InfluxDB__


---
# __6.__ ⚙️ Конфигурирование сервера __InfluxDB__

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
# __6.__ ⚙️ Конфигурирование сервера __InfluxDB__

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
# __7.__ 🛠️ Сокращение количества и ускорение фильтров по тегам в __Grafana__

---
# __8.__ 🛠️ Удаление значений тегов с низкой селективностью из __InfluxDB__


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

## Разгонный потенциал __InfluxDB__ v1.8 огромен

## __InfluxDB__ v2 использует быстрый движок

## __VictoriaMetrics__ кеширует ответы и жмет данные

## __ClickHouse__ быстр (при наличии памяти) и удобен

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
<!-- _class: invert -->

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
<!-- _class: main
-->

# Делите, кешируйте, ускоряйте! 

### `https://github.com/polarnik/influxdb-bench`

---

<!-- 
_footer: 'Cсылка на [слайды](https://polarnik.github.io/influxdb-bench/), ссылка [на бенчмарк](https://github.com/polarnik/influxdb-bench)'
-->


# Ускорение __InfluxDB__, Смирнов Вячеслав, __@qa_load__

### Feedback :

### 🙂 👍 👉

### 👀 🎦 🍿


![bg 42%](img/qr-code.png)
