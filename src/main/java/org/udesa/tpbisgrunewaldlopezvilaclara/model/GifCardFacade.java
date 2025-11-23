package org.udesa.tpbisgrunewaldlopezvilaclara.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GifCardFacade {
    public static final String InvalidUser = "InvalidUser";
    public static final String InvalidMerchant = "InvalidMerchant";
    public static final String InvalidToken = "InvalidToken";

    @Autowired private UserService userService;
    @Autowired private GiftCardService giftCardService;
    @Autowired private MerchantService merchantService;
    @Autowired private Clock clock;

    private Map<UUID, UserSession> sessions = new HashMap();

    public UUID login( String userKey, String pass ) {
        UserEntity user = userService.findByName( userKey );
        if ( !user.getPassword().equals( pass ) ) {
            throw new RuntimeException( InvalidUser );
        }

        UUID token = UUID.randomUUID();
        sessions.put( token, new UserSession( userKey, clock ) );
        return token;
    }

    public void redeem( UUID token, String cardId ) {
        String user = findUser( token );
        giftCardService.redeemCard( cardId, user );
    }

    public int balance( UUID token, String cardId ) {
        return ownedCard( token, cardId ).balance();
    }

    public void charge( String merchantKey, String cardId, int amount, String description ) {
        merchantService.findByCode( merchantKey );
        giftCardService.chargeCard( cardId, amount, description );
    }

    public List<String> details( UUID token, String cardId ) {
        return ownedCard( token, cardId ).getCharges();
    }

    private GiftCard ownedCard( UUID token, String cardId ) {
        GiftCard card = giftCardService.findByCardId( cardId );
        if ( !card.isOwnedBy( findUser( token ) ) ) throw new RuntimeException( InvalidToken );
        return card;
    }

    private String findUser( UUID token ) {
        return sessions.computeIfAbsent( token, key -> { throw new RuntimeException( InvalidToken ); } )
                       .userAliveAt( clock );
    }
}
