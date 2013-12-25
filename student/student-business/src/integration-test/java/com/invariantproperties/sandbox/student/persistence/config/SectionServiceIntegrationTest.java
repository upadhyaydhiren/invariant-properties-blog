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

import static com.invariantproperties.sandbox.student.matcher.SectionEquality.equalTo;
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
import com.invariantproperties.sandbox.student.business.SectionService;
import com.invariantproperties.sandbox.student.business.TestRunService;
import com.invariantproperties.sandbox.student.config.BusinessApplicationContext;
import com.invariantproperties.sandbox.student.config.TestBusinessApplicationContext;
import com.invariantproperties.sandbox.student.config.TestPersistenceJpaConfig;
import com.invariantproperties.sandbox.student.domain.Section;
import com.invariantproperties.sandbox.student.domain.TestRun;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BusinessApplicationContext.class, TestBusinessApplicationContext.class,
        TestPersistenceJpaConfig.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class SectionServiceIntegrationTest {

    @Resource
    private SectionService dao;

    @Resource
    TestRunService testService;

    @Test
    public void testSectionLifecycle() throws Exception {
        final TestRun testRun = testService.createTestRun();

        final String name = "Calculus 101 - Fall 2013 : " + testRun.getUuid();

        final Section expected = new Section();
        expected.setName(name);

        assertNull(expected.getId());

        // create section
        Section actual = dao.createSection(name);
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());
        expected.setCreationDate(actual.getCreationDate());

        assertThat(expected, equalTo(actual));
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreationDate());

        // get section by id
        actual = dao.findSectionById(expected.getId());
        assertThat(expected, equalTo(actual));

        // get section by uuid
        actual = dao.findSectionByUuid(expected.getUuid());
        assertThat(expected, equalTo(actual));

        // get all sections
        final List<Section> sections = dao.findSectionsByTestRun(testRun);
        assertTrue(sections.contains(actual));

        // update section
        expected.setName("Calculus 101 - Fall 2014 : " + testRun.getUuid());
        actual = dao.updateSection(actual, expected.getName());
        assertThat(expected, equalTo(actual));

        // verify testRun.getObjects
        // final List<TestablePersistentObject> objects = testRun.getObjects();
        // assertTrue(objects.contains(actual));

        // delete Section
        dao.deleteSection(expected.getUuid());
        try {
            dao.findSectionByUuid(expected.getUuid());
            fail("exception expected");
        } catch (ObjectNotFoundException e) {
            // expected
        }

        testService.deleteTestRun(testRun.getUuid());
    }

    /**
     * @test findSectionById() with unknown section.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindSectionByIdWhenSectionIsNotKnown() {
        final Integer id = 1;
        dao.findSectionById(id);
    }

    /**
     * @test findSectionByUuid() with unknown Section.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindSectionByUuidWhenSectionIsNotKnown() {
        final String uuid = "missing";
        dao.findSectionByUuid(uuid);
    }

    /**
     * Test updateSection() with unknown section.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateSectionWhenSectionIsNotFound() {
        final Section section = new Section();
        section.setUuid("missing");
        dao.updateSection(section, "Calculus 101 - Fall 2014");
    }

    /**
     * Test deleteSection() with unknown section.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteSectionWhenSectionIsNotFound() {
        dao.deleteSection("missing");
    }
}