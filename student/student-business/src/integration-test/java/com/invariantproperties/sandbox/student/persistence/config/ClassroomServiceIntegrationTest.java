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

import static com.invariantproperties.sandbox.student.matcher.ClassroomEquality.equalTo;
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

import com.invariantproperties.sandbox.student.business.ClassroomService;
import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.business.TestRunService;
import com.invariantproperties.sandbox.student.business.config.BusinessApplicationContext;
import com.invariantproperties.sandbox.student.config.TestBusinessApplicationContext;
import com.invariantproperties.sandbox.student.config.TestPersistenceJpaConfig;
import com.invariantproperties.sandbox.student.domain.Classroom;
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
public class ClassroomServiceIntegrationTest {

    @Resource
    private ClassroomService dao;

    @Resource
    private TestRunService testService;

    @Test
    public void testClassroomLifecycle() throws Exception {
        final TestRun testRun = testService.createTestRun();

        final String name = "Eng 101 : " + testRun.getUuid();

        final Classroom expected = new Classroom();
        expected.setName(name);

        assertNull(expected.getId());

        // create classroom
        Classroom actual = dao.createClassroom(name);
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());
        expected.setCreationDate(actual.getCreationDate());

        assertThat(expected, equalTo(actual));
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreationDate());

        // get classroom by id
        actual = dao.findClassroomById(expected.getId());
        assertThat(expected, equalTo(actual));

        // get classroom by uuid
        actual = dao.findClassroomByUuid(expected.getUuid());
        assertThat(expected, equalTo(actual));

        // get all classrooms
        final List<Classroom> classrooms = dao.findClassroomsByTestRun(testRun);
        assertTrue(classrooms.contains(actual));

        // update classroom
        expected.setName("Eng 102 : " + testRun.getUuid());
        actual = dao.updateClassroom(actual, expected.getName());
        assertThat(expected, equalTo(actual));

        // verify testRun.getObjects
        final List<TestablePersistentObject> objects = testRun.getObjects();
        assertTrue(objects.contains(actual));

        // delete Classroom
        dao.deleteClassroom(expected.getUuid());
        try {
            dao.findClassroomByUuid(expected.getUuid());
            fail("exception expected");
        } catch (ObjectNotFoundException e) {
            // expected
        }

        testService.deleteTestRun(testRun.getUuid());
    }

    /**
     * @test findClassroomById() with unknown classroom.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindClassroomByIdWhenClassroomIsNotKnown() {
        final Integer id = 1;
        dao.findClassroomById(id);
    }

    /**
     * @test findClassroomByUuid() with unknown Classroom.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindClassroomByUuidWhenClassroomIsNotKnown() {
        final String uuid = "missing";
        dao.findClassroomByUuid(uuid);
    }

    /**
     * Test updateClassroom() with unknown classroom.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateClassroomWhenClassroomIsNotFound() {
        final Classroom classroom = new Classroom();
        classroom.setUuid("missing");
        dao.updateClassroom(classroom, "Eng 102");
    }

    /**
     * Test deleteClassroom() with unknown classroom.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteClassroomWhenClassroomIsNotFound() {
        dao.deleteClassroom("missing");
    }
}