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

import com.invariantproperties.sandbox.student.domain.Course;
import com.invariantproperties.sandbox.student.webservice.client.CourseRestClient;
import com.invariantproperties.sandbox.student.webservice.client.CourseRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.client.ObjectNotFoundException;

/**
 * Integration tests for CourseResource
 * 
 * @author bgiles
 */
public class CourseRestServerIntegrationTest {

	private final CourseRestClient client = new CourseRestClientImpl(
			"http://localhost:8080/rest/course/");

	@Test
	public void testGetAll() throws IOException {
		final Course[] courses = client.getAllCourses();
		assertNotNull(courses);
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testUnknownCourse() throws IOException {
		client.getCourse("missing");
	}

	@Test
	public void testLifecycle() throws IOException {
		final String physicsName = "Physics 201";
		final Course expected = client.createCourse(physicsName);
		assertEquals(physicsName, expected.getName());

		final Course actual1 = client.getCourse(expected.getUuid());
		assertEquals(physicsName, actual1.getName());

		final Course[] courses = client.getAllCourses();
		assertTrue(courses.length > 0);

		final String mechanicsName = "Newtonian Mechanics 201";
		final Course actual2 = client.updateCourse(actual1.getUuid(),
				mechanicsName);
		assertEquals(mechanicsName, actual2.getName());

		client.deleteCourse(actual1.getUuid());
		try {
			client.getCourse(expected.getUuid());
			fail("should have thrown exception");
		} catch (ObjectNotFoundException e) {
			// do nothing
		}
	}
}
