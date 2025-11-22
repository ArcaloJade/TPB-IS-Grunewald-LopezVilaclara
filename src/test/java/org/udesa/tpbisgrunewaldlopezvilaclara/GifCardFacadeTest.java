package org.udesa.tpbisgrunewaldlopezvilaclara;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.Clock;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.GifCardFacade;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.GiftCard;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.GiftCardRepository;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.GiftCardService;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.Merchant;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.MerchantRepository;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.MerchantService;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.UserEntity;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.UserRepository;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.UserService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GifCardFacadeTest {

    @Autowired private GifCardFacade facade;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private GiftCardService giftCardService;
    @Autowired private GiftCardRepository giftCardRepository;
    @Autowired private MerchantService merchantService;
    @Autowired private MerchantRepository merchantRepository;

//    @Autowired private Clock clock;
//    @MockBean private Clock clock;
    @SpyBean private Clock clock; // SpyBean me deja cambiar algunas llamadas a now() sin romper las demás

    // Se espera que el usuario pueda inciar sesion con usuario y password y obtener un token
    //    debe poder usar el token para gestionar la tarjeta.
    //    el token se vence a los 5'

    // las giftcards ya estan definidas en el sistema.
    //    el usuario las reclama, pueden ser varias
    //    puede consultar el saldo y el detalle de gastos de sus tarjetas

    // los merchants pueden hacer cargos en las tarjetas que hayan sido reclamadas.
    //    los cargos se actualizan en el balance de las tarjetas

    @BeforeEach
    public void setUp() {
        if (userRepository.findByName("Bob").isEmpty()) {
            userService.save(new UserEntity("Bob", "BobPass"));
        }
        if (userRepository.findByName("Kevin").isEmpty()) {
            userService.save(new UserEntity("Kevin", "KevPass"));
        }
        if (giftCardRepository.findByCardId("GC1").isEmpty()) {
            giftCardService.save(new GiftCard("GC1", 10));
        }
        if (giftCardRepository.findByCardId("GC2").isEmpty()) {
            giftCardService.save(new GiftCard("GC2", 5));
        }
        if (merchantRepository.findByCode("M1").isEmpty()) {
            merchantService.save(new Merchant("M1"));
        }
    }

    @Test public void userCanOpenASession() {
        assertNotNull( facade.login( "Bob", "BobPass" ) );
    }

    @Test public void unkownUserCannorOpenASession() {
        assertThrows( RuntimeException.class, () -> facade.login( "Stuart", "StuPass" ) );
    }

    @Test public void userCannotUseAnInvalidtoken() {
        assertThrows( RuntimeException.class, () -> facade.redeem( UUID.randomUUID(), "GC1" ) );
        assertThrows( RuntimeException.class, () -> facade.balance( UUID.randomUUID(), "GC1" ) );
        assertThrows( RuntimeException.class, () -> facade.details( UUID.randomUUID(), "GC1" ) );
    }

    @Test public void userCannotCheckOnAlienCard() {
        UUID token = facade.login( "Bob", "BobPass" );

        assertThrows( RuntimeException.class, () -> facade.balance( token, "GC1" ) );
    }

    @Test public void userCanRedeeemACard() {
        UUID token = facade.login( "Bob", "BobPass" );

        facade.redeem( token, "GC1" );
        assertEquals( 10, facade.balance( token, "GC1" ) );
    }

    @Test public void userCanRedeeemASecondCard() {
        UUID token = facade.login( "Bob", "BobPass" );

        facade.redeem( token, "GC1" );
        facade.redeem( token, "GC2" );

        assertEquals( 10, facade.balance( token, "GC1" ) );
        assertEquals( 5, facade.balance( token, "GC2" ) );
    }

    @Test public void multipleUsersCanRedeeemACard() {
        UUID bobsToken = facade.login( "Bob", "BobPass" );
        UUID kevinsToken = facade.login( "Kevin", "KevPass" );

        facade.redeem( bobsToken, "GC1" );
        facade.redeem( kevinsToken, "GC2" );

        assertEquals( 10, facade.balance( bobsToken, "GC1" ) );
        assertEquals( 5, facade.balance( kevinsToken, "GC2" ) );
    }

    @Test public void unknownMerchantCantCharge() {
        assertThrows( RuntimeException.class, () -> facade.charge( "Mx", "GC1", 2, "UnCargo" ) );

    }

    @Test public void merchantCantChargeUnredeemedCard() {
        assertThrows( RuntimeException.class, () -> facade.charge( "M1", "GC1", 2, "UnCargo" ) );
    }

    @Test public void merchantCanChargeARedeemedCard() {
        UUID token = facade.login( "Bob", "BobPass" );

        facade.redeem( token, "GC1" );
        facade.charge( "M1", "GC1", 2, "UnCargo" );

        assertEquals( 8, facade.balance( token, "GC1" ) );
    }

    @Test public void merchantCannotOverchargeACard() {
        UUID token = facade.login( "Bob", "BobPass" );

        facade.redeem( token, "GC1" );
        assertThrows( RuntimeException.class, () -> facade.charge( "M1", "GC1", 11, "UnCargo" ) );
    }

    @Test public void userCanCheckHisEmptyCharges() {
        UUID token = facade.login( "Bob", "BobPass" );

        facade.redeem( token, "GC1" );

        assertTrue( facade.details( token, "GC1" ).isEmpty() );
    }

    @Test public void userCanCheckHisCharges() {
        UUID token = facade.login( "Bob", "BobPass" );

        facade.redeem( token, "GC1" );
        facade.charge( "M1", "GC1", 2, "UnCargo" );

        assertEquals( "UnCargo", facade.details( token, "GC1" ).getLast() );
    }

    @Test public void userCannotCheckOthersCharges() {
        facade.redeem( facade.login( "Bob", "BobPass" ), "GC1" );

        UUID token = facade.login( "Kevin", "KevPass" );

        assertThrows( RuntimeException.class, () -> facade.details( token, "GC1" ) );
    }

    @Test public void tokenExpires() {
        LocalDateTime t0 = LocalDateTime.now();
        LocalDateTime t1 = t0.plusMinutes(16);

        // el doReturn es para SpyBean
        doReturn(t0, t1).when(clock).now();

        UUID token = facade.login("Kevin", "KevPass");

        assertThrows(RuntimeException.class, () -> facade.redeem(token, "GC1"));
    }

}
