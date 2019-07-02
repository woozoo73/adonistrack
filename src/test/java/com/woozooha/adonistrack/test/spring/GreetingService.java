package com.woozooha.adonistrack.test.spring;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GreetingService {

    @Autowired
    GreetingRepository greetingRepository;

    public Greeting greeting(Long id) {
        return greetingRepository.findById(id).get();
    }

    @PostConstruct
    public void init() {
        Greeting greeting = new Greeting(1L, "Hello\nfoo");
        greetingRepository.save(greeting);
    }

}
