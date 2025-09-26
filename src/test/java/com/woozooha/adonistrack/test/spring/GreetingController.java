package com.woozooha.adonistrack.test.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @Autowired
    GreetingService greetingService;

    @GetMapping("/greeting/{id}")
    public Greeting greeting(@PathVariable Long id) {
        return greetingService.greeting(id);
    }

    @GetMapping("/loop/{id}/{count}")
    public Greeting loop(@PathVariable Long id, @PathVariable int count) {
        Greeting greeting = null;
        for (int i = 0; i < count; i++) {
            greeting = greetingService.greeting(id);
        }

        return greeting;
    }

}
