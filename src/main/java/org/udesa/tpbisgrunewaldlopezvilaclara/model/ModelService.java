package org.udesa.tpbisgrunewaldlopezvilaclara.model;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class ModelService <M extends ModelEntity, R extends JpaRepository<M, Long>> {
    @Autowired protected R repository;

    @Transactional(readOnly = true)
    public List<M> findAll() {
        return StreamSupport.stream( repository.findAll().spliterator(), false ).toList();
    }

    @Transactional(readOnly = true)
    public M getById( long id ) {
        return getById( id, () -> {
            throw new RuntimeException( "Object of class " + getModelClass() + " and id: " + id + " not found" );
        } );
    }

    public Class<M> getModelClass() {
        return (Class<M>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[ 0 ];
    }

    @Transactional(readOnly = true)
    public M getById( long id, Supplier<? extends M> supplier ) {
        return repository.findById( id ).orElseGet( supplier );
    }

    @Transactional
    public M save( M model ) {
        return repository.save( model );
    }

    @Transactional
    public void delete( long id ) {
        repository.deleteById( id );
    }

    @Transactional
    public void delete( M model ) {
        repository.delete( model );
    }
}

