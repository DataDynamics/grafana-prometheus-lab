package io.datadynamics.prometheus.micrometer.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/metered")
public class MeteredController {

    @Autowired
    MeterRegistry meterRegistry;

    Timer timer;

    @PostConstruct
    public void postConstruct() {
        timer = Timer.builder("http.rest.api.request.metered.hello")
                .description("HTTP REST API Request Time")
                .maximumExpectedValue(Duration.ofMinutes(2))
                .minimumExpectedValue(Duration.ofSeconds(1))
                .sla(Duration.ofMinutes(1))
                .tags("instance", "api.datalake.net")
                .register(meterRegistry);
    }

    @GetMapping("/hello/{name}")
    public Map helloworld(@PathVariable("name") String name) {
        Timer.Sample sample = Timer.start(meterRegistry);

        sleep();

        Map map = new HashMap();
        map.put("message", "Hello " + name);

        sample.stop(timer);
        return map;
    }

    private static void sleep() {
        try {
            int millis = RandomUtils.insecure().nextInt(500, 2000);
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
}
