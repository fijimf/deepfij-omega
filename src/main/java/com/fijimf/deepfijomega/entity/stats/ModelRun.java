package com.fijimf.deepfijomega.entity.stats;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "model_run")
public class ModelRun {
    @Id
    private Long id;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "run_date")
    private LocalDateTime runDate;

    @OneToMany(mappedBy = "modelRun")
    private List<Series> seriesList;

    public ModelRun() {
    }

    public ModelRun( String modelName, LocalDateTime runDate) {
        this.id = 0L;
        this.modelName = modelName;
        this.runDate = runDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
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