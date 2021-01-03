package com.fijimf.deepfijomega.entity.stats;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "series")
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "model_run_id")
    private ModelRun modelRun;

    @ManyToOne
    @JoinColumn(name = "statistic_id")
    private Statistic statistic;

    @OneToMany(mappedBy = "series")
    List<Snapshot> snapshots;

    public Series() {
    }

    public Series(ModelRun modelRun, Statistic statistic, List<Snapshot> snapshots) {
        this.id = 0L;
        this.modelRun = modelRun;
        this.statistic = statistic;
        this.snapshots = snapshots;
    }

    public ModelRun getModelRun() {
        return modelRun;
    }

    public void setModelRun(ModelRun modelRun) {
        this.modelRun = modelRun;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public void setStatistic(Statistic statistic) {
        this.statistic = statistic;
    }

    public List<Snapshot> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<Snapshot> snapshots) {
        this.snapshots = snapshots;
    }
}
