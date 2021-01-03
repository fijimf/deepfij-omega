package com.fijimf.deepfijomega.manager;

import com.fijimf.deepfijomega.entity.stats.ModelRun;
import com.fijimf.deepfijomega.entity.stats.Observation;
import com.fijimf.deepfijomega.entity.stats.Series;
import com.fijimf.deepfijomega.entity.stats.Snapshot;
import com.fijimf.deepfijomega.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * The way this thing works is that for a given Season we look at Series of Snapshots of Observations
 *  - when we retrieve statistics we retrieve them by season & statistic(key)
 *  - when we create/update them we create them by season and model_run(key)
 */

@Service
public class StatisticManager {
    private final SeasonRepository seasonRepo;

    private final ModelRunRepository modelRunRepo;

    private final ModelRepository modelRepo;

    private final SeriesRepository seriesRepo;

    private final SnapshotRepository snapRepo;

    private final ObservationRepository obsRepo;

    private final StatisticRepository statRepo;


    @Autowired
    public StatisticManager(SeasonRepository seasonRepo, ModelRunRepository modelRunRepo, ModelRepository modelRepo, SeriesRepository seriesRepo, SnapshotRepository snapRepo, ObservationRepository obsRepo, StatisticRepository statRepo) {
        this.seasonRepo = seasonRepo;
        this.modelRunRepo = modelRunRepo;
        this.modelRepo = modelRepo;
        this.seriesRepo = seriesRepo;
        this.snapRepo = snapRepo;
        this.obsRepo = obsRepo;
        this.statRepo = statRepo;
    }

    public Optional<Series> getSeries(String modelKey, String statKey, Integer year) {
        return seasonRepo.findFirstByYear(year).flatMap(s ->
                modelRunRepo.findByModelKeyAndSeasonId(modelKey, s.getId()).flatMap(mr ->
                        seriesRepo.findByModelRunIdAndStatisticKey(mr.getId(), statKey)
                )
        );
    }

    public void saveModel(String key, Long seasonId, Map<String, Map<LocalDate, Map<Long, Double>>> modelData) {
        ModelRun modelRun = findAndClearModelRun(key, seasonId).orElseGet(() -> createModelRun(key, seasonId));
        modelData.forEach((statKey, seriesData)->{
            statRepo.findByKey(statKey).ifPresent(stat->{
                Series series = seriesRepo.save(new Series(modelRun, stat, List.of()));
                seriesData.forEach((date, data)->{
                    Snapshot snapshot = snapRepo.save(new Snapshot(series, date, List.of()));
                    data.forEach((team, value)->{
                        if (value!=null){
                            obsRepo.save(new Observation(snapshot,team,value));
                        }
                    });
                });
            });
        });
    }

    public Optional<ModelRun> findAndClearModelRun(String key, Long seasonId) {
        return modelRunRepo.findByModelKeyAndSeasonId(key, seasonId).map(m->{
            m.getSeriesList().clear();
            m.setRunDate(LocalDateTime.now());
            return modelRunRepo.save(m);
        });
    }

    public ModelRun createModelRun(String key, Long seasonId) {
        return modelRepo.findByKey(key).map(mm-> modelRunRepo.save(new ModelRun(mm, seasonId, LocalDateTime.now()))).orElseThrow(RuntimeException::new);
    }

}
