package org.udesa.tpbisgrunewaldlopezvilaclara.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Merchant extends ModelEntity {
    @Column(unique = true) private String code;

    public Merchant() {}

    public Merchant( String code ) {
        this.code = code;
    }
}