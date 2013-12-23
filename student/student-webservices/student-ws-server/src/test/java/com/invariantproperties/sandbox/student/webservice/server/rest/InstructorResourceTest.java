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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.invariantproperties.sandbox.student.business.InstructorService;
import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.business.TestRunService;
import com.invariantproperties.sandbox.student.domain.Instructor;

/**
 * Unit tests for InstructorResource.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class InstructorResourceTest {
    private Instructor david = new Instructor();
    private Instructor edith = new Instructor();

    @Before
    public void init() {
        david.setId(1);
        david.setName("David");
        david.setEmailAddress("david@example.com");
        david.setUuid(UUID.randomUUID().toString());

        edith.setId(2);
        edith.setName("Edith");
        edith.setEmailAddress("edith@example.com");
        edith.setUuid(UUID.randomUUID().toString());
    }

    @Test
    public void testFindAllInstructors() {
        final List<Instructor> expected = Arrays.asList(david);

        final InstructorService service = Mockito.mock(InstructorService.class);
        when(service.findAllInstructors()).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.findAllInstructors();

        assertEquals(200, response.getStatus());
        final Instructor[] actual = (Instructor[]) response.getEntity();
        assertEquals(expected.size(), actual.length);
        assertNull(actual[0].getId());
        assertEquals(expected.get(0).getName(), actual[0].getName());
        assertEquals(expected.get(0).getEmailAddress(), actual[0].getEmailAddress());
        assertEquals(expected.get(0).getUuid(), actual[0].getUuid());
    }

    @Test
    public void testFindAllInstructorsEmpty() {
        final List<Instructor> expected = new ArrayList<>();

        final InstructorService service = Mockito.mock(InstructorService.class);
        when(service.findAllInstructors()).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.findAllInstructors();

        assertEquals(200, response.getStatus());
        final Instructor[] actual = (Instructor[]) response.getEntity();
        assertEquals(0, actual.length);
    }

    @Test
    public void testFindAllInstructorsFailure() {
        final InstructorService service = Mockito.mock(InstructorService.class);
        when(service.findAllInstructors()).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.findAllInstructors();

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetInstructor() {
        final Instructor expected = david;

        final InstructorService service = Mockito.mock(InstructorService.class);
        when(service.findInstructorByUuid(expected.getUuid())).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.getInstructor(expected.getUuid());

        assertEquals(200, response.getStatus());
        final Instructor actual = (Instructor) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmailAddress(), actual.getEmailAddress());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    @Test
    public void testGetInstructorMissing() {
        final InstructorService service = Mockito.mock(InstructorService.class);
        when(service.findInstructorByUuid(david.getUuid())).thenThrow(new ObjectNotFoundException(david.getUuid()));

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.getInstructor(david.getUuid());

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetInstructorFailure() {
        final InstructorService service = Mockito.mock(InstructorService.class);
        when(service.findInstructorByUuid(david.getUuid())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.getInstructor(david.getUuid());

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateInstructor() {
        final Instructor expected = david;
        final NameAndEmailAddress req = new NameAndEmailAddress();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final InstructorService service = Mockito.mock(InstructorService.class);
        when(service.createInstructor(req.getName(), req.getEmailAddress())).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.createInstructor(req);

        assertEquals(201, response.getStatus());
        final Instructor actual = (Instructor) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmailAddress(), actual.getEmailAddress());
    }

    /**
     * Test handling when the instructor can't be created for some reason. For
     * now the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testCreateInstructorProblem() {
        final Instructor expected = david;
        final NameAndEmailAddress req = new NameAndEmailAddress();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final InstructorService service = Mockito.mock(InstructorService.class);
        when(service.createInstructor(req.getName(), req.getEmailAddress())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.createInstructor(req);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateInstructorFailure() {
        final Instructor expected = david;
        final NameAndEmailAddress req = new NameAndEmailAddress();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final InstructorService service = Mockito.mock(InstructorService.class);
        when(service.createInstructor(req.getName(), req.getEmailAddress())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.createInstructor(req);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateInstructor() {
        final Instructor expected = david;
        final NameAndEmailAddress req = new NameAndEmailAddress();
        req.setName(edith.getName());
        req.setEmailAddress(edith.getEmailAddress());
        final Instructor updated = new Instructor();
        updated.setId(expected.getId());
        updated.setName(edith.getName());
        updated.setEmailAddress(edith.getEmailAddress());
        updated.setUuid(expected.getUuid());

        final InstructorService service = Mockito.mock(InstructorService.class);
        when(service.findInstructorByUuid(expected.getUuid())).thenReturn(expected);
        when(service.updateInstructor(expected, req.getName(), req.getEmailAddress())).thenReturn(updated);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.updateInstructor(expected.getUuid(), req);

        assertEquals(200, response.getStatus());
        final Instructor actual = (Instructor) response.getEntity();
        assertNull(actual.getId());
        assertEquals(edith.getName(), actual.getName());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    /**
     * Test handling when the instructor can't be updated for some reason. For
     * now the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testUpdateInstructorProblem() {
        final Instructor expected = david;
        final NameAndEmailAddress req = new NameAndEmailAddress();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final InstructorService service = Mockito.mock(InstructorService.class);
        when(service.updateInstructor(expected, req.getName(), req.getEmailAddress())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.createInstructor(req);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateInstructorFailure() {
        final Instructor expected = david;
        final NameAndEmailAddress req = new NameAndEmailAddress();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final InstructorService service = Mockito.mock(InstructorService.class);
        when(service.updateInstructor(expected, req.getName(), req.getEmailAddress())).thenThrow(
                new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.createInstructor(req);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeleteInstructor() {
        final Instructor expected = david;

        final InstructorService service = Mockito.mock(InstructorService.class);
        doNothing().when(service).deleteInstructor(expected.getUuid());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.deleteInstructor(expected.getUuid());

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteInstructorMissing() {
        final Instructor expected = david;
        final Name name = new Name();
        name.setName(expected.getName());

        final InstructorService service = Mockito.mock(InstructorService.class);
        doThrow(new ObjectNotFoundException(expected.getUuid())).when(service).deleteInstructor(expected.getUuid());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.deleteInstructor(expected.getUuid());

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteInstructorFailure() {
        final Instructor expected = david;

        final InstructorService service = Mockito.mock(InstructorService.class);
        doThrow(new UnitTestException()).when(service).deleteInstructor(expected.getUuid());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final InstructorResource resource = new InstructorResource(service, testService);
        final Response response = resource.deleteInstructor(expected.getUuid());

        assertEquals(500, response.getStatus());
    }
}
