from prometheus_client import CollectorRegistry, Gauge, push_to_gateway
'''
    https://prometheus.github.io/client_python/exporting/pushgateway/

    pip install prometheus_client

    # HELP job_last_success_unixtime Last time a batch job successfully finished
    # TYPE job_last_success_unixtime gauge
    job_last_success_unixtime{instance="",job="batchA"} 1.734914692756612e+09
    
    # HELP push_failure_time_seconds Last Unix time when changing this group in the Pushgateway failed.
    # TYPE push_failure_time_seconds gauge
    push_failure_time_seconds{instance="",job="batchA"} 0
    
    # HELP push_time_seconds Last Unix time when changing this group in the Pushgateway succeeded.
    # TYPE push_time_seconds gauge
    push_time_seconds{instance="",job="batchA"} 1.734914692774076e+09
'''

registry = CollectorRegistry()
g = Gauge('job_last_success_unixtime', 'Last time a batch job successfully finished', registry=registry)
g.set_to_current_time()
push_to_gateway('localhost:9091', job='batchA', registry=registry)