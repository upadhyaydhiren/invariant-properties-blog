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
package com.invariantproperties.sandbox.student.business;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.invariantproperties.sandbox.student.domain.Instructor;
import com.invariantproperties.sandbox.student.repository.InstructorRepository;

/**
 * Unit tests for InstructorServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class InstructorServiceImplTest {

    @Test
    public void testFindAllInstructors() {
        final List<Instructor> expected = Collections.emptyList();

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findAll()).thenReturn(expected);

        final InstructorService service = new InstructorServiceImpl(repository);
        final List<Instructor> actual = service.findAllInstructors();

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindAllInstructorsError() {
        final List<Instructor> expected = Collections.emptyList();

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findAll()).thenThrow(new UnitTestException());

        final InstructorService service = new InstructorServiceImpl(repository);
        final List<Instructor> actual = service.findAllInstructors();

        assertEquals(expected, actual);
    }

    @Test
    public void testFindInstructorById() {
        final Instructor expected = new Instructor();
        expected.setId(1);

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(expected);

        final InstructorService service = new InstructorServiceImpl(repository);
        final Instructor actual = service.findInstructorById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindInstructorByIdMissing() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(null);

        final InstructorService service = new InstructorServiceImpl(repository);
        service.findInstructorById(1);
    }

    @Test(expected = PersistenceException.class)
    public void testFindInstructorByIdError() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findOne(any(Integer.class))).thenThrow(new UnitTestException());

        final InstructorService service = new InstructorServiceImpl(repository);
        service.findInstructorById(1);
    }

    @Test
    public void testFindInstructorByUuid() {
        final Instructor expected = new Instructor();
        expected.setUuid("[uuid]");

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(expected);

        final InstructorService service = new InstructorServiceImpl(repository);
        final Instructor actual = service.findInstructorByUuid(expected.getUuid());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindInstructorByUuidMissing() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(null);

        final InstructorService service = new InstructorServiceImpl(repository);
        service.findInstructorByUuid("[uuid]");
    }

    @Test(expected = PersistenceException.class)
    public void testFindInstructorByUuidError() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenThrow(new UnitTestException());

        final InstructorService service = new InstructorServiceImpl(repository);
        service.findInstructorByUuid("[uuid]");
    }

    @Test
    public void testCreateInstructor() {
        final Instructor expected = new Instructor();
        expected.setName("name");
        expected.setEmailAddress("email");
        expected.setUuid("[uuid]");

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.saveAndFlush(any(Instructor.class))).thenReturn(expected);

        final InstructorService service = new InstructorServiceImpl(repository);
        final Instructor actual = service.createInstructor(expected.getName(), expected.getEmailAddress());

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCreateInstructorError() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.saveAndFlush(any(Instructor.class))).thenThrow(new UnitTestException());

        final InstructorService service = new InstructorServiceImpl(repository);
        service.createInstructor("name", "email");
    }

    @Test
    public void testUpdateInstructor() {
        final Instructor expected = new Instructor();
        expected.setName("Alice");
        expected.setName("alice@example.com");
        expected.setUuid("[uuid]");

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(expected);
        when(repository.saveAndFlush(any(Instructor.class))).thenReturn(expected);

        final InstructorService service = new InstructorServiceImpl(repository);
        final Instructor actual = service.updateInstructor(expected, "Bob", "bob@example.com");

        assertEquals("Bob", actual.getName());
        assertEquals("bob@example.com", actual.getEmailAddress());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateInstructorMissing() {
        final Instructor expected = new Instructor();
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(null);

        final InstructorService service = new InstructorServiceImpl(repository);
        service.updateInstructor(expected, "Bob", "bob@example.com");
    }

    @Test(expected = PersistenceException.class)
    public void testUpdateInstructorError() {
        final Instructor expected = new Instructor();
        expected.setUuid("[uuid]");

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).saveAndFlush(any(Instructor.class));

        final InstructorService service = new InstructorServiceImpl(repository);
        service.updateInstructor(expected, "Bob", "bob@example.com");
    }

    @Test
    public void testDeleteInstructor() {
        final Instructor expected = new Instructor();
        expected.setUuid("[uuid]");

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(expected);
        doNothing().when(repository).delete(any(Instructor.class));

        final InstructorService service = new InstructorServiceImpl(repository);
        service.deleteInstructor(expected.getUuid());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteInstructorMissing() {
        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(null);

        final InstructorService service = new InstructorServiceImpl(repository);
        service.deleteInstructor("[uuid]");
    }

    @Test(expected = PersistenceException.class)
    public void testDeleteInstructorError() {
        final Instructor expected = new Instructor();
        expected.setUuid("[uuid]");

        final InstructorRepository repository = Mockito.mock(InstructorRepository.class);
        when(repository.findInstructorByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).delete(any(Instructor.class));

        final InstructorService service = new InstructorServiceImpl(repository);
        service.deleteInstructor(expected.getUuid());
    }
}
