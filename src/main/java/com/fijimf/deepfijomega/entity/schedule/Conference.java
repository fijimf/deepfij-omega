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
}
