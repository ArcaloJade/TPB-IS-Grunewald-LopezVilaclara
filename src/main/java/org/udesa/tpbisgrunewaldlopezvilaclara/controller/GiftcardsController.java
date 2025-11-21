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

    @Autowired
    public GiftcardsController(GifCardFacade facade) {
        this.facade = facade;
    }

    // capturo el error si crachea y devuelvo mensaje (Emilio lo tiene en TusLibros asi que lo pongo)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage();
        if ("InvalidUser".equals(msg) || "InvalidToken".equals(msg) || "InvalidMerchant".equals(msg)) {
            return ResponseEntity.status(401).body(Map.of("error", msg));
        }
        return ResponseEntity.status(500).body(Map.of("error", "InternalError", "detail", msg));
    }

    // curl -X POST "http://localhost:8080/api/giftcards/login?user=aUser&pass=aPassword"

    //    POST /api/giftcards/login?user=aUser&pass=aPassword
    //    Devuelve un token válido
    //    @PostMapping("/login") public ResponseEntity<Map<String, Object>> login( @RequestParam String user, @RequestParam String pass )

//    @PostMapping(value = "/login", params = {"user", "pass"})
//    public ResponseEntity<UUID> login(
//            @RequestParam String user,
//            @RequestParam String pass
//    ) {
//        return ResponseEntity.ok(facade.login(user, pass));
//    }

    @PostMapping(value = "/login", params = {"user", "pass"}) // LOLO: Antes devolvia un UUID solo, no un Map
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String user,
            @RequestParam String pass
    ) {
        UUID token = facade.login(user, pass);
        return ResponseEntity.ok(Map.of("token", token.toString()));
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

    //    GET /api/giftcards/{cardId}/balance
    //    Consulta saldo de la tarjeta
    //    @GetMapping("/{cardId}/balance") public ResponseEntity<Map<String, Object>> balance( @RequestHeader("Authorization") String header, @PathVariable String cardId ) {

    @GetMapping("/{cardId}/balance")
    public ResponseEntity<Map<String, Object>> balance(
            @RequestHeader("Authorization") String header,
            @PathVariable String cardId
    ) {
        UUID token = UUID.fromString(header.replace("Bearer ", "").trim());
        return ResponseEntity.ok(Map.of("balance", facade.balance(token, cardId)));
    }


    //    GET /api/giftcards/{cardId}/details
    //    Lista los movimientos de la tarjeta
    //    @GetMapping("/{cardId}/details") public ResponseEntity<Map<String, Object>> details( @RequestHeader("Authorization") String tokenHeader, @PathVariable String cardId ) {

    @GetMapping("/{cardId}/details")
    public ResponseEntity<Map<String, Object>> details(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable String cardId
    ) {
        UUID token = UUID.fromString(tokenHeader.replace("Bearer ", "").trim());
        return ResponseEntity.ok(Map.of("details", facade.details(token, cardId)));
    }


    //    POST /api/giftcards/{cardId}/charge?merchant=MerchantCode&amount=anAmount&description=aDescriptio
    //    Un merchant hace un cargo sobre la tarjeta
    //    @PostMapping("/{cardId}/charge") public ResponseEntity<String> charge( @RequestParam String merchant, @RequestParam int amount, @RequestParam String description, @PathVariable String cardId ) {

    @PostMapping("/{cardId}/charge")
    public ResponseEntity<String> charge(
            @RequestParam String merchant,
            @RequestParam int amount,
            @RequestParam String description,
            @PathVariable String cardId
    ){
        facade.charge(merchant, cardId, amount, description);
        return ResponseEntity.ok("OK");
    }




}
