CREATE TABLE model
(
    id   BIGSERIAL PRIMARY KEY,
    key  VARCHAR(64),
    name VARCHAR(64)
);
CREATE UNIQUE INDEX ON model (key);

CREATE TABLE model_run
(
    id        BIGSERIAL PRIMARY KEY,
    model_id  BIGINT    NOT NULL,
    season_id BIGINT    NOT NULL,
    run_date  TIMESTAMP NOT NULL,
    CONSTRAINT fk_model FOREIGN KEY (model_id) REFERENCES model (id) ON DELETE NO ACTION,
    CONSTRAINT fk_season FOREIGN KEY (season_id) REFERENCES season (id) ON DELETE NO ACTION
);

CREATE UNIQUE INDEX ON model_run (model_id, season_id);

CREATE TABLE statistic
(
    id             BIGSERIAL PRIMARY KEY,
    model_id       BIGINT             NOT NULL,
    key            VARCHAR(64) UNIQUE NOT NULL,
    name           VARCHAR(64)        NOT NULL,
    higherIsBetter BOOLEAN            NOT NULL,
    defaultValue   NUMERIC(18, 6)     NULL,
    formatString   VARCHAR(32)
);
CREATE UNIQUE INDEX ON statistic (model_id, key);


CREATE TABLE series
(
    id           BIGSERIAL PRIMARY KEY,
    model_run_id BIGINT NOT NULL,
    statistic_id BIGINT NOT NULL,
    CONSTRAINT fk_model_run FOREIGN KEY (model_run_id) REFERENCES model_run (id) ON DELETE CASCADE,
    CONSTRAINT fk_statistic FOREIGN KEY (statistic_id) REFERENCES statistic (id) ON DELETE CASCADE
);

CREATE TABLE snapshot
(
    id        BIGSERIAL PRIMARY KEY,
    series_id BIGINT NOT NULL,
    date      DATE   NOT NULL,
    CONSTRAINT fk_series FOREIGN KEY (series_id) REFERENCES series (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX ON snapshot (series_id, date);

CREATE TABLE observation
(
    id          BIGSERIAL PRIMARY KEY,
    snapshot_id BIGINT         NOT NULL,
    team_id     BIGINT         NOT NULL,
    value       NUMERIC(18, 6) NULL,
    CONSTRAINT fk_snapshot FOREIGN KEY (snapshot_id) REFERENCES snapshot (id) ON DELETE CASCADE,
    CONSTRAINT fk_team FOREIGN KEY (team_id) REFERENCES team (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX ON observation (snapshot_id, team_id);
