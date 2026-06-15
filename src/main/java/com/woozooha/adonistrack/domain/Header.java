package com.woozooha.adonistrack.domain;

import lombok.*;

import java.util.List;

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

        this.value = makeValue();
    }

    private String makeValue() {
        if (values == null) {
            return null;
        }

        if (values.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (i < values.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
