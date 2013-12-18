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

import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.business.StudentService;
import com.invariantproperties.sandbox.student.domain.Student;

/**
 * Unit tests for StudentResource.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class StudentResourceTest {
    private Student david = new Student();
    private Student edith = new Student();

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
    public void testFindAllStudents() {
        final List<Student> expected = Arrays.asList(david);

        final StudentService service = Mockito.mock(StudentService.class);
        when(service.findAllStudents()).thenReturn(expected);

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.findAllStudents();

        assertEquals(200, response.getStatus());
        final Student[] actual = (Student[]) response.getEntity();
        assertEquals(expected.size(), actual.length);
        assertNull(actual[0].getId());
        assertEquals(expected.get(0).getName(), actual[0].getName());
        assertEquals(expected.get(0).getEmailAddress(), actual[0].getEmailAddress());
        assertEquals(expected.get(0).getUuid(), actual[0].getUuid());
    }

    @Test
    public void testFindAllStudentsEmpty() {
        final List<Student> expected = new ArrayList<>();

        final StudentService service = Mockito.mock(StudentService.class);
        when(service.findAllStudents()).thenReturn(expected);

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.findAllStudents();

        assertEquals(200, response.getStatus());
        final Student[] actual = (Student[]) response.getEntity();
        assertEquals(0, actual.length);
    }

    @Test
    public void testFindAllStudentsFailure() {
        final StudentService service = Mockito.mock(StudentService.class);
        when(service.findAllStudents()).thenThrow(new UnitTestException());

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.findAllStudents();

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetStudent() {
        final Student expected = david;

        final StudentService service = Mockito.mock(StudentService.class);
        when(service.findStudentByUuid(expected.getUuid())).thenReturn(expected);

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.getStudent(expected.getUuid());

        assertEquals(200, response.getStatus());
        final Student actual = (Student) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmailAddress(), actual.getEmailAddress());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    @Test
    public void testGetStudentMissing() {
        final StudentService service = Mockito.mock(StudentService.class);
        when(service.findStudentByUuid(david.getUuid())).thenThrow(new ObjectNotFoundException(david.getUuid()));

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.getStudent(david.getUuid());

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetStudentFailure() {
        final StudentService service = Mockito.mock(StudentService.class);
        when(service.findStudentByUuid(david.getUuid())).thenThrow(new UnitTestException());

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.getStudent(david.getUuid());

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateStudent() {
        final Student expected = david;
        final NameAndEmailAddress req = new NameAndEmailAddress();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final StudentService service = Mockito.mock(StudentService.class);
        when(service.createStudent(req.getName(), req.getEmailAddress())).thenReturn(expected);

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.createStudent(req);

        assertEquals(201, response.getStatus());
        final Student actual = (Student) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmailAddress(), actual.getEmailAddress());
    }

    /**
     * Test handling when the student can't be created for some reason. For now
     * the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testCreateStudentProblem() {
        final Student expected = david;
        final NameAndEmailAddress req = new NameAndEmailAddress();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final StudentService service = Mockito.mock(StudentService.class);
        when(service.createStudent(req.getName(), req.getEmailAddress())).thenReturn(null);

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.createStudent(req);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateStudentFailure() {
        final Student expected = david;
        final NameAndEmailAddress req = new NameAndEmailAddress();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final StudentService service = Mockito.mock(StudentService.class);
        when(service.createStudent(req.getName(), req.getEmailAddress())).thenThrow(new UnitTestException());

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.createStudent(req);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateStudent() {
        final Student expected = david;
        final NameAndEmailAddress req = new NameAndEmailAddress();
        req.setName(edith.getName());
        req.setEmailAddress(edith.getEmailAddress());
        final Student updated = new Student();
        updated.setId(expected.getId());
        updated.setName(edith.getName());
        updated.setEmailAddress(edith.getEmailAddress());
        updated.setUuid(expected.getUuid());

        final StudentService service = Mockito.mock(StudentService.class);
        when(service.findStudentByUuid(expected.getUuid())).thenReturn(expected);
        when(service.updateStudent(expected, req.getName(), req.getEmailAddress())).thenReturn(updated);

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.updateStudent(expected.getUuid(), req);

        assertEquals(200, response.getStatus());
        final Student actual = (Student) response.getEntity();
        assertNull(actual.getId());
        assertEquals(edith.getName(), actual.getName());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    /**
     * Test handling when the student can't be updated for some reason. For now
     * the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testUpdateStudentProblem() {
        final Student expected = david;
        final NameAndEmailAddress req = new NameAndEmailAddress();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final StudentService service = Mockito.mock(StudentService.class);
        when(service.updateStudent(expected, req.getName(), req.getEmailAddress())).thenReturn(null);

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.createStudent(req);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateStudentFailure() {
        final Student expected = david;
        final NameAndEmailAddress req = new NameAndEmailAddress();
        req.setName(expected.getName());
        req.setEmailAddress(expected.getEmailAddress());

        final StudentService service = Mockito.mock(StudentService.class);
        when(service.updateStudent(expected, req.getName(), req.getEmailAddress())).thenThrow(new UnitTestException());

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.createStudent(req);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeleteStudent() {
        final Student expected = david;

        final StudentService service = Mockito.mock(StudentService.class);
        doNothing().when(service).deleteStudent(expected.getUuid());

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.deleteStudent(expected.getUuid());

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteStudentMissing() {
        final Student expected = david;
        final Name name = new Name();
        name.setName(expected.getName());

        final StudentService service = Mockito.mock(StudentService.class);
        doThrow(new ObjectNotFoundException(expected.getUuid())).when(service).deleteStudent(expected.getUuid());

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.deleteStudent(expected.getUuid());

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteStudentFailure() {
        final Student expected = david;

        final StudentService service = Mockito.mock(StudentService.class);
        doThrow(new UnitTestException()).when(service).deleteStudent(expected.getUuid());

        final StudentResource resource = new StudentResource(service);
        final Response response = resource.deleteStudent(expected.getUuid());

        assertEquals(500, response.getStatus());
    }
}
