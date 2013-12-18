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

import org.junit.Test;

import com.invariantproperties.sandbox.student.domain.Classroom;
import com.invariantproperties.sandbox.student.webservice.client.ClassroomRestClient;
import com.invariantproperties.sandbox.student.webservice.client.ClassroomRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.client.ObjectNotFoundException;

/**
 * Integration tests for ClassroomResource
 * 
 * @author bgiles
 */
public class ClassroomRestServerIntegrationTest {

    private final ClassroomRestClient client = new ClassroomRestClientImpl("http://localhost:8080/rest/classroom/");

    @Test
    public void testGetAll() throws IOException {
        final Classroom[] classrooms = client.getAllClassrooms();
        assertNotNull(classrooms);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUnknownClassroom() throws IOException {
        client.getClassroom("missing");
    }

    @Test
    public void testLifecycle() throws IOException {
        final String eng201Name = "Engineering 201";
        final Classroom expected = client.createClassroom(eng201Name);
        assertEquals(eng201Name, expected.getName());

        final Classroom actual1 = client.getClassroom(expected.getUuid());
        assertEquals(eng201Name, actual1.getName());

        final Classroom[] classrooms = client.getAllClassrooms();
        assertTrue(classrooms.length > 0);

        final String eng202Name = "Engineering 201";
        final Classroom actual2 = client.updateClassroom(actual1.getUuid(), eng202Name);
        assertEquals(eng202Name, actual2.getName());

        client.deleteClassroom(actual1.getUuid());
        try {
            client.getClassroom(expected.getUuid());
            fail("should have thrown exception");
        } catch (ObjectNotFoundException e) {
            // do nothing
        }
    }
}
