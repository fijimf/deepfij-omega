package com.fijimf.deepfijomega.entity.schedule;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "season")
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private int year;

    @OneToMany(mappedBy = "seasonId")
    private Set<ConferenceMapping> conferenceMappings;

    @OneToMany(mappedBy = "seasonId")
    private Set<Game> games;

    protected Season() {
    }

    public Season(int year) {
        this.id = 0L;
        this.year = year;
        this.conferenceMappings = Set.of();
        this.games = Set.of();
    }

    public long getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public Set<ConferenceMapping> getConferenceMapping() {
        return conferenceMappings;
    }

    public Set<Game> getGames() {
        return games;
    }

    public boolean inSeason(LocalDate today) {
        LocalDate end = LocalDate.of(year, 4, 30);
        return today.isAfter(end.minusYears(1)) && today.isBefore(end);
    }
}
