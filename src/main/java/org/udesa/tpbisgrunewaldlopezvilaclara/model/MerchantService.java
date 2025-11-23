package org.udesa.tpbisgrunewaldlopezvilaclara.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MerchantService extends ModelService<Merchant, MerchantRepository> {

    @Transactional(readOnly = true)
    public Merchant findByCode( String code ) {
        return repository.findByCode( code )
                .orElseThrow( () -> new RuntimeException( GifCardFacade.InvalidMerchant ));
    }
}