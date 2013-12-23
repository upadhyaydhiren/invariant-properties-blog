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

import static com.invariantproperties.sandbox.student.matcher.CourseEquality.equalTo;
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

import com.invariantproperties.sandbox.student.business.CourseService;
import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.business.TestRunService;
import com.invariantproperties.sandbox.student.business.config.BusinessApplicationContext;
import com.invariantproperties.sandbox.student.config.TestBusinessApplicationContext;
import com.invariantproperties.sandbox.student.config.TestPersistenceJpaConfig;
import com.invariantproperties.sandbox.student.domain.Course;
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
public class CourseServiceIntegrationTest {

    @Resource
    private CourseService dao;

    @Resource
    private TestRunService testService;

    @Test
    public void testCourseLifecycle() throws Exception {
        final TestRun testRun = testService.createTestRun();

        final String name = "Calculus 101 : " + testRun.getUuid();

        final Course expected = new Course();
        expected.setName(name);

        assertNull(expected.getId());

        // create course
        Course actual = dao.createCourseForTesting(name, testRun);
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());
        expected.setCreationDate(actual.getCreationDate());

        assertThat(expected, equalTo(actual));
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreationDate());

        // get course by id
        actual = dao.findCourseById(expected.getId());
        assertThat(expected, equalTo(actual));

        // get course by uuid
        actual = dao.findCourseByUuid(expected.getUuid());
        assertThat(expected, equalTo(actual));

        // get all courses
        final List<Course> courses = dao.findCoursesByTestRun(testRun);
        assertTrue(courses.contains(actual));

        // update course
        expected.setName("Calculus 102 : " + testRun.getUuid());
        actual = dao.updateCourse(actual, expected.getName());
        assertThat(expected, equalTo(actual));

        // verify testRun.getObjects
        final List<TestablePersistentObject> objects = testRun.getObjects();
        assertTrue(objects.contains(actual));

        // delete Course
        dao.deleteCourse(expected.getUuid());
        try {
            dao.findCourseByUuid(expected.getUuid());
            fail("exception expected");
        } catch (ObjectNotFoundException e) {
            // expected
        }

        testService.deleteTestRun(testRun.getUuid());
    }

    /**
     * @test findCourseById() with unknown course.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindCourseByIdWhenCourseIsNotKnown() {
        final Integer id = 1;
        dao.findCourseById(id);
    }

    /**
     * @test findCourseByUuid() with unknown Course.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindCourseByUuidWhenCourseIsNotKnown() {
        final String uuid = "missing";
        dao.findCourseByUuid(uuid);
    }

    /**
     * Test updateCourse() with unknown course.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateCourseWhenCourseIsNotFound() {
        final Course course = new Course();
        course.setUuid("missing");
        dao.updateCourse(course, "Calculus 102");
    }

    /**
     * Test deleteCourse() with unknown course.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteCourseWhenCourseIsNotFound() {
        dao.deleteCourse("missing");
    }
}