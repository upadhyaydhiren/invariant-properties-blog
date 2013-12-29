/*
 * This code was written by Bear Giles <bgiles@coyotesong.com> and he
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Any contributions made by others are licensed to this project under
 * one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * Copyright (c) 2013 Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.sandbox.student.business;

import static com.invariantproperties.sandbox.student.specification.TermSpecifications.testRunIs;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.student.domain.Term;
import com.invariantproperties.sandbox.student.domain.TestRun;
import com.invariantproperties.sandbox.student.repository.TermRepository;

/**
 * Implementation of TermService
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class TermFinderServiceImpl implements TermFinderService {
    private static final Logger log = LoggerFactory.getLogger(TermFinderServiceImpl.class);

    @Resource
    private TermRepository termRepository;

    /**
     * Default constructor
     */
    public TermFinderServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    TermFinderServiceImpl(TermRepository termRepository) {
        this.termRepository = termRepository;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.FinderService#
     *      count()
     */
    @Transactional(readOnly = true)
    @Override
    public long count() {
        return countByTestRun(null);
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.FinderService#
     *      countByTestRun(com.invariantproperties.sandbox.student.domain.TestRun)
     */
    @Transactional(readOnly = true)
    @Override
    public long countByTestRun(TestRun testRun) {
        long count = 0;
        try {
            count = termRepository.count(testRunIs(testRun));
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving classroom count by " + testRun, e);
            }
            throw new PersistenceException("unable to count classrooms by " + testRun, e, 0);
        }

        return count;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.TermFinderService#
     *      findAllTerms()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Term> findAllTerms() {
        return findTermsByTestRun(null);
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.TermFinderService#
     *      findTermById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public Term findTermById(Integer id) {
        Term term = null;
        try {
            term = termRepository.findOne(id);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving term: " + id, e);
            }
            throw new PersistenceException("unable to find term by id", e, id);
        }

        if (term == null) {
            throw new ObjectNotFoundException(id);
        }

        return term;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.TermFinderService#
     *      findTermByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Term findTermByUuid(String uuid) {
        Term term = null;
        try {
            term = termRepository.findTermByUuid(uuid);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving term: " + uuid, e);
            }
            throw new PersistenceException("unable to find term by uuid", e, uuid);
        }

        if (term == null) {
            throw new ObjectNotFoundException(uuid);
        }

        return term;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.TermFinderService#
     *      findTermsByTestRun(com.invariantproperties.sandbox.student.common.TestRun)
     */
    @Transactional(readOnly = true)
    @Override
    public List<Term> findTermsByTestRun(TestRun testRun) {
        List<Term> terms = null;

        try {
            terms = termRepository.findAll(testRunIs(testRun));
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("error loading list of terms: " + e.getMessage(), e);
            }
            throw new PersistenceException("unable to get list of terms.", e);
        }

        return terms;
    }
}
