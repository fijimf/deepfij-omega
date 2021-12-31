package com.fijimf.deepfijomega.entity.schedule;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "season")
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private int year;

    @OneToMany(mappedBy = "seasonId", fetch = FetchType.EAGER)
    private Set<ConferenceMapping> conferenceMappings;

    @OneToMany(mappedBy = "seasonId", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
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

    public Optional<LocalDateTime> getLastUpdatedAt(){
        return games.stream().map(Game::lastUpdated).max(Comparator.naturalOrder());
    }

    public boolean inSeason(LocalDate today) {
        LocalDate end = LocalDate.of(year, 4, 30);
        return today.isAfter(end.minusYears(1)) && today.isBefore(end);
    }

    public static int dateToSeasonYear(LocalDate d) {
        if (d.getMonthValue() > 10) {
            return d.getYear() + 1;
        } else {
            return d.getYear();
        }
    }

    public List<LocalDate> getSeasonDates() {
        LocalDate start = LocalDate.of(year - 1, 11, 1);
        return start
                .datesUntil(LocalDate.of(year, 5, 1))
                .collect(Collectors.toList());
    }

    public LocalDate getFirstDate() {
       return games.stream().map(Game::getDate).min(LocalDate::compareTo).orElse(LocalDate.of(year - 1, 11, 1));
    }

    public Long getTeamConference(Team t) {
        return getConferenceMapping()
                .stream()
                .filter(cm -> (cm.getTeamId() == t.getId()))
                .findFirst()
                .map(ConferenceMapping::getConferenceId)
                .orElse(-1L);
    }

    public List<Long> getConferenceTeams(Long cid) {
        return getConferenceMapping()
                .stream()
                .filter(cm -> cm.getConferenceId() == cid)
                .map(ConferenceMapping::getTeamId)
                .collect(Collectors.toList());
    }

}
