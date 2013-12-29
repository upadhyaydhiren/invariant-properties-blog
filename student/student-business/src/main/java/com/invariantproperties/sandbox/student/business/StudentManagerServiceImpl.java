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

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.student.domain.Student;
import com.invariantproperties.sandbox.student.domain.TestRun;
import com.invariantproperties.sandbox.student.repository.StudentRepository;

/**
 * Implementation of StudentService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class StudentManagerServiceImpl implements StudentManagerService {
    private static final Logger log = LoggerFactory.getLogger(StudentManagerServiceImpl.class);

    @Resource
    private StudentRepository studentRepository;

    /**
     * Default constructor
     */
    public StudentManagerServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    StudentManagerServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.StudentFinderService#
     *      createStudent(java.lang.String, java.lang.String)
     */
    @Transactional
    @Override
    public Student createStudent(String name, String emailAddress) {
        final Student student = new Student();
        student.setName(name);
        student.setEmailAddress(emailAddress);

        Student actual = null;
        try {
            actual = studentRepository.saveAndFlush(student);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving student: " + name, e);
            }
            throw new PersistenceException("unable to create student", e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.StudentFinderService#
     *      createStudentForTesting(java.lang.String, java.lang.String,
     *      com.invariantproperties.sandbox.student.common.TestRun)
     */
    @Transactional
    @Override
    public Student createStudentForTesting(String name, String emailAddress, TestRun testRun) {
        final Student student = new Student();
        student.setName(name);
        student.setEmailAddress(emailAddress);
        student.setTestRun(testRun);

        Student actual = null;
        try {
            actual = studentRepository.saveAndFlush(student);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving student: " + name, e);
            }
            throw new PersistenceException("unable to create student", e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.sandbox.student.persistence.StudentFinderService#
     *      updateStudent(com.invariantproperties.sandbox.student.domain.Student,
     *      java.lang.String, java.lang.String)
     */
    @Transactional
    @Override
    public Student updateStudent(Student student, String name, String emailAddress) {
        Student updated = null;
        try {
            final Student actual = studentRepository.findStudentByUuid(student.getUuid());

            if (actual == null) {
                log.debug("did not find student: " + student.getUuid());
                throw new ObjectNotFoundException(student.getUuid());
            }

            actual.setName(name);
            actual.setEmailAddress(emailAddress);
            updated = studentRepository.saveAndFlush(actual);
            student.setName(name);
            student.setEmailAddress(emailAddress);

        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error deleting student: " + student.getUuid(), e);
            }
            throw new PersistenceException("unable to delete student", e, student.getUuid());
        }

        return updated;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.StudentFinderService#
     *      deleteStudent(java.lang.String, java.lang.Integer)
     */
    @Transactional
    @Override
    public void deleteStudent(String uuid, Integer version) {
        Student student = null;
        try {
            student = studentRepository.findStudentByUuid(uuid);

            if (student == null) {
                log.debug("did not find student: " + uuid);
                throw new ObjectNotFoundException(uuid);
            }
            studentRepository.delete(student);

        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error deleting student: " + uuid, e);
            }
            throw new PersistenceException("unable to delete student", e, uuid);
        }
    }
}
