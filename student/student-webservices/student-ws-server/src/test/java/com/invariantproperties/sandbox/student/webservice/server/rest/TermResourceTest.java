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

import com.invariantproperties.sandbox.student.business.CourseService;
import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.domain.Course;

/**
 * Unit tests for CourseResource.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class TermResourceTest {
    private Course fall2013 = new Course();
    private Course fall2014 = new Course();

    @Before
    public void init() {
        fall2013.setId(1);
        fall2013.setName("Fall 2013");
        fall2013.setUuid(UUID.randomUUID().toString());

        fall2014.setId(2);
        fall2014.setName("Fall 2014");
        fall2014.setUuid(UUID.randomUUID().toString());
    }

    @Test
    public void testFindAllCourses() {
        final List<Course> expected = Arrays.asList(fall2013);

        final CourseService service = Mockito.mock(CourseService.class);
        when(service.findAllCourses()).thenReturn(expected);

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.findAllCourses();

        assertEquals(200, response.getStatus());
        final Course[] actual = (Course[]) response.getEntity();
        assertEquals(expected.size(), actual.length);
        assertNull(actual[0].getId());
        assertEquals(expected.get(0).getName(), actual[0].getName());
        assertEquals(expected.get(0).getUuid(), actual[0].getUuid());
    }

    @Test
    public void testFindAllCoursesEmpty() {
        final List<Course> expected = new ArrayList<>();

        final CourseService service = Mockito.mock(CourseService.class);
        when(service.findAllCourses()).thenReturn(expected);

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.findAllCourses();

        assertEquals(200, response.getStatus());
        final Course[] actual = (Course[]) response.getEntity();
        assertEquals(0, actual.length);
    }

    @Test
    public void testFindAllCoursesFailure() {
        final CourseService service = Mockito.mock(CourseService.class);
        when(service.findAllCourses()).thenThrow(new UnitTestException());

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.findAllCourses();

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetCourse() {
        final Course expected = fall2013;

        final CourseService service = Mockito.mock(CourseService.class);
        when(service.findCourseByUuid(expected.getUuid())).thenReturn(expected);

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.getCourse(expected.getUuid());

        assertEquals(200, response.getStatus());
        final Course actual = (Course) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    @Test
    public void testGetCourseMissing() {
        final CourseService service = Mockito.mock(CourseService.class);
        when(service.findCourseByUuid(fall2013.getUuid())).thenThrow(new ObjectNotFoundException(fall2013.getUuid()));

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.getCourse(fall2013.getUuid());

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetCourseFailure() {
        final CourseService service = Mockito.mock(CourseService.class);
        when(service.findCourseByUuid(fall2013.getUuid())).thenThrow(new UnitTestException());

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.getCourse(fall2013.getUuid());

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateCourse() {
        final Course expected = fall2013;
        final Name name = new Name();
        name.setName(expected.getName());

        final CourseService service = Mockito.mock(CourseService.class);
        when(service.createCourse(name.getName())).thenReturn(expected);

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.createCourse(name);

        assertEquals(201, response.getStatus());
        final Course actual = (Course) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void testCreateCourseBlankName() {
        final Name name = new Name();

        final CourseService service = Mockito.mock(CourseService.class);
        when(service.createCourse(name.getName())).thenReturn(null);

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.createCourse(name);

        assertEquals(400, response.getStatus());
    }

    /**
     * Test handling when the course can't be created for some reason. For now
     * the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testCreateCourseProblem() {
        final Course expected = fall2013;
        final Name name = new Name();
        name.setName(expected.getName());

        final CourseService service = Mockito.mock(CourseService.class);
        when(service.createCourse(name.getName())).thenReturn(null);

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.createCourse(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateCourseFailure() {
        final Course expected = fall2013;
        final Name name = new Name();
        name.setName(expected.getName());

        final CourseService service = Mockito.mock(CourseService.class);
        when(service.createCourse(name.getName())).thenThrow(new UnitTestException());

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.createCourse(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateCourse() {
        final Course expected = fall2013;
        final Name name = new Name();
        name.setName(fall2014.getName());
        final Course updated = new Course();
        updated.setId(expected.getId());
        updated.setName(fall2014.getName());
        updated.setUuid(expected.getUuid());

        final CourseService service = Mockito.mock(CourseService.class);
        when(service.findCourseByUuid(expected.getUuid())).thenReturn(expected);
        when(service.updateCourse(expected, name.getName())).thenReturn(updated);

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.updateCourse(expected.getUuid(), name);

        assertEquals(200, response.getStatus());
        final Course actual = (Course) response.getEntity();
        assertNull(actual.getId());
        assertEquals(fall2014.getName(), actual.getName());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    @Test
    public void testUpdateCourseBlankName() {
        final Course expected = fall2013;
        final Name name = new Name();

        final CourseService service = Mockito.mock(CourseService.class);
        when(service.createCourse(name.getName())).thenReturn(null);

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.updateCourse(expected.getUuid(), name);

        assertEquals(400, response.getStatus());
    }

    /**
     * Test handling when the course can't be updated for some reason. For now
     * the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testUpdateCourseProblem() {
        final Course expected = fall2013;
        final Name name = new Name();
        name.setName(expected.getName());

        final CourseService service = Mockito.mock(CourseService.class);
        when(service.updateCourse(expected, name.getName())).thenReturn(null);

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.createCourse(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateCourseFailure() {
        final Course expected = fall2013;
        final Name name = new Name();
        name.setName(expected.getName());

        final CourseService service = Mockito.mock(CourseService.class);
        when(service.updateCourse(expected, name.getName())).thenThrow(new UnitTestException());

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.createCourse(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeleteCourse() {
        final Course expected = fall2013;

        final CourseService service = Mockito.mock(CourseService.class);
        doNothing().when(service).deleteCourse(expected.getUuid());

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.deleteCourse(expected.getUuid());

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteCourseMissing() {
        final Course expected = fall2013;
        final Name name = new Name();
        name.setName(expected.getName());

        final CourseService service = Mockito.mock(CourseService.class);
        doThrow(new ObjectNotFoundException(expected.getUuid())).when(service).deleteCourse(expected.getUuid());

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.deleteCourse(expected.getUuid());

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteCourseFailure() {
        final Course expected = fall2013;

        final CourseService service = Mockito.mock(CourseService.class);
        doThrow(new UnitTestException()).when(service).deleteCourse(expected.getUuid());

        final CourseResource resource = new CourseResource(service);
        final Response response = resource.deleteCourse(expected.getUuid());

        assertEquals(500, response.getStatus());
    }
}