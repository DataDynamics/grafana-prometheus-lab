# Micrometer & Prometheus Metric Example

## Prometheus Metric Type

* Counter
  * 재시작시 0으로 초기화될 수 있음
  * 증가만 할 수 있음
  * 누적치
  * 예) 서버의 요청/에러/처리 완료 수
* Gauge
  * 증가 또는 감소할 수 있는 단일 숫자값
  * 현재 메모리 사용량/온도와 같은 측정값들 사용 가능
* Histogram
  * 이벤트의 분포도를 추적
  * 예) 응답시간의 latency, 요청의 개수
  * N개의 버킷으로 구성
  * 1개의 버킷이 timeseries
  * 기본 버킷은 밀리초 단위로 web/rpc 요청을 처리하는 용도였음

참고 https://prometheus.io/docs/tutorials/understanding_metric_types/

## Prometheus Base Unit

* Time - `seconds`
* Temperature - `celsius`
* Length - `meters`
* Bytes - `bytes`
* Bits - `bytes`
* Percent - `ratio` -> 0~100 보다는 0~1값이고 `ratio`을 suffix로 사용 (예; `disk_usage_ratio`). 일반적으로 `A_per_b` 패턴을 사용
* Voltage - `volts`
* Electric Current - `amperes`
* Energy - `joules`
* Power - `rate(joules[5m])`
* Mass - `grams`

Prometheus Client API에서 `io.prometheus.metrics.model.snapshots.Unit`을 참고

## Naming

* 제일 앞에는 application 명
  * 단일 문자로 구성
  * 메트릭이 속한 도메인을 식별하는데 사용 (일종의 namespace) (예; prometheus process http 등)
  * 예시
    * `prometheus_notifications_total` (specific to the Prometheus server)
    * `process_cpu_seconds_total` (exported by many client libraries)
    * `http_request_duration_seconds` (for all HTTP requests)
* single unit 사용 (밀리초와 초를 같이 사용하면 안됨)
* base unit 사용 (seconds, bytes, meters 등; milliseconds, megabytes 안됨)
* unit은 제일 마지막에 추가
  * `http_request_duration_seconds`
  * `node_memory_usage_bytes`
  * `http_requests_total` (for a unit-less accumulating count)
  * `process_cpu_seconds_total` (for an accumulating count with unit)
  * `foobar_build_info` (for a pseudo-metric that provides metadata about the running binary)
  * `data_pipeline_last_record_processed_timestamp_seconds` (for a timestamp that tracks the time of the latest record processed in a data processing pipeline)
* 메트릭 이름은 lexicographically 정렬, 그룹핑 개념으로 네이밍
  * `prometheus_tsdb_head_truncations_closed_total`
  * `prometheus_tsdb_head_truncations_established_total`
  * `prometheus_tsdb_head_truncations_failed_total`
  * `prometheus_tsdb_head_truncations_total`
* 같은 Metric이지만 다른 특징을 갖는 경우 label 사용
  * `api_http_requests_total` - differentiate request types: `operation="create|update|delete"`
  * `api_request_duration_seconds` - differentiate request stages: `stage="extract|transform|load"` 