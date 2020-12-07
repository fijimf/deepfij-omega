package com.fijimf.deepfijomega.scraping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CasablancaJsonMappingTest {

    @Test
    public void testParse() throws IOException {
        InputStream inputStream = new ClassPathResource("eg1.json").getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        Casablanca value = mapper.readValue(inputStream, Casablanca.class);
        assertThat(value).isNotNull();
        List<UpdateCandidate> updateCandidates = value.extractUpdates(LocalDate.of(2018,2,5));
        assertThat(updateCandidates)
                .extracting(UpdateCandidate::getAwayKey)
                .containsOnly("indiana", "bucknell", "syracuse", "siena", "hampton",
                        "delaware-st", "norfolk-st", "florida-am", "coppin-st", "howard",
                        "alabama-st", "alabama-am", "alcorn", "west-virginia", "southern-u");
        assertThat(updateCandidates)
                .extracting(UpdateCandidate::getHomeKey)
                .containsOnly("rutgers", "lehigh", "louisville", "fairfield", "nc-central",
                        "nc-at", "bethune-cookman", "md-east-shore", "south-carolina-st", "morgan-st",
                        "ark-pine-bluff", "mississippi-val", "grambling", "oklahoma", "jackson-st");
        assertThat(updateCandidates)
                .extracting(u -> u.getAwayScore().orElse(0))
                .containsOnly(65, 89, 78, 65, 86, 51, 83, 61, 84, 61, 65, 67, 72, 75, 67);
        assertThat(updateCandidates)
                .extracting(u -> u.getHomeScore().orElse(0))
                .containsOnly(43, 92, 73, 78, 70, 54, 79, 62, 60, 97, 59, 77, 81, 73, 62);
        assertThat(updateCandidates)
                .extracting(UpdateCandidate::getDate)
                .allMatch(d -> d.equals(LocalDate.of(2018, 2, 5)));
        assertThat(updateCandidates)
                .extracting(u -> u.getNumPeriods().orElse(0))
                .allMatch(i -> i == 2);

    }


}
