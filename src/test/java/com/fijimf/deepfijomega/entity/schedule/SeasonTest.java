package com.fijimf.deepfijomega.entity.schedule;

import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SeasonTest {

    @Test
    void getSeasonDates() {
        Season season = new Season(2020);
        List<LocalDate> seasonDates = season.getSeasonDates();
        assertThat(seasonDates).contains(LocalDate.of(2019,11,1), Index.atIndex(0));
        assertThat(seasonDates).contains(LocalDate.of(2020,4,30), Index.atIndex(seasonDates.size()-1));
    }
}