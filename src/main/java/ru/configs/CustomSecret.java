package ru.configs;

import lombok.Data;
import java.util.List;

@Data
public class CustomSecret {
    private String name;
    private String namespace;
    private List<Aliace> aliaceList;

    public CustomSecret(String name, String namespace, List<Aliace> aliaceList) {
        this.name = name;
        this.namespace = namespace;
        this.aliaceList = aliaceList;
    }
}
