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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private Team team;
    @Column(unique = true)
    private String alias;

    protected Alias() {
    }

    public Alias(Team team, String alias) {
        this.id = 0L;
        this.team = team;
        this.alias = alias;
    }

    public long getId() {
        return id;
    }

    public Team getTeam() { return team; }

    public String getAlias() {
        return alias;
    }
}