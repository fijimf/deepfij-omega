package com.fijimf.deepfijomega.controllers;

import java.time.LocalDateTime;

public class ScrapeSeasonLine {
    private final int year;
    private final int numberOfGames;
    private final String modelName;
    private final int lastScrapeNumberOfUpdates;
    private final LocalDateTime lastScrape;
    private final boolean updateable;


    public ScrapeSeasonLine(int year, int numberOfGames, String modelName, int lastScrapeNumberOfUpdates, LocalDateTime lastScrape, boolean updateable) {
        this.year = year;
        this.numberOfGames = numberOfGames;
        this.modelName = modelName;
        this.lastScrapeNumberOfUpdates = lastScrapeNumberOfUpdates;
        this.lastScrape = lastScrape;
        this.updateable = updateable;
    }

    public int getYear() {
        return year;
    }

    public int getNumberOfGames() {
        return numberOfGames;
    }

    public String getModelName() {
        return modelName;
    }

    public int getLastScrapeNumberOfUpdates() {
        return lastScrapeNumberOfUpdates;
    }

    public LocalDateTime getLastScrape() {
        return lastScrape;
    }

    public boolean isUpdateable() {
        return updateable;
    }
}
