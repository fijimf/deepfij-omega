package com.fijimf.deepfijomega.entity.scraping;


import com.fijimf.deepfijomega.entity.schedule.Game;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    @Column(name="update_or_fill")
    private String updateOrFill;// VARCHAR(8) NOT NULL,
    private int season;// INT NOT NULL,
    private String model;// VARCHAR(24) NOT NULL,
    @Column(name="started_at")
    private LocalDateTime startedAt;// TIMESTAMP NOT NULL,
    @Column(name="completed_at")
    private LocalDateTime completedAt;// TIMESTAMP NULL

    @OneToMany(mappedBy = "jobId")
    @OrderBy("modelKey")
    private List<ScrapeRequest> scrapeRequests;

    protected ScrapeJob() {
    }

    public ScrapeJob(String updateOrFill, int season, String model, LocalDateTime startedAt, LocalDateTime completedAt, List<ScrapeRequest> scrapeRequests) {
        this.id = 0L;
        this.updateOrFill = updateOrFill;
        this.season = season;
        this.model = model;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.scrapeRequests = scrapeRequests;
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

    public List<ScrapeRequest> getScrapeRequests() {
        return scrapeRequests;
    }

    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt;}

    public Optional<Duration> getDuration(){
        if (completedAt==null) {
            return Optional.empty();
        } else {
            Duration duration = Duration.between(startedAt, completedAt);
            return Optional.of(duration);
        }
    }

    public String getDurationString() {
        Optional<Duration> duration = getDuration();
        if (duration.isEmpty()){
            return "";
        } else {
            Duration d = duration.get();
            return String.format("%d:%02d:%02d.%03d", d.toHoursPart(), d.toMinutesPart(), d.toSecondsPart(), d.toMillisPart());
        }
    }

    public int getNumRequests() {
       return scrapeRequests.size();
    }

    public int getGamesScraped() {
       return scrapeRequests
                .stream()
                .map(ScrapeRequest::getUpdatesProposed)
                .reduce(Integer::sum).orElse(0);
    }

    public int getChangesAccepted() {
       return scrapeRequests
                .stream()
                .map(ScrapeRequest::getUpdatesAccepted)
                .reduce(Integer::sum).orElse(0);
    }

    public long getErrorCount() {
        return  scrapeRequests
                .stream()
                .filter(req->req.getStatusCode()!=200)
                .count();

    }



}
