package org.udesa.tpbisgrunewaldlopezvilaclara;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.UserEntity;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest extends ModelServiceTest<UserEntity, UserService> {

    @Autowired
    private UserService userService;

    @BeforeEach
    void init() {
        // le digo al test genérico qué service concreto usar
        this.service = userService;
    }

    @Override
    protected UserEntity newSample() {
        UserEntity u = new UserEntity();
        u.setName("TestUser_" + System.nanoTime()); // solución al UNIQUE porque explotaba H2
        u.setPassword("pass123");
        return u;
    }

    @Override
    protected UserEntity updateUser(UserEntity user) {
        user.setPassword("newPass456");
        return user;
    }

    @Test
    void test01FindByNameSuccess() {
        UserEntity model = savedSample();

        UserEntity found = service.findByName(model.getName());

        assertNotNull(found);
        assertEquals(model.getId(), found.getId());
        assertEquals(model.getName(), found.getName());
    }

    @Test
    void test02FindByNameThrowsExceptionIfNotFound() {
        assertThrows(RuntimeException.class,
                () -> service.findByName("usuario_que_no_existe"));
    }
}
