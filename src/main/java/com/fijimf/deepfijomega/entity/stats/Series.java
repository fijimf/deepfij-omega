package com.fijimf.deepfijomega.entity.stats;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="series")
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="model_run_id")
    private ModelRun modelRun;

    @ManyToOne
    @JoinColumn(name = "statistic_id")
    private Statistic statistic;

    @OneToMany(mappedBy = "series")
    List<Snapshot> snapshots;

}
