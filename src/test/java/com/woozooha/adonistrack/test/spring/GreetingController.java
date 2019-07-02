package com.woozooha.adonistrack.test.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @Autowired
    GreetingService greetingService;

    @RequestMapping("/greeting/{id}")
    public Greeting greeting(@PathVariable Long id) {
        return greetingService.greeting(id);
    }

}
