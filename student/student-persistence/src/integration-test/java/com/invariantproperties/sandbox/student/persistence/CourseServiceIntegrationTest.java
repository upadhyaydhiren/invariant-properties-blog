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

import static com.invariantproperties.sandbox.student.matcher.CourseEquality.equalTo;
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
import com.invariantproperties.sandbox.student.domain.Course;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestPersistenceJpaConfig.class, TestPersistenceApplicationContext.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class CourseServiceIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(CourseServiceIntegrationTest.class.getName());

    @Resource
    private CourseService dao;

    @Test
    public void testCourseLifecycle() throws Exception {
        final String name = "Calculus 101";

        final Course expected = new Course();
        expected.setName(name);

        assertNull(expected.getId());

        // create Course
        Course actual = dao.createCourse(name);
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());
        expected.setCreationDate(actual.getCreationDate());

        assertThat(expected, equalTo(actual));
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreationDate());

        // get Course by id
        actual = dao.findCourseById(expected.getId());
        assertThat(expected, equalTo(actual));

        // get Course by uuid
        actual = dao.findCourseByUuid(expected.getUuid());
        assertThat(expected, equalTo(actual));

        // update Course
        // expected.setName("Bob");
        // expected.setEmailAddress("bob@example.com");
        // actual = dao.updateCourse(actual, expected.getName(),
        // expected.getEmailAddress());
        // assertThat(expected, equalTo(actual));

        // delete Course
        dao.deleteCourse(expected.getUuid());
        log.info("findCourseByUuid(" + expected.getUuid() + ") is expected to fail.");
        actual = dao.findCourseByUuid(expected.getUuid());
        assertNull(actual);
    }

    /**
     * @test findCourseById() with unknown Course.
     */
    @Test
    public void testfindCourseByIdWhenCourseIsNotKnown() {
        final Integer id = 1;
        final Course Course = dao.findCourseById(id);
        assertNull(Course);
    }

    /**
     * @test findCourseByUuid() with unknown Course.
     */
    @Test
    public void testfindCourseByUuidWhenCourseIsNotKnown() {
        final String uuid = "missing";
        final Course Course = dao.findCourseByUuid(uuid);
        assertNull(Course);
    }

    /**
     * Test updateCourse() with unknown Course.
     * 
     * @throws ObjectNotFoundException
     */
    // @Test(expected = ObjectNotFoundException.class)
    // public void testUpdateCourseWhenCourseIsNotFound() {
    // final Course Course = new Course();
    // Course.setUuid("missing");

    // dao.updateCourse(Course, "Bob", "bob@example.com");
    // }

    /**
     * Test deleteCourse() with unknown Course.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteCourseWhenCourseIsNotFound() {
        dao.deleteCourse("missing");
    }
}