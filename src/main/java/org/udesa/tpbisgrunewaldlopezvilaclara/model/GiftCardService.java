package org.udesa.tpbisgrunewaldlopezvilaclara.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GiftCardService extends ModelService<GiftCard, GiftCardRepository> {

    @Transactional(readOnly = true)
    public GiftCard findByCardId( String cardId ) {
        return repository.findByCardId( cardId )
                .orElseThrow( () -> new RuntimeException( "Card not found: " + cardId ));
    }

    @Transactional
    public GiftCard redeemCard( String cardId, String owner ) {
        GiftCard card = findByCardId( cardId );
        card.redeem( owner );
        return save( card );
    }

    @Transactional
    public GiftCard chargeCard( String cardId, int amount, String description ) {
        GiftCard card = findByCardId( cardId );
        card.charge( amount, description );
        return save( card );
    }
}