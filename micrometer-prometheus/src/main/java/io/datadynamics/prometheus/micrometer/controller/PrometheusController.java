package io.datadynamics.prometheus.micrometer.controller;

import io.prometheus.metrics.core.metrics.Counter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
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

    @PostMapping("/alert")
    ResponseEntity alert(@RequestBody String message) {
        log.info("Alert message: {}", message);
        return ResponseEntity.ok().build();
    }


}
