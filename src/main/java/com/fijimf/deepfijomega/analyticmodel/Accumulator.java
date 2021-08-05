package com.fijimf.deepfijomega.analyticmodel;

import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.stats.Statistic;

import java.util.List;
import java.util.Map;

public interface Accumulator {
    void accumulate(Game g);

    List<Statistic>  getStatistics();

    Map<String, Map<Long, Double>> extractValues();
}
