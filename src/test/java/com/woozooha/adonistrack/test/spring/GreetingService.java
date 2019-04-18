package com.woozooha.adonistrack.test.spring;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    GreetingRepository greetingRepository;

    public Greeting greeting(String name) {
        greetingRepository.find(name);
        greetingRepository.find("foo-" + name);

        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

}
