package com.invariantproperties.blog.tapestry.persistence.dao;

import java.util.List;

import org.apache.tapestry5.jpa.annotations.CommitAfter;

import com.invariantproperties.blog.tapestry.persistence.domain.Server;

/**
 * @author bgiles@coyotesong.com
 */
public interface ServerDao extends Dao<Server> {
    @Override
    Server getById(final Long id);

    @Override
    @CommitAfter
    Server create(final Server server);

    @Override
    @CommitAfter
    Server update(final Server server);

    @Override
    @CommitAfter
    void delete(final Long id);

    @Override
    List<Server> findAll();
}
