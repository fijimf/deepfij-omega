package com.fijimf.deepfijomega.analyticmodel;

import com.fijimf.deepfijomega.entity.schedule.Game;
import com.fijimf.deepfijomega.entity.stats.Statistic;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.*;
import java.util.function.Function;

public class DescriptiveStatsAccumulator implements Accumulator {
    private final Map<Long, DescriptiveStatistics> table = new HashMap<>();
    private final Collection<Long> teamIds;
    private final Function<Game, Double> homeValue;
    private final Function<Game, Double> awayValue;
    private final Map<Statistic, Function<DescriptiveStatistics, Double>> extractors;

    public DescriptiveStatsAccumulator(Collection<Long> teamIds, Function<Game, Double> homeValue, Function<Game, Double> awayValue, Map<Statistic, Function<DescriptiveStatistics, Double>> extractors) {
        this.teamIds = teamIds;
        this.homeValue = homeValue;
        this.awayValue = awayValue;
        this.extractors = extractors;
    }

    @Override
    public List<Statistic>  getStatistics() {
        return new ArrayList<>(extractors.keySet());
    }

    @Override
    public void accumulate(Game g) {
        long awayId = g.getAwayTeam().getId();
        accumulate(g.getHomeTeam().getId(), homeValue.apply(g));
        accumulate(g.getAwayTeam().getId(), awayValue.apply(g));
    }

    private void accumulate(long id, Double x) {
        if (x != null) {
            if (!table.containsKey(id)) {
                table.put(id, new DescriptiveStatistics());
            }
            table.get(id).addValue(x);
        }
    }

    @Override
    public Map<String, Map<Long, Double>> extractValues() {
        Map<String, Map<Long, Double>> allExtractedValues = new HashMap<>();
        extractors.keySet().stream().forEach(sk -> {
            Map<Long, Double> extractedValues = new HashMap<>();
            teamIds.forEach(i -> {
                Optional<Double> x = Optional.ofNullable(table.get(i)).map(extractors.get(sk));
                extractedValues.put(i, x.orElse(sk.getDefaultValue()));
            });
            allExtractedValues.put(sk.getKey(),extractedValues);
        });
        return allExtractedValues;
    }
}
