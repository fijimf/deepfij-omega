package com.fijimf.deepfijomega.scraping;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CasablancaTest {
    @Test
    public void parseCurrentPeriodString(){
        assertThat(Casablanca.getNumPeriods(null)).isEqualTo(2);
        assertThat(Casablanca.getNumPeriods("")).isEqualTo(2);
        assertThat(Casablanca.getNumPeriods("final")).isEqualTo(2);
        assertThat(Casablanca.getNumPeriods("Final")).isEqualTo(2);
        assertThat(Casablanca.getNumPeriods("FINAL")).isEqualTo(2);
        
        assertThat(Casablanca.getNumPeriods("Final/OT")).isEqualTo(3);
        assertThat(Casablanca.getNumPeriods("Final(OT)")).isEqualTo(3);
        assertThat(Casablanca.getNumPeriods("FINAL(OT)")).isEqualTo(3);        
        assertThat(Casablanca.getNumPeriods("Final (OT)")).isEqualTo(3);
        assertThat(Casablanca.getNumPeriods("FINAL (OT)")).isEqualTo(3);
        
        assertThat(Casablanca.getNumPeriods("Final/2OT")).isEqualTo(4);
        assertThat(Casablanca.getNumPeriods("Final(2OT)")).isEqualTo(4);
        assertThat(Casablanca.getNumPeriods("FINAL(2OT)")).isEqualTo(4);        
        assertThat(Casablanca.getNumPeriods("Final (2OT)")).isEqualTo(4);
        assertThat(Casablanca.getNumPeriods("FINAL (2OT)")).isEqualTo(4);
               
        assertThat(Casablanca.getNumPeriods("Final/3OT")).isEqualTo(5);
        assertThat(Casablanca.getNumPeriods("Final(3OT)")).isEqualTo(5);
        assertThat(Casablanca.getNumPeriods("FINAL(3OT)")).isEqualTo(5);        
        assertThat(Casablanca.getNumPeriods("Final (3OT)")).isEqualTo(5);
        assertThat(Casablanca.getNumPeriods("FINAL (3OT)")).isEqualTo(5);
              
        assertThat(Casablanca.getNumPeriods("Final/4OT")).isEqualTo(6);
        assertThat(Casablanca.getNumPeriods("Final(4OT)")).isEqualTo(6);
        assertThat(Casablanca.getNumPeriods("FINAL(4OT)")).isEqualTo(6);        
        assertThat(Casablanca.getNumPeriods("Final (4OT)")).isEqualTo(6);
        assertThat(Casablanca.getNumPeriods("FINAL (4OT)")).isEqualTo(6);
                
        assertThat(Casablanca.getNumPeriods("Final/5OT")).isEqualTo(7);
        assertThat(Casablanca.getNumPeriods("Final(5OT)")).isEqualTo(7);
        assertThat(Casablanca.getNumPeriods("FINAL(5OT)")).isEqualTo(7);        
        assertThat(Casablanca.getNumPeriods("Final (5OT)")).isEqualTo(7);
        assertThat(Casablanca.getNumPeriods("FINAL (5OT)")).isEqualTo(7);
        
    }
}
