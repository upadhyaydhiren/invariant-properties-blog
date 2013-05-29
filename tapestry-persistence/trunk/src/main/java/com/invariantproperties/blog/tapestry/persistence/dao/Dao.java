package com.invariantproperties.blog.tapestry.persistence.dao;

import java.util.List;

/**
 * Interface that defines all methods that must be implemented by DAOs. This
 * makes the AOP somewhat easier.
 * 
 * @author bgiles@coyotesong.com
 * 
 * @param <T>
 */
public interface Dao<T> {
    /**
     * Get the object by its primary key.
     * 
     * @param id
     * @return
     */
    T getById(Long id);

    /**
     * Create a new object and return it.
     * 
     * @param t
     * @return
     */
    T create(T t);

    /**
     * Update the object and return it. Note: you should not use the passed-in
     * value after this point. (See JPA merge for details.)
     * 
     * @param t
     * @return
     */
    T update(T t);

    /**
     * Delete the specified object.
     * 
     * @param id
     */
    void delete(Long id);

    /**
     * Simple enumeration of all values.
     * 
     * @return
     */
    List<T> findAll();
}
