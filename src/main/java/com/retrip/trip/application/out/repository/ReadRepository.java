package com.retrip.trip.application.out.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface ReadRepository<T, ID> extends JpaRepository<T, ID> {

    @Override
    default <S extends T> S save(S entity) {
        throw new UnsupportedOperationException("해당 Repository는 Read만 가능합니다.");
    }

    @Override
    default <S extends T> S saveAndFlush(S entity) {
        throw new UnsupportedOperationException("해당 Repository는 Read만 가능합니다.");
    }

    @Override
    default <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new UnsupportedOperationException("해당 Repository는 Read만 가능합니다.");
    }

    @Override
    default void deleteAllInBatch(Iterable<T> entities) {
        throw new UnsupportedOperationException("해당 Repository는 Read만 가능합니다.");
    }

    @Override
    default void deleteAllByIdInBatch(Iterable<ID> ids) {
        throw new UnsupportedOperationException("해당 Repository는 Read만 가능합니다.");
    }

    @Override
    default void deleteAllInBatch() {
        throw new UnsupportedOperationException("해당 Repository는 Read만 가능합니다.");
    }

    @Override
    default <S extends T> List<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("해당 Repository는 Read만 가능합니다.");
    }

    @Override
    default void deleteById(ID id) {
        throw new UnsupportedOperationException("해당 Repository는 Read만 가능합니다.");
    }

    @Override
    default void delete(T entity) {
        throw new UnsupportedOperationException("해당 Repository는 Read만 가능합니다.");
    }

    @Override
    default void deleteAllById(Iterable<? extends ID> ids) {
        throw new UnsupportedOperationException("해당 Repository는 Read만 가능합니다.");
    }

    @Override
    default void deleteAll(Iterable<? extends T> entities) {
        throw new UnsupportedOperationException("해당 Repository는 Read만 가능합니다.");
    }

    @Override
    default void deleteAll() {
        throw new UnsupportedOperationException("해당 Repository는 Read만 가능합니다.");
    }
}
