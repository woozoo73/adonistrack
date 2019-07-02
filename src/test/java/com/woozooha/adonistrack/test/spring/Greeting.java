package com.woozooha.adonistrack.test.spring;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Greeting {

    @Id
    private Long id;

    private String content;

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

}
