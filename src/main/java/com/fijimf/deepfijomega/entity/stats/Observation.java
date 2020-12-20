package com.fijimf.deepfijomega.entity.stats;

import javax.persistence.*;

@Entity
@Table(name = "observation")
public class Observation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name="snapshot_id")
    private Snapshot snapshot;

    @Column(name="team_id")
    private Long teamId;
    private Double value;

    public Observation() {
    }

    public Observation(Snapshot snapshot, Long teamId, Double value) {
        this.id=0L;
        this.snapshot = snapshot;
        this.teamId = teamId;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
