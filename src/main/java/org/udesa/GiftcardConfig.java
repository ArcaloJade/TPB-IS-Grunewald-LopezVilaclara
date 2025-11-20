package org.udesa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.*;

import java.util.List;
import java.util.Map;

@Configuration
public class GiftcardConfig {

    @Bean
    public Clock clock() {
        return new Clock();
    }

    @Bean
    public List<GiftCard> cards() {
        return List.of(
                new GiftCard("C1", 100),
                new GiftCard("C2", 500)
        );
    }

    @Bean
    public Map<String, String> users() {
        return Map.of(
                "aUser", "aPassword",
                "otroUser", "pass123"
        );
    }

    @Bean
    public List<String> merchants() {
        return List.of("M1", "M2", "M3");
    }
}
