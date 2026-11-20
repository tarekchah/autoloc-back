package tn.esprit.autoloc.service;

public interface IWriteService<T, K> {
    boolean check(T entity, boolean isNew);
    T create(T entity);
    T update(K id, T entity);
}
