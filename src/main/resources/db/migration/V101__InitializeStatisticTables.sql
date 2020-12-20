CREATE TABLE statistic
(
    id             BIGSERIAL PRIMARY KEY,
    key            VARCHAR(64) UNIQUE NOT NULL,
    name           VARCHAR(64)        NOT NULL,
    higherIsBetter BOOLEAN            NOT NULL,
    defaultValue   NUMERIC(18, 6)     NULL,
    formatString   VARCHAR(32)
);
CREATE UNIQUE INDEX ON statistic (key);

CREATE TABLE model_run
(
    id         BIGSERIAL PRIMARY KEY,
    model_name VARCHAR(64),
    run_date   TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX ON model_run (model_name);

CREATE TABLE series
(
    id           BIGSERIAL PRIMARY KEY,
    model_run_id BIGSERIAL NOT NULL,
    statistic_id BIGSERIAL NOT NULL,
    CONSTRAINT fk_model_run FOREIGN KEY(model_run_id) REFERENCES model_run(id) ON DELETE CASCADE,
    CONSTRAINT fk_statistic FOREIGN KEY(statistic_id) REFERENCES statistic(id) ON DELETE CASCADE
);

CREATE TABLE snapshot
(
    id        BIGSERIAL PRIMARY KEY,
    series_id BIGSERIAL NOT NULL,
    date      DATE      NOT NULL,
    CONSTRAINT fk_series FOREIGN KEY(series_id) REFERENCES series(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX ON snapshot (series_id, date);

CREATE TABLE observation
(
    id          BIGSERIAL PRIMARY KEY,
    snapshot_id BIGSERIAL      NOT NULL,
    team_id     BIGSERIAL      NOT NULL,
    value       NUMERIC(18, 6) NULL,
    CONSTRAINT fk_snapshot FOREIGN KEY(snapshot_id) REFERENCES snapshot(id)  ON DELETE CASCADE,
    CONSTRAINT fk_team FOREIGN KEY(team_id) REFERENCES team(id)  ON DELETE CASCADE
);

CREATE UNIQUE INDEX ON observation (snapshot_id, team_id);
