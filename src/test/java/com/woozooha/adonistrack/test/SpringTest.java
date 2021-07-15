package com.woozooha.adonistrack.test;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.test.spring.Application;

import lombok.extern.slf4j.Slf4j;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, classes = { Application.class })
@Slf4j
public class SpringTest {

    String baseUri = "http://localhost:8080";

    @Test
    public void greeting1() {
        Long id = 1L;
        Map<String, Object> greeting = greeting(id);
        assertEquals(id.toString(), greeting.get("id").toString());
        assertEquals("Hello\nfoo", greeting.get("content"));
    }

    @Test
    public void greeting2() {
        Long id = 2L;
        Map<String, Object> greeting = greeting(id);
        assertNull(greeting.get("id"));
        assertNull(greeting.get("content"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void greeting3() {
        greeting(null);
    }

    @Test
    public void zHistories() {
        List<Map<String, Invocation>> invocationList = histories();
        assertTrue(invocationList.size() > 0);
    }

    private Map<String, Object> greeting(Long id) {
        Response response = request().get("/greeting/{id}", id);
        JsonPath jsonPath = response.jsonPath();
        Map<String, Object> greeting = jsonPath.get();

        return greeting;
    }

    private List<Map<String, Invocation>> histories() {
        Response response = request().get("/adonistrack/histories");
        JsonPath jsonPath = response.jsonPath();
        String json = jsonPath.prettify();

        log.info(json);

        List<Map<String, Invocation>> invocationList = jsonPath.get();

        return invocationList;
    }

    private RequestSpecification request() {
        RequestSpecification spec = new RequestSpecBuilder().setBaseUri(baseUri).build();
        RequestSpecification request = given().spec(spec).log().all().contentType(MediaType.APPLICATION_JSON_VALUE);

        return request;
    }

}
