package com.fijimf.deepfijomega.entity.schedule;

import javax.persistence.*;

/*
CREATE TABLE alias
(
    id      BIGSERIAL    PRIMARY KEY,
    team_id BIGINT       NOT NULL  REFERENCES team(id),
    alias   VARCHAR(128) NOT NULL
);

CREATE UNIQUE INDEX ON alias (alias);
 */
@Entity
@Table(name = "alias")
public class Alias {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;//      BIGSERIAL    PRIMARY KEY,
    private long teamId;// BIGINT       NOT NULL  REFERENCES team(id),
    @Column(unique = true)
    private String alias;//   VARCHAR(128) NOT NULL

    protected Alias() {
    }

    public Alias(long teamId, String alias) {
        this.teamId = teamId;
        this.alias = alias;
    }

    public long getId() {
        return id;
    }

    public long getTeamId() {
        return teamId;
    }

    public String getAlias() {
        return alias;
    }
}