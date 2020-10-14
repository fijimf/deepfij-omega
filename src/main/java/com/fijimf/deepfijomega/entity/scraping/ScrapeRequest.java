package com.fijimf.deepfijomega.entity.scraping;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
CREATE TABLE scrape_request
(
    id BIGSERIAL PRIMARY KEY,
    job_id BIGINT NOT NULL REFERENCES scrape_job(id),
    model_key VARCHAR(20) NOT NULL,
    requested_at TIMESTAMP NOT NULL,
    status_code INT NOT NULL,
    digest VARCHAR(48) NOT NULL,
    updates_proposed INT NOT NULL,
    updates_accepted INT NOT NULL
);
 */
@Entity
@Table(
        name = "scrape_request",
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "model_key"})
)
public class ScrapeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //BIGSERIAL PRIMARY KEY,
    @Column(name = "job_id")
    private long jobId;// BIGINT NOT NULL REFERENCES scrape_job(id),
    @Column(name = "model_key")
    private String modelKey;// VARCHAR(20) NOT NULL,
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;// TIMESTAMP NOT NULL,
    @Column(name = "status_code")
    private int statusCode;// INT NOT NULL,
    private String digest;// VARCHAR(48) NOT NULL,
    @Column(name = "updates_proposed")
    private int updatesProposed;// INT NOT NULL,
    @Column(name = "updates_accepted")
    private int updatesAccepted;// INT NOT NULL

    protected ScrapeRequest() {
    }

    public ScrapeRequest(long jobId, String modelKey, LocalDateTime requestedAt, int statusCode, String digest, int updatesProposed, int updatesAccepted) {
        this.jobId = jobId;
        this.modelKey = modelKey;
        this.requestedAt = requestedAt;
        this.statusCode = statusCode;
        this.digest = digest;
        this.updatesProposed = updatesProposed;
        this.updatesAccepted = updatesAccepted;
    }

    public long getId() {
        return id;
    }

    public long getJobId() {
        return jobId;
    }

    public String getModelKey() {
        return modelKey;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public String getRequestedAtString() {
        return requestedAt.format(DateTimeFormatter.ofPattern("yyyy-MMM-d HH:mm:ss"));
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDigest() {
        return digest;
    }

    public int getUpdatesProposed() {
        return updatesProposed;
    }

    public int getUpdatesAccepted() {
        return updatesAccepted;
    }
}
