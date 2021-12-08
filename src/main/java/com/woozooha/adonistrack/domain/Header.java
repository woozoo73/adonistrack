package com.woozooha.adonistrack.domain;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class Header {

    private String name;

    @Getter(value = AccessLevel.NONE)
    private List<String> values;

    private String value;

    public Header(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getValue() {
        if (values == null) {
            return null;
        }

        return values.stream().collect(Collectors.joining(","));
    }

}
