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

import com.invariantproperties.sandbox.student.domain.Course;
import com.invariantproperties.sandbox.student.repository.CourseRepository;

/**
 * Unit tests for CourseServiceImpl.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class CourseServiceImplTest {

    @Test
    public void testFindAllCourses() {
        final List<Course> expected = Collections.emptyList();

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findAll()).thenReturn(expected);

        final CourseService service = new CourseServiceImpl(repository);
        final List<Course> actual = service.findAllCourses();

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testFindAllCoursesError() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCoursesByTestRun(null)).thenThrow(new UnitTestException());

        final CourseService service = new CourseServiceImpl(repository);
        service.findAllCourses();
    }

    @Test
    public void testFindCourseById() {
        final Course expected = new Course();
        expected.setId(1);

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(expected);

        final CourseService service = new CourseServiceImpl(repository);
        final Course actual = service.findCourseById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindCourseByIdMissing() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findOne(any(Integer.class))).thenReturn(null);

        final CourseService service = new CourseServiceImpl(repository);
        service.findCourseById(1);
    }

    @Test(expected = PersistenceException.class)
    public void testFindCourseByIdError() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findOne(any(Integer.class))).thenThrow(new UnitTestException());

        final CourseService service = new CourseServiceImpl(repository);
        service.findCourseById(1);
    }

    @Test
    public void testFindCourseByUuid() {
        final Course expected = new Course();
        expected.setUuid("[uuid]");

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(expected);

        final CourseService service = new CourseServiceImpl(repository);
        final Course actual = service.findCourseByUuid(expected.getUuid());

        assertEquals(expected, actual);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testFindCourseByUuidMissing() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(null);

        final CourseService service = new CourseServiceImpl(repository);
        service.findCourseByUuid("[uuid]");
    }

    @Test(expected = PersistenceException.class)
    public void testFindCourseByUuidError() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenThrow(new UnitTestException());

        final CourseService service = new CourseServiceImpl(repository);
        service.findCourseByUuid("[uuid]");
    }

    @Test
    public void testCreateCourse() {
        final Course expected = new Course();
        expected.setName("name");
        expected.setUuid("[uuid]");

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.saveAndFlush(any(Course.class))).thenReturn(expected);

        final CourseService service = new CourseServiceImpl(repository);
        final Course actual = service.createCourse(expected.getName());

        assertEquals(expected, actual);
    }

    @Test(expected = PersistenceException.class)
    public void testCreateCourseError() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.saveAndFlush(any(Course.class))).thenThrow(new UnitTestException());

        final CourseService service = new CourseServiceImpl(repository);
        service.createCourse("name");
    }

    @Test
    public void testUpdateCourse() {
        final Course expected = new Course();
        expected.setName("Alice");
        expected.setUuid("[uuid]");

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(expected);
        when(repository.saveAndFlush(any(Course.class))).thenReturn(expected);

        final CourseService service = new CourseServiceImpl(repository);
        final Course actual = service.updateCourse(expected, "Bob");

        assertEquals("Bob", actual.getName());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateCourseMissing() {
        final Course expected = new Course();
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(null);

        final CourseService service = new CourseServiceImpl(repository);
        service.updateCourse(expected, "Bob");
    }

    @Test(expected = PersistenceException.class)
    public void testUpdateCourseError() {
        final Course expected = new Course();
        expected.setUuid("[uuid]");

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).saveAndFlush(any(Course.class));

        final CourseService service = new CourseServiceImpl(repository);
        service.updateCourse(expected, "Bob");
    }

    @Test
    public void testDeleteCourse() {
        final Course expected = new Course();
        expected.setUuid("[uuid]");

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(expected);
        doNothing().when(repository).delete(any(Course.class));

        final CourseService service = new CourseServiceImpl(repository);
        service.deleteCourse(expected.getUuid());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteCourseMissing() {
        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(null);

        final CourseService service = new CourseServiceImpl(repository);
        service.deleteCourse("[uuid]");
    }

    @Test(expected = PersistenceException.class)
    public void testDeleteCourseError() {
        final Course expected = new Course();
        expected.setUuid("[uuid]");

        final CourseRepository repository = Mockito.mock(CourseRepository.class);
        when(repository.findCourseByUuid(any(String.class))).thenReturn(expected);
        doThrow(new UnitTestException()).when(repository).delete(any(Course.class));

        final CourseService service = new CourseServiceImpl(repository);
        service.deleteCourse(expected.getUuid());
    }
}
