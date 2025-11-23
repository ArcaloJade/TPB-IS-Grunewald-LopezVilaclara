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

    @SpyBean private Clock clock;

    UUID token1 = UUID.randomUUID();
    
    private String USER_1 = "Bob";
    private String PASSWORD_1 = "BobPass";
    private String GiftCard1 = "GC1";
    
    private String USER_2 = "Kevin";
    private String PASSWORD_2 = "KevPass";
    private String GiftCard2= "GC2";
    
    private String MERCHANT_1 = "M1";
    private String description = "unCargo";


    @BeforeEach
    public void setUp() {
        if (userRepository.findByName(USER_1).isEmpty()) {
            userService.save(new UserEntity(USER_1, PASSWORD_1));
        }
        if (userRepository.findByName(USER_2).isEmpty()) {
            userService.save(new UserEntity(USER_2, PASSWORD_2));
        }
        if (giftCardRepository.findByCardId(GiftCard1).isEmpty()) {
            giftCardService.save(new GiftCard(GiftCard1, 10));
        }
        if (giftCardRepository.findByCardId(GiftCard2).isEmpty()) {
            giftCardService.save(new GiftCard(GiftCard2, 5));
        }
        if (merchantRepository.findByCode(MERCHANT_1).isEmpty()) {
            merchantService.save(new Merchant(MERCHANT_1));
        }

        token1 = facade.login( USER_1, PASSWORD_1 );
    }

    @Test public void userCanOpenASession() {
        assertNotNull( facade.login( USER_1, PASSWORD_1 ) );
    }

    @Test public void unkownUserCannorOpenASession() {
        assertThrows( RuntimeException.class, () -> facade.login( "Stuart", "StuPass" ) );
    }

    @Test public void userCannotUseAnInvalidtoken() {
        assertThrows( RuntimeException.class, () -> facade.redeem( UUID.randomUUID(), GiftCard1 ) );
        assertThrows( RuntimeException.class, () -> facade.balance( UUID.randomUUID(), GiftCard1 ) );
        assertThrows( RuntimeException.class, () -> facade.details( UUID.randomUUID(), GiftCard1 ) );
    }

    @Test public void userCannotCheckOnAlienCard() {
        assertThrows( RuntimeException.class, () -> facade.balance( token1, GiftCard1 ) );
    }

    @Test public void userCanRedeeemACard() {
        facade.redeem( token1, GiftCard1 );
        assertEquals( 10, facade.balance( token1, GiftCard1 ) );
    }

    @Test public void userCanRedeeemASecondCard() {
        facade.redeem( token1, GiftCard1 );
        facade.redeem( token1, GiftCard2 );

        assertEquals( 10, facade.balance( token1, GiftCard1 ) );
        assertEquals( 5, facade.balance( token1, GiftCard2 ) );
    }

    @Test public void multipleUsersCanRedeeemACard() {
        UUID bobsToken = facade.login( USER_1, PASSWORD_1 );
        UUID kevinsToken = facade.login( USER_2, PASSWORD_2 );

        facade.redeem( bobsToken, GiftCard1 );
        facade.redeem( kevinsToken, GiftCard2 );

        assertEquals( 10, facade.balance( bobsToken, GiftCard1 ) );
        assertEquals( 5, facade.balance( kevinsToken, GiftCard2 ) );
    }

    @Test public void unknownMerchantCantCharge() {
        assertThrows( RuntimeException.class, () -> facade.charge( "Mx", GiftCard1, 2, description ) );

    }

    @Test public void merchantCantChargeUnredeemedCard() {
        assertThrows( RuntimeException.class, () -> facade.charge( MERCHANT_1, GiftCard1, 2, description ) );
    }

    @Test public void merchantCanChargeARedeemedCard() {
        facade.redeem( token1, GiftCard1 );
        facade.charge( MERCHANT_1, GiftCard1, 2, description );

        assertEquals( 8, facade.balance( token1, GiftCard1 ) );
    }

    @Test public void merchantCannotOverchargeACard() {
        facade.redeem( token1, GiftCard1 );
        assertThrows( RuntimeException.class, () -> facade.charge( MERCHANT_1, GiftCard1, 11, description ) );
    }

    @Test public void userCanCheckHisEmptyCharges() {
        facade.redeem( token1, GiftCard1 );

        assertTrue( facade.details( token1, GiftCard1 ).isEmpty() );
    }

    @Test public void userCanCheckHisCharges() {
        facade.redeem( token1, GiftCard1 );
        facade.charge( MERCHANT_1, GiftCard1, 2, description );

        assertEquals( description, facade.details( token1, GiftCard1 ).getLast() );
    }

    @Test public void userCannotCheckOthersCharges() {
        facade.redeem( facade.login( USER_1, PASSWORD_1 ), GiftCard1 );
        UUID token = facade.login( USER_2, PASSWORD_2 );

        assertThrows( RuntimeException.class, () -> facade.details( token, GiftCard1 ) );
    }

    @Test public void tokenExpires() {
        LocalDateTime t0 = LocalDateTime.now();
        LocalDateTime t1 = t0.plusMinutes(16);

        doReturn(t0, t1).when(clock).now();

        UUID token = facade.login(USER_2, PASSWORD_2);

        assertThrows(RuntimeException.class, () -> facade.redeem(token, GiftCard1));
    }

}
