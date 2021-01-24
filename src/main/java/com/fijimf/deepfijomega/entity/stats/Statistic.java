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
    private long id;
    private String key;
    private String name;

    @Column(name="model_id")
    private Long modelId;

    @Column(name="higher_is_better")
    private boolean higherIsBetter;
    @Column(name="default_value")
    private Double defaultValue;
    @Column(name="format_string")
    private String formatString;

    public Statistic(){

    }
    public Statistic(String key, String name,Long modelId, boolean higherIsBetter,  Double defaultValue, String formatString) {
        this.id = 0L;
        this.key = key;
        this.name = name;
        this.modelId = modelId;
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

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
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
