package com.fijimf.deepfijomega.entity.stats;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "model_run")
public class ModelRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;

    @Column(name = "season_id")
    private Long seasonId;

    @Column(name = "run_date")
    private LocalDateTime runDate;

    @OneToMany(mappedBy = "modelRun")
    private List<Series> seriesList;

    public ModelRun() {
    }

    public ModelRun(Model model, Long seasonId, LocalDateTime runDate) {
        this.id = 0L;
        this.model = model;
        this.seasonId = seasonId;
        this.runDate = runDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Long getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
    }

    public LocalDateTime getRunDate() {
        return runDate;
    }

    public void setRunDate(LocalDateTime runDate) {
        this.runDate = runDate;
    }

    public List<Series> getSeriesList() {
        return seriesList;
    }

    public void setSeriesList(List<Series> seriesList) {
        this.seriesList = seriesList;
    }
}