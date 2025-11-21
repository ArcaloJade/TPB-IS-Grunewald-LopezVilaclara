package org.udesa.tpbisgrunewaldlopezvilaclara.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends ModelService<UserEntity, UserRepository> {

    protected void updateData( UserEntity existingObject, UserEntity updatedObject) {
        existingObject.setName( updatedObject.getName() );
        existingObject.setPassword( updatedObject.getPassword() );
    }

    @Transactional(readOnly = true)
    public UserEntity findByName( String name ) {
        return repository.findByName( name )
                .orElseThrow( () -> new RuntimeException( GifCardFacade.InvalidUser ));
    }
}