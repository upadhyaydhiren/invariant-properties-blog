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
package com.invariantproperties.sandbox.student.persistence;

import static com.invariantproperties.sandbox.student.matcher.StudentEquality.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.student.config.TestPersistenceApplicationContext;
import com.invariantproperties.sandbox.student.config.TestPersistenceJpaConfig;
import com.invariantproperties.sandbox.student.domain.Student;

/**
 * @author bgiles
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestPersistenceJpaConfig.class, TestPersistenceApplicationContext.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class StudentServiceIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(StudentServiceIntegrationTest.class.getName());

    @Resource
    private StudentService dao;

    @Test
    public void testStudentLifecycle() throws Exception {
        final String name = "Alice";
        final String emailAddress = "alice@example.com";

        final Student expected = new Student();
        expected.setName(name);
        expected.setEmailAddress(emailAddress);

        assertNull(expected.getId());

        // create Student
        Student actual = dao.createStudent(name, emailAddress);
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());
        expected.setCreationDate(actual.getCreationDate());

        assertThat(expected, equalTo(actual));
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreationDate());

        // get Student by id
        actual = dao.findStudentById(expected.getId());
        assertThat(expected, equalTo(actual));

        // get Student by uuid
        actual = dao.findStudentByUuid(expected.getUuid());
        assertThat(expected, equalTo(actual));

        // update Student
        // expected.setName("Bob");
        // expected.setEmailAddress("bob@example.com");
        // actual = dao.updateStudent(actual, expected.getName(),
        // expected.getEmailAddress());
        // assertThat(expected, equalTo(actual));

        // delete Student
        dao.deleteStudent(expected.getUuid());
        log.info("findStudentByUuid(" + expected.getUuid() + ") is expected to fail.");
        actual = dao.findStudentByUuid(expected.getUuid());
        assertNull(actual);
    }

    /**
     * @test findStudentById() with unknown Student.
     */
    @Test
    public void testfindStudentByIdWhenStudentIsNotKnown() {
        final Integer id = 1;
        final Student Student = dao.findStudentById(id);
        assertNull(Student);
    }

    /**
     * @test findStudentByUuid() with unknown Student.
     */
    @Test
    public void testfindStudentByUuidWhenStudentIsNotKnown() {
        final String uuid = "missing";
        final Student Student = dao.findStudentByUuid(uuid);
        assertNull(Student);
    }

    /**
     * Test updateStudent() with unknown Student.
     * 
     * @throws ObjectNotFoundException
     */
    // @Test(expected = ObjectNotFoundException.class)
    // public void testUpdateStudentWhenStudentIsNotFound() {
    // final Student Student = new Student();
    // Student.setUuid("missing");

    // dao.updateStudent(Student, "Bob", "bob@example.com");
    // }

    /**
     * Test deleteStudent() with unknown Student.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteStudentWhenStudentIsNotFound() {
        dao.deleteStudent("missing");
    }
}