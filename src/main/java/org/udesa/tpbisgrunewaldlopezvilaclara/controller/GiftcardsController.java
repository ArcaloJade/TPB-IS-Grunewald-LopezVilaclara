package org.udesa.tpbisgrunewaldlopezvilaclara.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    // LOLO: Dejo esto comentado x las dudas, pero el constructor que dejé nos deja inyectar el facade
    // real como hace Emilio, y tbn sigue la estructura q puse para los tests. fuera de eso no afecta otra
    // funcionalidad ni nada de lo que ya hiciste

//    public GiftcardsController() {
//        List<GiftCard> cards = new ArrayList<>();
//        Map<String, String> users = new HashMap<>();
//        users.put("aUser", "aPassword");
//
//        List<String> merchants = new ArrayList<>();
//        merchants.add("m1");
//        merchants.add("m2");
//
//        Clock clock = new Clock();
//
//        this.facade = new GifCardFacade(cards, users, merchants, clock);
//    }

    @Autowired
    public GiftcardsController(GifCardFacade facade) {
        this.facade = facade;
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

    //    POST /api/giftcards/login?user=aUser&pass=aPassword
    //    Devuelve un token válido
    //    @PostMapping("/login") public ResponseEntity<Map<String, Object>> login( @RequestParam String user, @RequestParam String pass )

    @PostMapping(value = "/login", params = {"user", "pass"})
    public ResponseEntity<UUID> login(
            @RequestParam String user,
            @RequestParam String pass
    ) {
        return ResponseEntity.ok(facade.login(user, pass));
    }

    //    POST /api/giftcards/{cardId}/redeem
    //    Reclama una tarjeta (header Authorization: Bearer <token>)
    //    @PostMapping("/{cardId}/redeem") public ResponseEntity<String> redeemCard( @RequestHeader("Authorization") String header, @PathVariable String cardId )

    @PostMapping(value = "/{cardId}/redeem") // LOLO: saqué param=... porque NO usa parámetros (?x=...)
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
