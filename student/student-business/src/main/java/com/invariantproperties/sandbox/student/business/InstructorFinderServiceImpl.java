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

import static com.invariantproperties.sandbox.student.specification.InstructorSpecifications.testRunIs;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.student.domain.Instructor;
import com.invariantproperties.sandbox.student.domain.TestRun;
import com.invariantproperties.sandbox.student.repository.InstructorRepository;

/**
 * Implementation of InstructorService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class InstructorFinderServiceImpl implements InstructorFinderService {
    private static final Logger log = LoggerFactory.getLogger(InstructorFinderServiceImpl.class);

    @Resource
    private InstructorRepository instructorRepository;

    /**
     * Default constructor
     */
    public InstructorFinderServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    InstructorFinderServiceImpl(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
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
            count = instructorRepository.count(testRunIs(testRun));
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving classroom count by " + testRun, e);
            }
            throw new PersistenceException("unable to count classrooms by " + testRun, e, 0);
        }

        return count;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.InstructorManagerService#
     *      findAllInstructors()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Instructor> findAllInstructors() {
        return findInstructorsByTestRun(null);
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.InstructorManagerService#
     *      findInstructorById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public Instructor findInstructorById(Integer id) {
        Instructor instructor = null;
        try {
            instructor = instructorRepository.findOne(id);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving instructor: " + id, e);
            }
            throw new PersistenceException("unable to find instructor by id", e, id);
        }

        if (instructor == null) {
            throw new ObjectNotFoundException(id);
        }

        return instructor;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.InstructorManagerService#
     *      findInstructorByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Instructor findInstructorByUuid(String uuid) {
        Instructor instructor = null;
        try {
            instructor = instructorRepository.findInstructorByUuid(uuid);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving instructor: " + uuid, e);
            }
            throw new PersistenceException("unable to find instructor by uuid", e, uuid);
        }

        if (instructor == null) {
            throw new ObjectNotFoundException(uuid);
        }

        return instructor;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.InstructorManagerService#
     *      findInstructorsByTestRun(com.invariantproperties.sandbox.student.common.TestRun)
     */
    @Transactional(readOnly = true)
    @Override
    public List<Instructor> findInstructorsByTestRun(TestRun testRun) {
        List<Instructor> instructors = null;

        try {
            instructors = instructorRepository.findAll(testRunIs(testRun));
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("error loading list of instructors: " + e.getMessage(), e);
            }
            throw new PersistenceException("unable to get list of instructors.", e);
        }

        return instructors;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.InstructorManagerService#
     *      findInstructorByEmailAddress(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Instructor findInstructorByEmailAddress(String emailAddress) {
        Instructor instructor = null;
        try {
            instructor = instructorRepository.findInstructorByEmailAddress(emailAddress);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving instructor: " + emailAddress, e);
            }
            throw new PersistenceException("unable to find instructor by email address", e, emailAddress);
        }

        if (instructor == null) {
            throw new ObjectNotFoundException("(" + emailAddress + ")");
        }

        return instructor;
    }
}
