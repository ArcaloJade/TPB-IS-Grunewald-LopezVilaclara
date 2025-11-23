# TPB-IS-Grunewald-LopezVilaclara
Trabajo práctico B de la materia Ingeniería de Software, Ingeniería en Inteligencia Artificial, Universidad de San Andrés. Primavera 2025.

**Consigna:**

Hola, abajo se transcribe el enunciado del TP B.
La fecha de entrega es Domingo 23 de noviembre, 23:59 hs
La entrega debe hacerse en el repositorio informado por cada grupo incluyendo los fuentes que implementan el TP y sus correspondientes tests.

Saludos

Emilio

Enunciado
Se nos pide implementar un servicio para que permita usar el facade de giftCards a través de una interfaz Api Rest con persistencia
Se definen los siguientes endpoints o recursos.

//    POST /api/giftcards/login?user=aUser&pass=aPassword
//    Devuelve un token válido
@PostMapping("/login") public ResponseEntity<Map<String, Object>> login( @RequestParam String user, @RequestParam String pass ) {

//    POST /api/giftcards/{cardId}/redeem
//    Reclama una tarjeta (header Authorization: Bearer <token>)
@PostMapping("/{cardId}/redeem") public ResponseEntity<String> redeemCard( @RequestHeader("Authorization") String header, @PathVariable String cardId ) {

//    GET /api/giftcards/{cardId}/balance
//    Consulta saldo de la tarjeta
@GetMapping("/{cardId}/balance") public ResponseEntity<Map<String, Object>> balance( @RequestHeader("Authorization") String header, @PathVariable String cardId ) {

//    GET /api/giftcards/{cardId}/details
//    Lista los movimientos de la tarjeta
@GetMapping("/{cardId}/details") public ResponseEntity<Map<String, Object>> details( @RequestHeader("Authorization") String tokenHeader, @PathVariable String cardId ) {

//    POST /api/giftcards/{cardId}/charge?merchant=MerchantCode&amount=anAmount&description=aDescriptio
//     Un merchant hace un cargo sobre la tarjeta
@PostMapping("/{cardId}/charge") public ResponseEntity<String> charge( @RequestParam String merchant, @RequestParam int amount, @RequestParam String description, @PathVariable String cardId ) {

El tp requiere implementar el Controller para brindar la API descripta arriba.
Los servicios Spring asociados para soportar el Controller.
Y la persistencia del estado de la aplicación

El proyecto IntelliJ asociado es el correspondiente a una aplicacion SpringBoot
Se ofrece una implementación de referencia del modelo del juego con la funcionalidad necesaria y compatible.
