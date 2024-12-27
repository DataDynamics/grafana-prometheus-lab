package io.datadynamics.prometheus.micrometer.controller;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sample")
public class SampleController {

    /*
        # HELP helloworld_count_total
        # TYPE helloworld_count_total counter
        helloworld_count_total{class="io.datadynamics.prometheus.micrometer.controller.SampleController",exception="none",method="helloworld",result="success"} 18.0
     */
    @Counted("helloworld_count")
    @GetMapping("/hello/{name}")
    Map helloworld(@PathVariable("name") String name) {
        Map map = new HashMap();
        map.put("message", "Hello " + name);
        return map;
    }

    /*
        # HELP helloworld_duration_seconds
        # TYPE helloworld_duration_seconds summary
        helloworld_duration_seconds_count{class="io.datadynamics.prometheus.micrometer.controller.SampleController",exception="none",method="timedHelloworld"} 3
        helloworld_duration_seconds_sum{class="io.datadynamics.prometheus.micrometer.controller.SampleController",exception="none",method="timedHelloworld"} 1.5036266
     */
    @Timed("helloworld_duration")
    @GetMapping("/hello-timed/{name}")
    Map timedHelloworld(@PathVariable("name") String name) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        Map map = new HashMap();
        map.put("message", "Hello " + name);
        return map;
    }

}
