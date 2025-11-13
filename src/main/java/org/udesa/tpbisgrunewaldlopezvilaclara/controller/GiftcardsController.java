package org.udesa.tpbisgrunewaldlopezvilaclara.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.udesa.tpbisgrunewaldlopezvilaclara.model.Clock;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.GifCardFacade;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.GiftCard;

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

    // capturo el error si crachea y devuelvo mensaje (Emilio lo tiene en TusLibros asi que lo pongo)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage();
        if ("InvalidUser".equals(msg) || "InvalidToken".equals(msg)) {
            return ResponseEntity.status(401).body(Map.of("error", msg));
        }
        return ResponseEntity.status(500).body(Map.of("error", "InternalError", "detail", msg));
    }

    // curl -X POST "http://localhost:8080/api/giftcards/login?user=aUser&pass=aPassword"
    @PostMapping(value = "/login", params = {"user", "pass"})
    public ResponseEntity<UUID> login(
            @RequestParam String user,
            @RequestParam String pass
    ) {
        return ResponseEntity.ok(facade.login(user, pass));
    }

    @PostMapping(value = "/redeem", params = {"header", "cardId"})
    public ResponseEntity<String> redeem(
            @RequestHeader ("Authorization") String header,
            @PathVariable String cardId
    ) {
        UUID token = UUID.fromString(header.replace("Bearer ", "").trim());
        facade.redeem(token, cardId);
        return ResponseEntity.ok("OK");
    }

    // GET para probar desde el navegador (SOLO DEBUG)
    // http://localhost:8080/api/giftcards/login?user=aUser&pass=aPassword
    @GetMapping(value = "/login", params = {"user", "pass"})
    public ResponseEntity<UUID> loginGet(
            @RequestParam String user,
            @RequestParam String pass
    ) {
        return ResponseEntity.ok(facade.login(user, pass));
    }

}
