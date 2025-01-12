package io.datadynamics.prometheus.micrometer.controller;

import io.micrometer.core.annotation.Counted;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @Counted("helloworld.sayhello")
    @GetMapping("/sayhello")
    public ResponseEntity sayhello() throws IOException {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        return ResponseEntity.ok("Hello World");
    }
}
