package com.fijimf.deepfijomega.entity.schedule;

import javax.persistence.*;

/*
CREATE TABLE team (
                      id BIGSERIAL PRIMARY KEY,
                      key VARCHAR(48) NOT NULL,
                      name VARCHAR(64) NOT NULL,
                      nickname VARCHAR(64) NOT NULL,
                      logo_url VARCHAR(128) NOT NULL,
                      color1 VARCHAR(48) NOT NULL,
                      color2 VARCHAR(48) NOT NULL
);

CREATE UNIQUE INDEX ON team(key);
CREATE UNIQUE INDEX ON team(name);
 */
@Entity
@Table(name="team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;// BIGSERIAL PRIMARY KEY,
    @Column(unique = true)
    private String key;// VARCHAR(48) NOT NULL,
    @Column(unique = true)
    private String name ;//VARCHAR(64) NOT NULL,
    private String nickname;// VARCHAR(64) NOT NULL,
    private String logoUrl;// VARCHAR(128) NOT NULL,
    private String color1;// VARCHAR(48) NOT NULL,
    private String color2;// VARCHAR(48) NOT NULL

    protected Team() {
    }

    public Team(String key, String name, String nickname, String logoUrl, String color1, String color2) {
        this.id=0L;
        this.key = key;
        this.name = name;
        this.nickname = nickname;
        this.logoUrl = logoUrl;
        this.color1 = color1;
        this.color2 = color2;
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

    public String getNickname() {
        return nickname;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getColor1() {
        return color1;
    }

    public String getColor2() {
        return color2;
    }
}
