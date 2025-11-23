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

    private String cardId = "C123";
    private String USER_1 = "aUser";
    private String PASSWORD_1 = "aPassword";
    private String USER_2 = "badUser";
    private String PASSWORD_2 = "badPass";

    private String description = "Compra en tienda";
    private int amount = 100;

    private String AUTHORIZATION = "Authorization";
    private String URL_TEMPLATE = "/api/giftcards/";
    private String BEARER = "Bearer ";

    private String INVALID_TOKEN = "InvalidToken";
    private String INVALID_USER = "InvalidUser";
    private String INTERNAL_ERROR = "InternalError";
    private String NO_CARD_ERROR = "CardNotFound";

    @MockBean GifCardFacade facade;

    @Test
    public void test01LoginSuccessReturnsToken() throws Exception {
        UUID fakeToken = UUID.randomUUID();

        when(facade.login(USER_1, PASSWORD_1)).thenReturn(fakeToken);

        mockMvc.perform(
                        post(URL_TEMPLATE + "login")
                                .param("user", USER_1)
                                .param("pass", PASSWORD_1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.token").value(fakeToken.toString()));
    }

    @Test
    public void test02LoginFailsWithInvalidUser() throws Exception {
        doThrow(new RuntimeException(INVALID_USER))
                .when(facade).login(USER_2, PASSWORD_2);

        mockMvc.perform(
                        post(URL_TEMPLATE + "login")
                                .param("user", USER_2)
                                .param("pass", PASSWORD_2)
                )
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.error").value(INVALID_USER));
    }

    @Test
    public void test03RedeemSuccess() throws Exception {
        UUID token = UUID.randomUUID();

        doNothing().when(facade).redeem(eq(token), eq(cardId));

        mockMvc.perform(
                        post(URL_TEMPLATE + cardId + "/redeem")
                                .header(AUTHORIZATION, BEARER + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().string("OK"));
    }


    @Test
    public void test04RedeemFailsWithInvalidToken() throws Exception {
        UUID token = UUID.randomUUID();

        doThrow(new RuntimeException(INVALID_TOKEN))
                .when(facade).redeem(token, cardId);

        mockMvc.perform(
                        post(URL_TEMPLATE + cardId + "/redeem")
                                .header(AUTHORIZATION, BEARER + token)
                )
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.error").value(INVALID_TOKEN));
    }

    @Test
    public void test05RedeemFailsWithInternalError() throws Exception {
        UUID token = UUID.randomUUID();

        doThrow(new RuntimeException("Allahu Akbar"))
                .when(facade).redeem(token, cardId);

        mockMvc.perform(
                        post(URL_TEMPLATE + cardId + "/redeem")
                                .header(AUTHORIZATION, BEARER + token)
                )
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.error").value(INTERNAL_ERROR))
                .andExpect(jsonPath("$.detail").value("Allahu Akbar"));
    }

    @Test
    public void test06BalanceSuccess() throws Exception {
        UUID token = UUID.randomUUID();
        int fakeBalance = 500;

        when(facade.balance(token, cardId)).thenReturn(fakeBalance);

        mockMvc.perform(
                        get(URL_TEMPLATE + cardId + "/balance")
                                .header(AUTHORIZATION, BEARER + token)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(fakeBalance));
    }

    @Test
    public void test07BalanceFailsWithInvalidToken() throws Exception {
        UUID token = UUID.randomUUID();

        doThrow(new RuntimeException(INVALID_TOKEN))
                .when(facade).balance(token, cardId);

        mockMvc.perform(
                        get(URL_TEMPLATE + cardId + "/balance")
                                .header(AUTHORIZATION, BEARER + token)
                )
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.error").value(INVALID_TOKEN));
    }

    @Test
    public void test08BalanceFailsWithInternalError() throws Exception {
        UUID token = UUID.randomUUID();

        doThrow(new RuntimeException(NO_CARD_ERROR))
                .when(facade).balance(token, cardId);

        mockMvc.perform(
                        get(URL_TEMPLATE + cardId + "/balance")
                                .header(AUTHORIZATION, BEARER + token)
                )
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.error").value(INTERNAL_ERROR))
                .andExpect(jsonPath("$.detail").value(NO_CARD_ERROR));
    }

    @Test
    public void test09DetailsSuccess() throws Exception {
        UUID token = UUID.randomUUID();

        List<String> fakeDetails = Arrays.asList("mov1", "mov2");

        when(facade.details(token, cardId)).thenReturn(fakeDetails);

        mockMvc.perform(
                        get(URL_TEMPLATE + cardId + "/details")
                                .header(AUTHORIZATION, BEARER + token)
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

        doThrow(new RuntimeException(INVALID_TOKEN))
                .when(facade).details(token, cardId);

        mockMvc.perform(
                        get(URL_TEMPLATE + cardId + "/details")
                                .header(AUTHORIZATION, BEARER + token)
                )
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.error").value(INVALID_TOKEN));
    }

    @Test
    public void test11DetailsFailsWithInternalError() throws Exception {
        UUID token = UUID.randomUUID();

        doThrow(new RuntimeException(NO_CARD_ERROR))
                .when(facade).details(token, cardId);

        mockMvc.perform(
                        get(URL_TEMPLATE + cardId + "/details")
                                .header(AUTHORIZATION, BEARER + token)
                )
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.error").value(INTERNAL_ERROR))
                .andExpect(jsonPath("$.detail").value(NO_CARD_ERROR));
    }

    @Test
    public void test12ChargeSuccess() throws Exception {
        String merchant = "M01";

        doNothing().when(facade).charge(merchant, cardId, amount, description);

        mockMvc.perform(
                        post(URL_TEMPLATE + cardId + "/charge")
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
        String merchant = "BAD_MERCHANT";

        doThrow(new RuntimeException("InvalidMerchant"))
                .when(facade).charge(merchant, cardId, amount, description);

        mockMvc.perform(
                        post(URL_TEMPLATE + cardId + "/charge")
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
        String merchant = "M01";

        doThrow(new RuntimeException(NO_CARD_ERROR))
                .when(facade).charge(merchant, cardId, amount, description);

        mockMvc.perform(
                        post(URL_TEMPLATE + cardId + "/charge")
                                .param("merchant", merchant)
                                .param("amount", String.valueOf(amount))
                                .param("description", description)
                )
                .andDo(print())
                .andExpect(status().is(500))
                .andExpect(jsonPath("$.error").value(INTERNAL_ERROR))
                .andExpect(jsonPath("$.detail").value(NO_CARD_ERROR));
    }


}