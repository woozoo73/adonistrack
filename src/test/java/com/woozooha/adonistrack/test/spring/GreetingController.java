package com.woozooha.adonistrack.test.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @Autowired
    GreetingService greetingService;

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", required = false) String name) {
        return greetingService.greeting(name);
    }

}
