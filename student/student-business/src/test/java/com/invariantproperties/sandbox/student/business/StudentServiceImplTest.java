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

import com.invariantproperties.sandbox.student.domain.Student;
import com.invariantproperties.sandbox.student.repository.StudentRepository;

/**
 * Unit tests for StudentServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class StudentServiceImplTest {

    @Test
    public void testFindAllStudents() {
        final List<Student> expected = Collections.emptyList();

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findAll()).thenReturn(expected);

        final StudentService service = new StudentServiceImpl(repository);
        final List<Student> actual = service.findAllStudents();

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindAllStudentsError() {
        final List<Student> expected = Collections.emptyList();

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findAll()).thenThrow(new UnitTestException());

        final StudentService service = new StudentServiceImpl(repository);
        final List<Student> actual = service.findAllStudents();

        assertEquals(expected, actual);
    }

    @Test
    public void testFindStudentById() {
        final Student expected = new Student();
        expected.setId(1);

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(expected);

        final StudentService service = new StudentServiceImpl(repository);
        final Student actual = service.findStudentById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindStudentByIdMissing() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(null);

        final StudentService service = new StudentServiceImpl(repository);
        service.findStudentById(1);
    }

    @Test(expected = PersistenceException.class)
    public void testFindStudentByIdError() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findOne(any(Integer.class))).thenThrow(new UnitTestException());

        final StudentService service = new StudentServiceImpl(repository);
        service.findStudentById(1);
    }

    @Test
    public void testFindStudentByUuid() {
        final Student expected = new Student();
        expected.setUuid("[uuid]");

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(expected);

        final StudentService service = new StudentServiceImpl(repository);
        final Student actual = service.findStudentByUuid(expected.getUuid());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindStudentByUuidMissing() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(null);

        final StudentService service = new StudentServiceImpl(repository);
        service.findStudentByUuid("[uuid]");
    }

    @Test(expected = PersistenceException.class)
    public void testFindStudentByUuidError() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenThrow(new UnitTestException());

        final StudentService service = new StudentServiceImpl(repository);
        service.findStudentByUuid("[uuid]");
    }

    @Test
    public void testCreateStudent() {
        final Student expected = new Student();
        expected.setName("name");
        expected.setEmailAddress("email");
        expected.setUuid("[uuid]");

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.saveAndFlush(any(Student.class))).thenReturn(expected);

        final StudentService service = new StudentServiceImpl(repository);
        final Student actual = service.createStudent(expected.getName(), expected.getEmailAddress());

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCreateStudentError() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.saveAndFlush(any(Student.class))).thenThrow(new UnitTestException());

        final StudentService service = new StudentServiceImpl(repository);
        service.createStudent("name", "email");
    }

    @Test
    public void testUpdateStudent() {
        final Student expected = new Student();
        expected.setName("Alice");
        expected.setName("alice@example.com");
        expected.setUuid("[uuid]");

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(expected);
        when(repository.saveAndFlush(any(Student.class))).thenReturn(expected);

        final StudentService service = new StudentServiceImpl(repository);
        final Student actual = service.updateStudent(expected, "Bob", "bob@example.com");

        assertEquals("Bob", actual.getName());
        assertEquals("bob@example.com", actual.getEmailAddress());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateStudentMissing() {
        final Student expected = new Student();
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(null);

        final StudentService service = new StudentServiceImpl(repository);
        service.updateStudent(expected, "Bob", "bob@example.com");
    }

    @Test(expected = PersistenceException.class)
    public void testUpdateStudentError() {
        final Student expected = new Student();
        expected.setUuid("[uuid]");

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).saveAndFlush(any(Student.class));

        final StudentService service = new StudentServiceImpl(repository);
        service.updateStudent(expected, "Bob", "bob@example.com");
    }

    @Test
    public void testDeleteStudent() {
        final Student expected = new Student();
        expected.setUuid("[uuid]");

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(expected);
        doNothing().when(repository).delete(any(Student.class));

        final StudentService service = new StudentServiceImpl(repository);
        service.deleteStudent(expected.getUuid());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteStudentMissing() {
        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(null);

        final StudentService service = new StudentServiceImpl(repository);
        service.deleteStudent("[uuid]");
    }

    @Test(expected = PersistenceException.class)
    public void testDeleteStudentError() {
        final Student expected = new Student();
        expected.setUuid("[uuid]");

        final StudentRepository repository = Mockito.mock(StudentRepository.class);
        when(repository.findStudentByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).delete(any(Student.class));

        final StudentService service = new StudentServiceImpl(repository);
        service.deleteStudent(expected.getUuid());
    }
}
