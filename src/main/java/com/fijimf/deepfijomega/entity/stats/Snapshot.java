package com.fijimf.deepfijomega.entity.stats;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "snapshot")
public class Snapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;

    private LocalDate date;

    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.MERGE)
    private List<Observation> observations;

    @Transient
    private final Map<Long, Double> teamMap = new HashMap<>();

    @Transient
    private final DescriptiveStatistics statistics = new DescriptiveStatistics();

    public Snapshot() {
    }

    public Snapshot(Series series, LocalDate date, List<Observation> observations) {
        this.id = 0L;
        this.series = series;
        this.date = date;
        this.observations = observations;
    }

    public Long getId() {
        return id;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Observation> getObservations() {
        return observations;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }

    @PostLoad
    public void updateTransientValues() {
        statistics.clear();
        teamMap.clear();
        observations.forEach(o -> {
            Double value = o.getValue();
            if (value !=null) {
                teamMap.put(o.getTeamId(), value);
                statistics.addValue(value);
            }
        });
    }

    public Map<Long, Double> getTeamMap() {
        return teamMap;
    }

    public DescriptiveStatistics getStatistics() {
        return statistics;
    }
}
