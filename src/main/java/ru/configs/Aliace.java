package ru.configs;

import lombok.Data;
import java.util.Date;

@Data
public class Aliace {
    private String name;
    private Date expirationDate;

    public Aliace(String name, Date expirationDate) {
        this.name = name;
        this.expirationDate = expirationDate;
    }
}
