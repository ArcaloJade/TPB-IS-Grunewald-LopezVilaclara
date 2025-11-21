package org.udesa.tpbisgrunewaldlopezvilaclara;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.GifCardFacade;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GifCardFacadeSpringTest {

    @Autowired
    GifCardFacade facade;

    @Test
    void facadeLoadsAsService() {
        // Esto lo hago solo para confirmar q el Facade carga como Service (lo convertí en uno)
        assertNotNull(facade);
    }
}
