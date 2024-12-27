package io.datadynamics.prometheus.micrometer.controller;

import io.prometheus.metrics.core.metrics.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/prometheus")
public class PrometheusController {

    @Autowired
    PrometheusService prometheusService;

    @GetMapping("/hello")
    Map helloworld() {
        Counter counter = prometheusService.getCounterOrNew("http_rest_api_prometheus_hello", "HTTP REST API HelloWorld");
        counter.inc(1);

        Map map = new HashMap();
        map.put("message", "Hello World");
        return map;
    }

}
