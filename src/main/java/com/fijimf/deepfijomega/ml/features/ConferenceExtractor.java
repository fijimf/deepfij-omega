package com.fijimf.deepfijomega.ml.features;

public class ConferenceExtractor {

    public static ConferenceExtractor Home=new ConferenceExtractor(true);
    public static ConferenceExtractor Away=new ConferenceExtractor(false);

    private final boolean isHome;

    public ConferenceExtractor(boolean isHome) {
        this.isHome = isHome;
    }
}
