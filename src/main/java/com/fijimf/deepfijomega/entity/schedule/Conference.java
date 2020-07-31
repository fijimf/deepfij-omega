package com.fijimf.deepfijomega.entity.schedule;

import javax.persistence.*;

/*
CREATE TABLE conference
(
    id        BIGSERIAL    PRIMARY KEY,
    key       VARCHAR(32)  NOT NULL,
    name      VARCHAR(144) NOT NULL,
    short_name VARCHAR(36) NOT NULL,
    level     VARCHAR(64) NOT NULL,
    logo_url  VARCHAR(256) NULL
);

CREATE UNIQUE INDEX ON conference (key);
CREATE UNIQUE INDEX ON conference (name);

 */
@Entity
@Table(name = "conference")
public class Conference {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;//        BIGSERIAL    PRIMARY KEY,
    @Column(unique = true)
    private String key;//       VARCHAR(32)  NOT NULL,
    @Column(unique = true)
    private String name;//      VARCHAR(144) NOT NULL,
    @Column(unique = true)
    private String shortName;// VARCHAR(36) NOT NULL,
    private String level;//     VARCHAR(64) NOT NULL,
    private String logoUrl;// VARCHAR(256) NULL

    protected Conference() {
    }

    public Conference(String key, String name, String shortName, String level, String logoUrl) {
        this.id = 0L;
        this.key = key;
        this.name = name;
        this.shortName = shortName;
        this.level = level;
        this.logoUrl = logoUrl;
    }

    public long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLevel() {
        return level;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
