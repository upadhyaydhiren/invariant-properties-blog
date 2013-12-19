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

import com.invariantproperties.sandbox.student.domain.Student;
import com.invariantproperties.sandbox.student.repository.StudentRepository;

/**
 * Implementation of StudentService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class StudentServiceImpl implements StudentService {
    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Resource
    private StudentRepository studentRepository;

    /**
     * Default constructor
     */
    public StudentServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.StudentService#
     *      findAllStudents()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Student> findAllStudents() {
        List<Student> students = null;

        try {
            students = studentRepository.findAll();
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("error loading list of students: " + e.getMessage(), e);
            }
            throw new PersistenceException("unable to get list of students.", e);
        }

        return students;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.StudentService#
     *      findStudentById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public Student findStudentById(Integer id) {
        Student student = null;
        try {
            student = studentRepository.findOne(id);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving student: " + id, e);
            }
            throw new PersistenceException("unable to find student by id", e, id);
        }

        if (student == null) {
            throw new ObjectNotFoundException(id);
        }

        return student;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.StudentService#
     *      findStudentByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Student findStudentByUuid(String uuid) {
        Student student = null;
        try {
            student = studentRepository.findStudentByUuid(uuid);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving student: " + uuid, e);
            }
            throw new PersistenceException("unable to find student by uuid", e, uuid);
        }

        if (student == null) {
            throw new ObjectNotFoundException(uuid);
        }

        return student;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.StudentService#
     *      findStudentByEmailAddress(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Student findStudentByEmailAddress(String emailAddress) {
        Student student = null;
        try {
            student = studentRepository.findStudentByEmailAddress(emailAddress);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving student: " + emailAddress, e);
            }
            throw new PersistenceException("unable to find student by email address", e, emailAddress);
        }

        if (student == null) {
            throw new ObjectNotFoundException("(" + emailAddress + ")");
        }

        return student;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.StudentService#
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
     * @see com.invariantproperties.sandbox.student.persistence.StudentService#
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
     * @see com.invariantproperties.sandbox.student.business.StudentService#
     *      deleteStudent(java.lang.String)
     */
    @Transactional
    @Override
    public void deleteStudent(String uuid) {
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
