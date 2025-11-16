package org.udesa.tpbisgrunewaldlopezvilaclara.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.GifCardFacade;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GiftcardsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired GiftcardsController controller;

    @MockBean GifCardFacade facade;

    // Tests de login

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
//                .andExpect(content().string(fakeToken.toString()));
                .andExpect(jsonPath("$").value(fakeToken.toString()));
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

    // Tests de redeem

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

}
