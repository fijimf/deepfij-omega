package com.fijimf.deepfijomega.manager;

import com.fijimf.deepfijomega.analyticmodel.AnalyticModel;
import com.fijimf.deepfijomega.analyticmodel.WonLost;
import com.fijimf.deepfijomega.entity.stats.*;
import com.fijimf.deepfijomega.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/*
 * The way this thing works is that for a given Season we look at Series of Snapshots of Observations
 *  - when we retrieve statistics we retrieve them by season & statistic(key)
 *  - when we create/update them we create them by season and model_run(key)
 */

@Service
public class StatisticManager {
    public static final Logger log = LoggerFactory.getLogger(StatisticManager.class);

    private final SeasonRepository seasonRepo;

    private final ModelRunRepository modelRunRepo;

    private final ModelRepository modelRepo;

    private final SeriesRepository seriesRepo;

    private final SnapshotRepository snapRepo;

    private final ObservationRepository obsRepo;

    private final StatisticRepository statRepo;

    private final WonLost wonLost = new WonLost();

    private final Map<String, ? extends AnalyticModel> models = Map.of(
            wonLost.getModelKey(), wonLost
    );

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

    @PostConstruct
    public void init() {
        for (AnalyticModel analyticModel : models.values()) {
            String modelKey = analyticModel.getModelKey();

            modelRepo.findByKey(modelKey).ifPresentOrElse(model -> {
                log.info("Model {} is present.", modelKey);
                initStatistics(analyticModel, model);
            }, () -> {
                log.info("Model {} is not present.  Creating DB record.", modelKey);
                Model model = modelRepo.save(new Model(modelKey, analyticModel.getModelName()));
                initStatistics(analyticModel, model);
            });
        }
    }

    private void initStatistics(AnalyticModel analyticModel, Model model) {
        analyticModel.getModelStatistics().forEach(s -> statRepo.findByKey(s.getKey())
                .ifPresentOrElse(ss -> {
                    if (!Objects.equals(ss.getModelId(), model.getId())) {
                        log.info("For model {} stat {} exists with the incorrect mapping.  Updating modelId", model.getKey(), ss.getKey());
                        ss.setModelId(model.getId());
                        statRepo.save(ss);
                    } else {
                        log.info("For model {} stat {} exists with the correct mapping.", model.getKey(), ss.getKey());
                    }
                }, () -> {
                    log.info("For model {} stat {} does not exist.  Creating.", model.getKey(), s.getKey());
                    s.setModelId(model.getId());
                    statRepo.save(s);
                }));
    }

    @Cacheable("statistics/series")
    public Optional<Series> getSeries(String modelKey, String statKey, Integer year) {
        return seasonRepo.findFirstByYear(year).flatMap(s ->
                modelRunRepo.findByModelKeyAndSeasonId(modelKey, s.getId()).flatMap(mr ->
                        seriesRepo.findByModelRunIdAndStatisticKey(mr.getId(), statKey)
                )
        );
    }

    public List<String> getModelKeys() {
        return new ArrayList<>(models.keySet());
    }

    public void runModel(int year, String key) {
        if (models.containsKey(key)) {
            runModel(year, models.get(key));
        }
    }

    public void runModel(int year, AnalyticModel model) {
        seasonRepo.findFirstByYear(year).ifPresentOrElse(s -> {
            long t = System.currentTimeMillis();
            log.info("Loaded season for year {}.  Starting model", year);
            Map<String, Map<LocalDate, Map<Long, Double>>> results = model.runModel(s);
            long u = System.currentTimeMillis();
            log.info("Completed running model {} for {}.  Writing to DB.", model.getModelKey(), year);
            saveModel(model.getModelKey(), s.getId(), results);
            long v = System.currentTimeMillis();
            log.info("Completed run for {} in year {}", model.getModelKey(), year);
            log.info("Model calculation took {} ms.", u-t);
            log.info("Model save took {} ms.", v-u);
            log.info("Total time {} ms.", v-t);
        }, () -> log.warn("Could not load season for year {}", year));
    }

    public void saveModel(String key, Long seasonId, Map<String, Map<LocalDate, Map<Long, Double>>> modelData) {
        ModelRun modelRun = findAndClearModelRun(key, seasonId).orElseGet(() -> createModelRun(key, seasonId));
        modelData.forEach((statKey, seriesData) -> statRepo.findByKey(statKey).ifPresent(stat -> saveSeries(modelRun, stat, seriesData)));
    }

    @Transactional
    public void saveSeries(ModelRun modelRun, Statistic stat, Map<LocalDate, Map<Long, Double>> seriesData){
        Series series = seriesRepo.save(new Series(modelRun, stat, List.of()));
        List<Snapshot> snapshots = seriesData.keySet().stream().map(k -> new Snapshot(series, k, List.of())).collect(Collectors.toList());

        snapRepo.saveAll(snapshots).stream().parallel().forEach(snap->{
            Map<Long, Double> data = seriesData.get(snap.getDate());
            obsRepo.saveAll( data.entrySet().stream().map(t -> new Observation(snap, t.getKey(), t.getValue())).collect(Collectors.toList()));
        });
    }

    public Optional<ModelRun> findAndClearModelRun(String key, Long seasonId) {
        return modelRunRepo.findByModelKeyAndSeasonId(key, seasonId).map(m -> {
            for (Series series : m.getSeriesList()) {
                seriesRepo.delete(series);
            }
            m.setRunDate(LocalDateTime.now());
            m.getSeriesList().clear();
            return modelRunRepo.save(m);
        });
    }

    public ModelRun createModelRun(String key, Long seasonId) {
        return modelRepo.findByKey(key).map(mm -> modelRunRepo.save(new ModelRun(mm, seasonId, LocalDateTime.now()))).orElseThrow(RuntimeException::new);
    }

}
