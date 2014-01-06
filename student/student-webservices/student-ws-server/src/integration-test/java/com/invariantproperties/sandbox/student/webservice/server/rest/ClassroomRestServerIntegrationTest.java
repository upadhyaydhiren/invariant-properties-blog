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

import com.invariantproperties.sandbox.student.domain.Classroom;
import com.invariantproperties.sandbox.student.domain.TestRun;
import com.invariantproperties.sandbox.student.webservice.client.ClassroomFinderRestClient;
import com.invariantproperties.sandbox.student.webservice.client.ClassroomManagerRestClient;
import com.invariantproperties.sandbox.student.webservice.client.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.webservice.client.RestClientException;
import com.invariantproperties.sandbox.student.webservice.client.TestRunManagerRestClient;
import com.invariantproperties.sandbox.student.webservice.client.impl.ClassroomFinderRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.client.impl.ClassroomManagerRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.client.impl.TestRunManagerRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.config.TestRestApplicationContext;

/**
 * Integration tests for ClassroomResource
 * 
 * @author bgiles
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestRestApplicationContext.class })
public class ClassroomRestServerIntegrationTest {

    @Resource
    private String resourceBase;

    private ClassroomFinderRestClient finderClient;

    private ClassroomManagerRestClient managerClient;

    private TestRunManagerRestClient testClient;

    @Before
    public void init() {
        this.finderClient = new ClassroomFinderRestClientImpl(resourceBase + "classroom/");
        this.managerClient = new ClassroomManagerRestClientImpl(resourceBase + "classroom/");
        this.testClient = new TestRunManagerRestClientImpl(resourceBase + "testRun/");
    }

    @Test
    public void testGetAll() throws IOException {
        final Classroom[] classrooms = finderClient.getAllClassrooms();
        assertNotNull(classrooms);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUnknownClassroom() throws IOException {
        finderClient.getClassroom("11111111-1111-1111-1111-111111111111");
    }

    @Test(expected = RestClientException.class)
    public void testBadClassroomUuid() throws IOException {
        finderClient.getClassroom("bad-uuid");
    }

    @Test
    public void testLifecycle() throws IOException {
        final TestRun testRun = testClient.createTestRun();

        final String eng201Name = "Engineering 201 : " + testRun.getUuid();
        final Classroom expected = managerClient.createClassroomForTesting(eng201Name, testRun);
        assertEquals(eng201Name, expected.getName());

        final Classroom actual1 = finderClient.getClassroom(expected.getUuid());
        assertEquals(eng201Name, actual1.getName());

        final Classroom[] classrooms = finderClient.getAllClassrooms();
        assertTrue(classrooms.length > 0);

        // final int count = finderClient.getCount();
        // assertTrue(count > 0);

        final String eng202Name = "Engineering 201 : " + testRun.getUuid();
        final Classroom actual2 = managerClient.updateClassroom(actual1.getUuid(), eng202Name);
        assertEquals(eng202Name, actual2.getName());

        managerClient.deleteClassroom(actual1.getUuid());
        try {
            finderClient.getClassroom(expected.getUuid());
            fail("should have thrown exception");
        } catch (ObjectNotFoundException e) {
            // do nothing
        }

        testClient.deleteTestRun(testRun.getUuid());
    }
}
