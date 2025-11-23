package org.udesa.tpbisgrunewaldlopezvilaclara;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.GiftCard;

public class GiftCardTest {
    private String description = "Un cargo";

    @Test public void aSimpleCard() {
        assertEquals( 10, newCard().balance() );
    }

    @Test public void aSimpleIsNotOwnedCard() {
        assertFalse( newCard().owned() );
    }

    @Test public void cannotChargeUnownedCards() {
        GiftCard aCard = newCard();
        assertThrows( RuntimeException.class, () -> aCard.charge( 2, description ) );
        assertEquals( 10, aCard.balance() );
        assertTrue( aCard.charges().isEmpty() );
    }

    @Test public void chargeACard() {
        GiftCard aCard = newCard();
        aCard.redeem( "Bob" );
        aCard.charge( 2, description);
        assertEquals( 8, aCard.balance() );
        assertEquals( description, aCard.charges().getLast() );
    }

    @Test public void cannotOverrunACard() {
        GiftCard aCard = newCard();
        assertThrows( RuntimeException.class, () -> aCard.charge( 11, description ) );
        assertEquals( 10, aCard.balance() );
    }

    private GiftCard newCard() {
        return new GiftCard( "GC1", 10 );
    }

}
