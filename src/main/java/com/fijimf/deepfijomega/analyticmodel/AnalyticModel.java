package com.fijimf.deepfijomega.analyticmodel;

import com.fijimf.deepfijomega.entity.schedule.Season;
import com.fijimf.deepfijomega.entity.stats.Statistic;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AnalyticModel {

    public String getModelKey();

    public String getModelName();

    public List<Statistic> getModelStatistics();

    public Map<String, Map<LocalDate, Map<Long, Double>>> runModel(Season season);
}
