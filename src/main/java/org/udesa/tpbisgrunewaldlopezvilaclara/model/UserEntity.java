package org.udesa.tpbisgrunewaldlopezvilaclara.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class UserEntity extends ModelEntity {
    @Column(unique = true) private String name;
    @Column private String password;

    public UserEntity() {
    }

    public UserEntity( String name, String password ) {
        this.name = name;
        this.password = password;
    }
}