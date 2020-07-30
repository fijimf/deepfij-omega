package com.fijimf.deepfijomega.entity.schedule;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "season")
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private int year;

    @OneToMany(mappedBy="seasonId")
    private Set<ConferenceMapping> conferenceMappings;

    protected Season() {
    }

    public Season(int year) {
        this.year = year;
    }

    public long getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public Set<ConferenceMapping> getConferenceMapping(){
       return conferenceMappings;
    }
}
