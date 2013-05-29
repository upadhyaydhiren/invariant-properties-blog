/**
 * 
 */
package com.invariantproperties.blog.tapestry.persistence.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;

import com.invariantproperties.blog.tapestry.persistence.dao.ServerDao;
import com.invariantproperties.blog.tapestry.persistence.domain.Server;

/**
 * Implementation of ServerDao using JPA.
 * 
 * @author bgiles@coyotesong.com
 */
public class ServerDaoJpa implements ServerDao {
    private final EntityManager em;

    public ServerDaoJpa(EntityManager em) {
        this.em = em;
    }

    @Override
    public Server getById(final Long id) {
        return em.find(Server.class, id);
    }

    @Override
    public Server create(final Server server) {
        em.persist(server);
        return server;
    }

    // TODO verify this does what I expect.
    @Override
    public Server update(final Server server) {
        return em.merge(server);
    }

    @Override
    public void delete(final Long id) {
        final Server server = em.find(Server.class, id);
        em.remove(server);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Server> findAll() {
        return em.createQuery("from Server s").getResultList();
    }
}
