package org.udesa.tpbisgrunewaldlopezvilaclara.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends ModelService<UserEntity, UserRepository> {

    @Transactional(readOnly = true)
    public UserEntity findByName( String name ) {
        return repository.findByName( name )
                .orElseThrow( () -> new RuntimeException( GifCardFacade.InvalidUser ));
    }
}