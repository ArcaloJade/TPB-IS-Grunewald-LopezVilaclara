package org.udesa.tpbisgrunewaldlopezvilaclara;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.Merchant;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.MerchantService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MerchantServiceTest extends ModelServiceTest<Merchant, MerchantService> {

    @Autowired
    private MerchantService merchantService;

    @BeforeEach
    void init() {
        this.service = merchantService;
    }

    @Override
    protected Merchant newSample() {
        return new Merchant("MERC_" + System.nanoTime());
    }

    @Override
    protected Merchant updateUser(Merchant m) {
        m.setCode(m.getCode() + "_UPDATED");
        return m;
    }

    @Test
    void testFindByCodeSuccess() {
        Merchant model = savedSample();

        Merchant found = service.findByCode(model.getCode());

        assertNotNull(found);
        assertEquals(model.getId(), found.getId());
        assertEquals(model.getCode(), found.getCode());
    }

    @Test
    void testFindByCodeFailure() {
        assertThrows(RuntimeException.class,
                () -> service.findByCode("NO_EXISTE_" + System.nanoTime()));
    }
}
