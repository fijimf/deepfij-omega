package com.fijimf.deepfijomega.scraping;

import java.time.LocalDateTime;
import java.util.List;

public class RequestResult {
    private final String url;
    private final int returnCode;
    private final String digest;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final List<UpdateCandidate> updateCandidates;

    public RequestResult(String url, int returnCode, String digest, LocalDateTime start, LocalDateTime end, List<UpdateCandidate> updateCandidates) {
        this.url = url;
        this.returnCode = returnCode;
        this.digest = digest;
        this.start = start;
        this.end = end;
        this.updateCandidates = updateCandidates;
    }

    public String getUrl() {
        return url;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public String getDigest() {
        return digest;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public List<UpdateCandidate> getUpdateCandidates() {
        return updateCandidates;
    }
}
