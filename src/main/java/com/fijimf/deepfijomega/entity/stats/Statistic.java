package com.fijimf.deepfijomega.entity.stats;

import javax.persistence.*;

/*
CREATE TABLE statistic
(
    id             BIGSERIAL PRIMARY KEY,
    key            VARCHAR(64) UNIQUE NOT NULL,
    name           VARCHAR(64)        NOT NULL,
    higherIsBetter BOOLEAN            NOT NULL,
    defaultValue   NUMERIC(18, 6)     NULL,
    formatString   VARCHAR(32)
);

 */
@Entity
@Table(name = "statistic")
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String key;
    private String name;
    private boolean higherIsBetter;
    private Double defaultValue;
    private String formatString;

    public Statistic(){

    }
    public Statistic(String key, String name, boolean higherIsBetter, Double defaultValue, String formatString) {
        this.id = 0L;
        this.key = key;
        this.name = name;
        this.higherIsBetter = higherIsBetter;
        this.defaultValue = defaultValue;
        this.formatString = formatString;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHigherIsBetter() {
        return higherIsBetter;
    }

    public void setHigherIsBetter(boolean higherIsBetter) {
        this.higherIsBetter = higherIsBetter;
    }

    public Double getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFormatString() {
        return formatString;
    }

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }
}
