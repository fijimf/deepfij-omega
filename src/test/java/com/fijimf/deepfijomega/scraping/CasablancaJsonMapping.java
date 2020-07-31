package com.fijimf.deepfijomega.scraping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CasablancaJsonMapping {

    @Test
    public void testParse() throws IOException {
        InputStream inputStream = new ClassPathResource("eg1.json").getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        Casablanca value = mapper.readValue(inputStream, Casablanca.class);
        assertNotNull(value);
    }
}
