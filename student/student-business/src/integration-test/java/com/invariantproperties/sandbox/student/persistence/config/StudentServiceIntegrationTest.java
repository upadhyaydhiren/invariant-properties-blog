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
package com.invariantproperties.sandbox.student.persistence.config;

import static com.invariantproperties.sandbox.student.matcher.StudentEquality.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.business.StudentService;
import com.invariantproperties.sandbox.student.business.TestRunService;
import com.invariantproperties.sandbox.student.business.config.BusinessApplicationContext;
import com.invariantproperties.sandbox.student.config.TestBusinessApplicationContext;
import com.invariantproperties.sandbox.student.config.TestPersistenceJpaConfig;
import com.invariantproperties.sandbox.student.domain.Student;
import com.invariantproperties.sandbox.student.domain.TestRun;
import com.invariantproperties.sandbox.student.domain.TestablePersistentObject;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BusinessApplicationContext.class, TestBusinessApplicationContext.class,
        TestPersistenceJpaConfig.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class StudentServiceIntegrationTest {

    @Resource
    private StudentService dao;

    @Resource
    TestRunService testService;

    @Test
    public void testStudentLifecycle() throws Exception {
        final TestRun testRun = testService.createTestRun();

        final String name = "Alice : " + testRun.getUuid();
        final String email = "alice-" + testRun.getUuid() + "@example.com";

        final Student expected = new Student();
        expected.setName(name);
        expected.setEmailAddress(email);

        assertNull(expected.getId());

        // create student
        Student actual = dao.createStudent(name, email);
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());
        expected.setCreationDate(actual.getCreationDate());

        assertThat(expected, equalTo(actual));
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreationDate());

        // get student by id
        actual = dao.findStudentById(expected.getId());
        assertThat(expected, equalTo(actual));

        // get student by uuid
        actual = dao.findStudentByUuid(expected.getUuid());
        assertThat(expected, equalTo(actual));

        // get all students
        final List<Student> students = dao.findStudentsByTestRun(testRun);
        assertTrue(students.contains(actual));

        // update student
        expected.setName("Bob : " + testRun.getUuid());
        expected.setEmailAddress("bob-" + testRun.getUuid() + "@example.com");
        actual = dao.updateStudent(actual, expected.getName(), expected.getEmailAddress());
        assertThat(expected, equalTo(actual));

        // verify testRun.getObjects
        final List<TestablePersistentObject> objects = testRun.getObjects();
        assertTrue(objects.contains(actual));

        // delete Student
        dao.deleteStudent(expected.getUuid());
        try {
            dao.findStudentByUuid(expected.getUuid());
            fail("exception expected");
        } catch (ObjectNotFoundException e) {
            // expected
        }

        testService.deleteTestRun(testRun.getUuid());
    }

    /**
     * @test findStudentById() with unknown student.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindStudentByIdWhenStudentIsNotKnown() {
        final Integer id = 1;
        dao.findStudentById(id);
    }

    /**
     * @test findStudentByUuid() with unknown Student.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindStudentByUuidWhenStudentIsNotKnown() {
        final String uuid = "missing";
        dao.findStudentByUuid(uuid);
    }

    /**
     * Test updateStudent() with unknown student.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateStudentWhenStudentIsNotFound() {
        final Student student = new Student();
        student.setUuid("missing");
        dao.updateStudent(student, "Alice", "alice@example.com");
    }

    /**
     * Test deleteStudent() with unknown student.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteStudentWhenStudentIsNotFound() {
        dao.deleteStudent("missing");
    }
}