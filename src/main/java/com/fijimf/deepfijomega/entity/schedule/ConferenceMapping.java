package com.fijimf.deepfijomega.entity.schedule;

import javax.persistence.*;

/*
CREATE TABLE conference_mapping
(
    id            BIGSERIAL PRIMARY KEY,
    season_id     BIGINT    NOT NULL REFERENCES season(id),
    conference_id BIGINT    NOT NULL REFERENCES conference(id),
    team_id       BIGINT    NOT NULL REFERENCES team(id)
);

CREATE UNIQUE INDEX ON conference_mapping (season_id, team_id);

 */
@Entity
@Table(name = "conference_mapping", uniqueConstraints = @UniqueConstraint(columnNames = {"season_id", "team_id"}))
public class ConferenceMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "season_id")
    private long seasonId;
    @Column(name = "conference_id")
    private long conferenceId;
    @Column(name = "team_id")
    private long teamId;

    protected ConferenceMapping() {
    }

    public ConferenceMapping(long seasonId, long conferenceId, long teamId) {
        this.id = 0L;
        this.seasonId = seasonId;
        this.conferenceId = conferenceId;
        this.teamId = teamId;
    }

    public long getId() {
        return id;
    }

    public long getSeasonId() {
        return seasonId;
    }

    public long getConferenceId() {
        return conferenceId;
    }

    public long getTeamId() {
        return teamId;
    }
}

