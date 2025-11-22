package org.udesa.tpbisgrunewaldlopezvilaclara;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.ModelEntity;
import org.udesa.tpbisgrunewaldlopezvilaclara.model.ModelService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ModelServiceTest<
        M extends ModelEntity,
        S extends ModelService<M, ? extends JpaRepository<M, Long>>> {


    @Autowired protected S service;

    protected M model;

    protected abstract M newSample(); // entidad de ejemplo

    protected abstract M updateUser(M entity);

    protected M savedSample() {
        return service.save(newSample());
    }

    @BeforeEach
    void setUp() {
        model = savedSample();
    }

    @Test
    public void test01EntitySave() {
        M newModel = newSample();
        M retrieved = service.save(newModel);

        assertNotNull(retrieved.getId());
        assertNotNull(newModel.getId());
        assertEquals(retrieved, newModel);
    }

    @Test
    public void test02EntityUpdate() {
        updateUser(model);
        service.save(model);

        M retrieved = service.getById(model.getId());
        assertEquals(model, retrieved);
    }

    @Test
    public void test03DeletionByObject() {
        service.delete(model);
        assertThrows(RuntimeException.class, () -> service.getById(model.getId()));
    }

    @Test
    public void test04DeletionById() {
        service.delete(model.getId());
        assertThrows(RuntimeException.class, () -> service.getById(model.getId()));
    }

    @Test
    public void test05DeletionByProxy() throws Exception {
        M proxy = service.getModelClass().getConstructor().newInstance();
        proxy.setId(model.getId());

        service.delete(proxy);
        assertThrows(RuntimeException.class, () -> service.getById(model.getId()));
    }

    @Test
    public void test06FindAll() {
        List<M> list = service.findAll();
        assertFalse(list.isEmpty());
        assertTrue(list.contains(model));
    }
}

