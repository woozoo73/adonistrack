package com.woozooha.adonistrack.test.spring;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class GreetingRepository {

    public String find(String name) {
        Assert.hasText(name, "name can't be null.");

        return "hello";
    }

}
