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

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.student.domain.Classroom;
import com.invariantproperties.sandbox.student.repository.ClassroomRepository;

/**
 * Implementation of ClassroomService
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class ClassroomServiceImpl implements ClassroomService {
    private static final Logger log = LoggerFactory.getLogger(ClassroomServiceImpl.class);

    @Resource
    private ClassroomRepository classroomRepository;

    /**
     * Default constructor
     */
    public ClassroomServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    ClassroomServiceImpl(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.ClassroomService#
     *      findAllClassrooms()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Classroom> findAllClassrooms() {
        List<Classroom> classrooms = null;

        try {
            classrooms = classroomRepository.findAll();
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("error loading list of classrooms: " + e.getMessage(), e);
            }
            throw new PersistenceException("unable to get list of classrooms.", e);
        }

        return classrooms;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.ClassroomService#
     *      findClassroomById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public Classroom findClassroomById(Integer id) {
        Classroom classroom = null;
        try {
            classroom = classroomRepository.findOne(id);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving classroom: " + id, e);
            }
            throw new PersistenceException("unable to find classroom by id", e, id);
        }

        if (classroom == null) {
            throw new ObjectNotFoundException(id);
        }

        return classroom;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.ClassroomService#
     *      findClassroomByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Classroom findClassroomByUuid(String uuid) {
        Classroom classroom = null;
        try {
            classroom = classroomRepository.findClassroomByUuid(uuid);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving classroom: " + uuid, e);
            }
            throw new PersistenceException("unable to find classroom by uuid", e, uuid);
        }

        if (classroom == null) {
            throw new ObjectNotFoundException(uuid);
        }

        return classroom;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.ClassroomService#
     *      createClassroom(java.lang.String)
     */
    @Transactional
    @Override
    public Classroom createClassroom(String name) {
        final Classroom classroom = new Classroom();
        classroom.setName(name);

        Classroom actual = null;
        try {
            actual = classroomRepository.saveAndFlush(classroom);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving classroom: " + name, e);
            }
            throw new PersistenceException("unable to create classroom", e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.sandbox.classroom.persistence.ClassroomService#
     *      updateClassroom(com.invariantproperties.sandbox.classroom.domain.Classroom,
     *      java.lang.String)
     */
    @Transactional
    @Override
    public Classroom updateClassroom(Classroom classroom, String name) {
        Classroom updated = null;
        try {
            final Classroom actual = classroomRepository.findClassroomByUuid(classroom.getUuid());

            if (actual == null) {
                log.debug("did not find classroom: " + classroom.getUuid());
                throw new ObjectNotFoundException(classroom.getUuid());
            }

            actual.setName(name);
            updated = classroomRepository.saveAndFlush(actual);
            classroom.setName(name);

        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error deleting classroom: " + classroom.getUuid(), e);
            }
            throw new PersistenceException("unable to delete classroom", e, classroom.getUuid());
        }

        return updated;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.ClassroomService#
     *      deleteClassroom(java.lang.String)
     */
    @Transactional
    @Override
    public void deleteClassroom(String uuid) {
        Classroom classroom = null;
        try {
            classroom = classroomRepository.findClassroomByUuid(uuid);

            if (classroom == null) {
                log.debug("did not find classroom: " + uuid);
                throw new ObjectNotFoundException(uuid);
            }
            classroomRepository.delete(classroom);

        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error deleting classroom: " + uuid, e);
            }
            throw new PersistenceException("unable to delete classroom", e, uuid);
        }
    }
}
