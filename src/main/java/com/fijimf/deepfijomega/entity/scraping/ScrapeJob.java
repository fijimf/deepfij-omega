package com.fijimf.deepfijomega.entity.scraping;


import javax.persistence.*;
import java.time.LocalDateTime;

/*
CREATE TABLE scrape_job
(
    id   BIGSERIAL PRIMARY KEY,
    update_or_fill VARCHAR(8) NOT NULL,
    season INT NOT NULL,
    model VARCHAR(24) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP NULL
);

 */

@Entity
@Table(name="scrape_job")
public class ScrapeJob {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    @Column(name="update_or_fill")
    private String updateOrFill;// VARCHAR(8) NOT NULL,
    private int season;// INT NOT NULL,
    private String model;// VARCHAR(24) NOT NULL,
    @Column(name="started_at")
    private LocalDateTime startedAt;// TIMESTAMP NOT NULL,
    @Column(name="completed_at")
    private LocalDateTime completedAt;// TIMESTAMP NULL

    protected ScrapeJob() {
    }

    public ScrapeJob(String updateOrFill, int season, String model, LocalDateTime startedAt, LocalDateTime completedAt) {
        this.id = 0L;
        this.updateOrFill = updateOrFill;
        this.season = season;
        this.model = model;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public long getId() {
        return id;
    }

    public String getUpdateOrFill() {
        return updateOrFill;
    }

    public int getSeason() {
        return season;
    }

    public String getModel() {
        return model;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
