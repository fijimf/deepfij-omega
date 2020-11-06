package com.fijimf.deepfijomega.entity.scraping;


import javax.persistence.*;

/*
CREATE TABLE season_scrape_model (
    id BIGSERIAL PRIMARY KEY,
    year INT NOT NULL,
    model_name VARCHAR(24) NOT NULL
);
 */

@Entity
@Table(name = "season_scrape_model")
public class SeasonScrapeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int year;// INT NOT NULL,
    private String modelName;// VARCHAR(24) NOT NULL,

    protected SeasonScrapeModel() {
    }

    public SeasonScrapeModel(int year, String modelName) {
        this.id = 0L;
        this.year = year;
        this.modelName = modelName;
    }

    public long getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public String getModelName() {
        return modelName;
    }
}
