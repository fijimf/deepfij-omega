package com.fijimf.deepfijomega.ml.features;

import com.fijimf.deepfijomega.entity.schedule.Game;

import java.time.format.DateTimeFormatter;

public class MonthExtractor  {

    public static String getFeature(Game g) {
        return g.getDate().format(DateTimeFormatter.ofPattern("MMMM"));
    }
}
