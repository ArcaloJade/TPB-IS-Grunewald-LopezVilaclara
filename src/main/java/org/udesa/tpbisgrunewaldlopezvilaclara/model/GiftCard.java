package org.udesa.tpbisgrunewaldlopezvilaclara.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
public class GiftCard extends ModelEntity {
    public static final String CargoImposible = "CargoImposible";
    public static final String InvalidCard = "InvalidCard";
    @Column(unique = true) private String cardId;
    @Column private int balance;
    @Column private String owner;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> charges = new ArrayList<>();

    public GiftCard() {}

    public GiftCard( String cardId, int initialBalance ) {
        this.cardId = cardId;
        this.balance = initialBalance;
    }

    public GiftCard charge( int anAmount, String description ) {
        if ( !owned() || ( balance - anAmount < 0 ) ) throw new RuntimeException( CargoImposible );

        balance = balance - anAmount;
        charges.add( description );

        return this;
    }

    public GiftCard redeem( String newOwner ) {
        if ( owned() ) throw new RuntimeException( InvalidCard );

        owner = newOwner;
        return this;
    }

    // proyectors
    public boolean owned() {                            return owner != null;                   }
    public boolean isOwnedBy( String aPossibleOwner ) { return owner.equals( aPossibleOwner );  }

    // accessors
    public String id() {            return String.valueOf(id); }
    public int balance() {          return balance; }
    public List<String> charges() { return charges; }

}
