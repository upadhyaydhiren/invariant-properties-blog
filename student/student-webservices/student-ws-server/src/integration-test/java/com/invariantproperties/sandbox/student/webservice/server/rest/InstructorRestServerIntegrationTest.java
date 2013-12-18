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

import com.invariantproperties.sandbox.student.domain.Instructor;
import com.invariantproperties.sandbox.student.webservice.client.InstructorRestClient;
import com.invariantproperties.sandbox.student.webservice.client.InstructorRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.client.ObjectNotFoundException;

/**
 * Integration tests for InstructorResource
 * 
 * @author bgiles
 */
public class InstructorRestServerIntegrationTest {

    final InstructorRestClient client = new InstructorRestClientImpl("http://localhost:8080/rest/instructor/");

    @Test
    public void testGetAll() throws IOException {
        final Instructor[] instructors = client.getAllInstructors();
        assertNotNull(instructors);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUnknownInstructor() throws IOException {
        client.getInstructor("missing");
    }

    @Test
    public void testLifecycle() throws IOException {
        final String davidName = "David";
        final String davidEmail = "david@example.com";
        final Instructor expected = client.createInstructor(davidName, davidEmail);
        assertEquals(davidName, expected.getName());
        assertEquals(davidEmail, expected.getEmailAddress());

        final Instructor actual1 = client.getInstructor(expected.getUuid());
        assertEquals(davidName, actual1.getName());
        assertEquals(davidEmail, actual1.getEmailAddress());

        final Instructor[] instructors = client.getAllInstructors();
        assertTrue(instructors.length > 0);

        final String edithName = "Edith";
        final String edithEmail = "edith@example.com";
        final Instructor actual2 = client.updateInstructor(actual1.getUuid(), edithName, edithEmail);
        assertEquals(edithName, actual2.getName());
        assertEquals(edithEmail, actual2.getEmailAddress());

        client.deleteInstructor(actual1.getUuid());
        try {
            client.getInstructor(expected.getUuid());
            fail("should have thrown exception");
        } catch (ObjectNotFoundException e) {
            // do nothing
        }
    }
}
