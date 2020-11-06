package com.fijimf.deepfijomega.integration.scraping;

import com.fijimf.deepfijomega.scraping.UpdateCandidate;
import com.fijimf.deepfijomega.scraping.Web1NcaaScraper;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class Web1NcaaScrapeTest {
    @Test
    public void parseSampleHtml() throws IOException, TransformerException, ParserConfigurationException, SAXException {
        Web1NcaaScraper scraper = new Web1NcaaScraper();
        Document document = scraper.loadFromReader(new InputStreamReader(new ClassPathResource("egWeb1.html").getInputStream()));
        assertThat(document).isNotNull();
    }

    @Test
    public void extractGameRows() throws IOException, TransformerException, ParserConfigurationException, SAXException, XPathExpressionException {
        Web1NcaaScraper scraper = new Web1NcaaScraper();
        Document document = scraper.loadFromReader(new InputStreamReader(new ClassPathResource("egWeb1.html").getInputStream()));
        List<Optional<UpdateCandidate>> elements = scraper.extractGameRows(document);

        assertThat(elements).isNotNull();
    }
}
