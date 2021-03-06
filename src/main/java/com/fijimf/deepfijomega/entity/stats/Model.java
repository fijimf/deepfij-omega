package com.fijimf.deepfijomega.entity.stats;

import javax.persistence.*;

@Entity
@Table(name = "model")
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name="key")
    private String key;

    public Model() {
    }

    public Model(String key, String name) {
        this.id = 0L;
        this.key = key;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}