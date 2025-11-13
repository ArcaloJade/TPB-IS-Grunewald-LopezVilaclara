package org.udesa.tpbisgrunewaldlopezvilaclara.controller;

import org.springframework.web.bind.annotation.*;

import org.udesa.tpbisgrunewaldlopezvilaclara.model.Clock;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.GifCardFacade;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.GiftCard;

import jakarta.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/api/giftcards")
public class GiftcardsController {

    private final GifCardFacade facade;

    public GiftcardsController() {
        List<GiftCard> cards = new ArrayList<>();
        Map<String, String> users = new HashMap<>();
        users.put("aUser", "aPassword");

        List<String> merchants = new ArrayList<>();
        merchants.add("m1");
        merchants.add("m2");

        Clock clock = new Clock();

        this.facade = new GifCardFacade(cards, users, merchants, clock);
    }

    // GET de prueba para navegador (porque nos pide solo POST pero es para chequear que ande)
    // http://localhost:8080/api/giftcards/login?user=aUser&pass=aPassword
    @GetMapping("/login")
    public Map<String, Object> loginGet(
            @RequestParam String user,
            @RequestParam String pass
    ) {
        var token = facade.login(user, pass);
        return Map.of("token", token.toString());
    }

    // --- Versión oficial del enunciado (POST) ---
    // curl -X POST "http://localhost:8080/api/giftcards/login?user=aUser&pass=aPassword"
    @PostMapping("/login")
    public Map<String, Object> loginPost(
            @RequestParam String user,
            @RequestParam String pass
    ) {
        var token = facade.login(user, pass);
        return Map.of("token", token.toString());
    }

}
