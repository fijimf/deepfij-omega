package com.fijimf.deepfijomega.scraping;

public class UpdateResult {
    private final int candidates;
    private final int unmapped;
    private final int inserts;
    private final int updates;
    private final int deletes;
    private final int unchanged;

    public UpdateResult(int candidates, int unmapped, int inserts, int updates, int deletes, int unchanged) {
        this.candidates = candidates;
        this.unmapped = unmapped;
        this.inserts = inserts;
        this.updates = updates;
        this.deletes = deletes;
        this.unchanged = unchanged;
    }

    public int getCandidates() {
        return candidates;
    }

    public int getUnmapped() {
        return unmapped;
    }

    public int getInserts() {
        return inserts;
    }

    public int getUpdates() {
        return updates;
    }

    public int getDeletes() {
        return deletes;
    }

    public int getUnchanged() {
        return unchanged;
    }

    public int getChanges() {
        return inserts+updates+deletes;
    }
}
