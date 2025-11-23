package org.udesa.tpbisgrunewaldlopezvilaclara.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.GifCardFacade;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GiftcardsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired GiftcardsController controller;

    @MockBean GifCardFacade facade;

    @Test
    public void test01LoginSuccessReturnsToken() throws Exception {
        UUID fakeToken = UUID.randomUUID();

        when(facade.login("aUser", "aPassword")).thenReturn(fakeToken);

        mockMvc.perform(
                        post("/api/giftcards/login")
                                .param("user", "aUser")
                                .param("pass", "aPassword")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.token").value(fakeToken.toString()));
    }

    @Test
    public void test02LoginFailsWithInvalidUser() throws Exception {
        doThrow(new RuntimeException("InvalidUser"))
                .when(facade).login("badUser", "badPass");

        mockMvc.perform(
                        post("/api/giftcards/login")
                                .param("user", "badUser")
                                .param("pass", "badPass")
                )
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.error").value("InvalidUser"));
    }

    @Test
    public void test03RedeemSuccess() throws Exception {
        UUID token = UUID.randomUUID();
        String cardId = "C123";

        doNothing().when(facade).redeem(eq(token), eq(cardId));

        mockMvc.perform(
                        post("/api/giftcards/" + cardId + "/redeem")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().string("OK"));
    }


    @Test
    public void test04RedeemFailsWithInvalidToken() throws Exception {
        UUID token = UUID.randomUUID();

        doThrow(new RuntimeException("InvalidToken"))
                .when(facade).redeem(token, "C123");

        mockMvc.perform(
                        post("/api/giftcards/C123/redeem")
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.error").value("InvalidToken"));
    }

    @Test
    public void test05RedeemFailsWithInternalError() throws Exception {
        UUID token = UUID.randomUUID();

        doThrow(new RuntimeException("Allahu Akbar"))
                .when(facade).redeem(token, "C123");

        mockMvc.perform(
                        post("/api/giftcards/C123/redeem")
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.error").value("InternalError"))
                .andExpect(jsonPath("$.detail").value("Allahu Akbar"));
    }

    @Test
    public void test06BalanceSuccess() throws Exception {
        UUID token = UUID.randomUUID();
        String cardId = "C123";
        int fakeBalance = 500;

        when(facade.balance(token, cardId)).thenReturn(fakeBalance);

        mockMvc.perform(
                        get("/api/giftcards/" + cardId + "/balance")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(fakeBalance));
    }

    @Test
    public void test07BalanceFailsWithInvalidToken() throws Exception {
        UUID token = UUID.randomUUID();
        String cardId = "C123";

        doThrow(new RuntimeException("InvalidToken"))
                .when(facade).balance(token, cardId);

        mockMvc.perform(
                        get("/api/giftcards/" + cardId + "/balance")
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.error").value("InvalidToken"));
    }

    @Test
    public void test08BalanceFailsWithInternalError() throws Exception {
        UUID token = UUID.randomUUID();
        String cardId = "C123";

        doThrow(new RuntimeException("CardNotFound"))
                .when(facade).balance(token, cardId);

        mockMvc.perform(
                        get("/api/giftcards/" + cardId + "/balance")
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.error").value("InternalError"))
                .andExpect(jsonPath("$.detail").value("CardNotFound"));
    }

    @Test
    public void test09DetailsSuccess() throws Exception {
        UUID token = UUID.randomUUID();
        String cardId = "C123";

        List<String> fakeDetails = Arrays.asList("mov1", "mov2");

        when(facade.details(token, cardId)).thenReturn(fakeDetails);

        mockMvc.perform(
                        get("/api/giftcards/" + cardId + "/details")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[0]").value("mov1"))
                .andExpect(jsonPath("$.details[1]").value("mov2"));
    }

    @Test
    public void test10DetailsFailsWithInvalidToken() throws Exception {
        UUID token = UUID.randomUUID();
        String cardId = "C123";

        doThrow(new RuntimeException("InvalidToken"))
                .when(facade).details(token, cardId);

        mockMvc.perform(
                        get("/api/giftcards/" + cardId + "/details")
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.error").value("InvalidToken"));
    }

    @Test
    public void test11DetailsFailsWithInternalError() throws Exception {
        UUID token = UUID.randomUUID();
        String cardId = "C123";

        doThrow(new RuntimeException("CardNotFound"))
                .when(facade).details(token, cardId);

        mockMvc.perform(
                        get("/api/giftcards/" + cardId + "/details")
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.error").value("InternalError"))
                .andExpect(jsonPath("$.detail").value("CardNotFound"));
    }

    @Test
    public void test12ChargeSuccess() throws Exception {
        String cardId = "C123";
        String merchant = "M01";
        int amount = 100;
        String description = "Compra en tienda";

        // No hace nada, simplemente verifica que se llame bien
        doNothing().when(facade).charge(merchant, cardId, amount, description);

        mockMvc.perform(
                        post("/api/giftcards/" + cardId + "/charge")
                                .param("merchant", merchant)
                                .param("amount", String.valueOf(amount))
                                .param("description", description)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().string("OK"));
    }

    @Test
    public void test13ChargeFailsWithInvalidMerchant() throws Exception {
        String cardId = "C123";
        String merchant = "BAD_MERCHANT";
        int amount = 100;
        String description = "Compra en tienda";

        doThrow(new RuntimeException("InvalidMerchant"))
                .when(facade).charge(merchant, cardId, amount, description);

        mockMvc.perform(
                        post("/api/giftcards/" + cardId + "/charge")
                                .param("merchant", merchant)
                                .param("amount", String.valueOf(amount))
                                .param("description", description)
                )
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.error").value("InvalidMerchant"));
    }

    @Test
    public void test14ChargeFailsWithInternalError() throws Exception {
        String cardId = "C123";
        String merchant = "M01";
        int amount = 100;
        String description = "Compra en tienda";

        doThrow(new RuntimeException("CardNotFound"))
                .when(facade).charge(merchant, cardId, amount, description);

        mockMvc.perform(
                        post("/api/giftcards/" + cardId + "/charge")
                                .param("merchant", merchant)
                                .param("amount", String.valueOf(amount))
                                .param("description", description)
                )
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.error").value("InternalError"))
                .andExpect(jsonPath("$.detail").value("CardNotFound"));
    }


}