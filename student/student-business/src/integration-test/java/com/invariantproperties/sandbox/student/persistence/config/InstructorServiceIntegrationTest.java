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

import static com.invariantproperties.sandbox.student.matcher.InstructorEquality.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.student.business.InstructorService;
import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.business.config.BusinessApplicationContext;
import com.invariantproperties.sandbox.student.config.TestBusinessApplicationContext;
import com.invariantproperties.sandbox.student.config.TestPersistenceJpaConfig;
import com.invariantproperties.sandbox.student.domain.Instructor;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BusinessApplicationContext.class, TestBusinessApplicationContext.class,
        TestPersistenceJpaConfig.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class InstructorServiceIntegrationTest {

    @Resource
    private InstructorService dao;

    @Test
    public void testInstructorLifecycle() throws Exception {
        final String name = "Alice";
        final String email = "alice@example.com";

        final Instructor expected = new Instructor();
        expected.setName(name);
        expected.setEmailAddress(email);

        assertNull(expected.getId());

        // create instructor
        Instructor actual = dao.createInstructor(name, email);
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());
        expected.setCreationDate(actual.getCreationDate());

        assertThat(expected, equalTo(actual));
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreationDate());

        // get instructor by id
        actual = dao.findInstructorById(expected.getId());
        assertThat(expected, equalTo(actual));

        // get instructor by uuid
        actual = dao.findInstructorByUuid(expected.getUuid());
        assertThat(expected, equalTo(actual));

        // update instructor
        expected.setName("Bob");
        expected.setEmailAddress("bob@example.com");
        actual = dao.updateInstructor(actual, expected.getName(), expected.getEmailAddress());
        assertThat(expected, equalTo(actual));

        // delete Instructor
        dao.deleteInstructor(expected.getUuid());
        try {
            dao.findInstructorByUuid(expected.getUuid());
            fail("exception expected");
        } catch (ObjectNotFoundException e) {
            // expected
        }
    }

    /**
     * @test findInstructorById() with unknown instructor.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindInstructorByIdWhenInstructorIsNotKnown() {
        final Integer id = 1;
        dao.findInstructorById(id);
    }

    /**
     * @test findInstructorByUuid() with unknown Instructor.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindInstructorByUuidWhenInstructorIsNotKnown() {
        final String uuid = "missing";
        dao.findInstructorByUuid(uuid);
    }

    /**
     * Test updateInstructor() with unknown instructor.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateInstructorWhenInstructorIsNotFound() {
        final Instructor instructor = new Instructor();
        instructor.setUuid("missing");
        dao.updateInstructor(instructor, "Alice", "alice@example.com");
    }

    /**
     * Test deleteInstructor() with unknown instructor.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteInstructorWhenInstructorIsNotFound() {
        dao.deleteInstructor("missing");
    }
}