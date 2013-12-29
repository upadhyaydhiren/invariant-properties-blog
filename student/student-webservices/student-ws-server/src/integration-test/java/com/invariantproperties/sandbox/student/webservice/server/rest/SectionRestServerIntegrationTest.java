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
package com.invariantproperties.sandbox.student.webservice.server.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.invariantproperties.sandbox.student.domain.Section;
import com.invariantproperties.sandbox.student.domain.TestRun;
import com.invariantproperties.sandbox.student.webservice.client.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.webservice.client.SectionFinderRestClient;
import com.invariantproperties.sandbox.student.webservice.client.SectionManagerRestClient;
import com.invariantproperties.sandbox.student.webservice.client.TestRunManagerRestClient;
import com.invariantproperties.sandbox.student.webservice.client.impl.SectionFinderRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.client.impl.SectionManagerRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.client.impl.TestRunManagerRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.config.TestRestApplicationContext;

/**
 * Integration tests for SectionResource
 * 
 * @author bgiles
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestRestApplicationContext.class })
public class SectionRestServerIntegrationTest {
    @Resource
    private String resourceBase;
    private SectionFinderRestClient finderClient;
    private SectionManagerRestClient managerClient;
    private TestRunManagerRestClient testClient;

    @Before
    public void init() {
        this.finderClient = new SectionFinderRestClientImpl(resourceBase + "section/");
        this.managerClient = new SectionManagerRestClientImpl(resourceBase + "section/");
        this.testClient = new TestRunManagerRestClientImpl(resourceBase + "testRun/");
    }

    @Test
    public void testGetAll() throws IOException {
        final Section[] sections = finderClient.getAllSections();
        assertNotNull(sections);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUnknownSection() throws IOException {
        finderClient.getSection("missing");
    }

    @Test
    public void testLifecycle() throws IOException {
        final TestRun testRun = testClient.createTestRun();

        final String physicsFall2013Name = "Physics 201 - Fall 2013 : " + testRun.getUuid();
        final Section expected = managerClient.createSection(physicsFall2013Name);
        assertEquals(physicsFall2013Name, expected.getName());

        final Section actual1 = finderClient.getSection(expected.getUuid());
        assertEquals(physicsFall2013Name, actual1.getName());

        final Section[] sections = finderClient.getAllSections();
        assertTrue(sections.length > 0);

        final String physicsFall2014Name = "Physics 201 - Fall 2014 : " + testRun.getUuid();
        final Section actual2 = managerClient.updateSection(actual1.getUuid(), physicsFall2014Name);
        assertEquals(physicsFall2014Name, actual2.getName());

        managerClient.deleteSection(actual1.getUuid());
        try {
            finderClient.getSection(expected.getUuid());
            fail("should have thrown exception");
        } catch (ObjectNotFoundException e) {
            // do nothing
        }

        testClient.deleteTestRun(testRun.getUuid());
    }
}
