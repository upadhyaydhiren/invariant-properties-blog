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
package com.invariantproperties.sandbox.student.webservice.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.invariantproperties.sandbox.student.domain.Course;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Unit tests for CourseRestClientImpl. Remember that we want to test the
 * behavior, not the implementation.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class CourseRestClientImplTest {
	private static final String UUID = "uuid";
	private static final String NAME = "name";

	@Test
	public void testGetAllCoursesEmpty() {
		CourseRestClient client = new CourseRestClientMock(200, new Course[0]);
		Course[] results = client.getAllCourses();
		assertEquals(0, results.length);
	}

	@Test
	public void testGetAllCoursesNonEmpty() {
		Course course = new Course();
		course.setUuid(UUID);
		CourseRestClient client = new CourseRestClientMock(200,
				new Course[] { course });
		Course[] results = client.getAllCourses();
		assertEquals(1, results.length);
	}

	@Test(expected = RestClientFailureException.class)
	public void testGetAllCoursesError() {
		CourseRestClient client = new CourseRestClientMock(500, null);
		client.getAllCourses();
	}

	@Test
	public void testGetCourse() {
		Course expected = new Course();
		expected.setUuid(UUID);
		CourseRestClient client = new CourseRestClientMock(200, expected);
		Course actual = client.getCourse(expected.getUuid());
		assertEquals(expected.getUuid(), actual.getUuid());
		// assertEquals(CourseRestClientMock.RESOURCE + course.getUuid(),
		// actual.getSelf());
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testGetCourseMissing() {
		CourseRestClient client = new CourseRestClientMock(404, null);
		client.getCourse(UUID);
	}

	@Test(expected = RestClientFailureException.class)
	public void testGetCourseError() {
		CourseRestClient client = new CourseRestClientMock(500, null);
		client.getCourse(UUID);
	}

	@Test
	public void testCreateCourse() {
		Course expected = new Course();
		expected.setName(NAME);
		CourseRestClient client = new CourseRestClientMock(
				Response.Status.CREATED.getStatusCode(), expected);
		Course actual = client.createCourse(expected.getName());
		assertEquals(expected.getName(), actual.getName());
		// assertEquals(CourseRestClientMock.RESOURCE + results.getUuid(),
		// actual.getSelf());
	}

	@Test(expected = RestClientFailureException.class)
	public void testCreateCourseError() {
		CourseRestClient client = new CourseRestClientMock(500, null);
		client.createCourse(UUID);
	}

	@Test
	public void testUpdateCourse() {
		Course expected = new Course();
		expected.setUuid(UUID);
		expected.setName(NAME);
		CourseRestClient client = new CourseRestClientMock(200, expected);
		Course actual = client.updateCourse(expected.getUuid(),
				expected.getName());
		assertEquals(expected.getUuid(), actual.getUuid());
		assertEquals(expected.getName(), actual.getName());
		// assertEquals(CourseRestClientMock.RESOURCE + course.getUuid(),
		// actual.getSelf());
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testUpdateCourseMissing() {
		CourseRestClient client = new CourseRestClientMock(404, null);
		client.updateCourse(UUID, NAME);
	}

	@Test(expected = RestClientFailureException.class)
	public void testUpdateCourseError() {
		CourseRestClient client = new CourseRestClientMock(500, null);
		client.updateCourse(UUID, NAME);
	}

	@Test
	public void testDeleteCourse() {
		Course course = new Course();
		course.setUuid(UUID);
		CourseRestClient client = new CourseRestClientMock(204, null);
		client.deleteCourse(course.getUuid());
	}

	@Test
	public void testDeleteCourseMissing() {
		CourseRestClient client = new CourseRestClientMock(204, null);
		client.deleteCourse(UUID);
	}

	@Test(expected = RestClientFailureException.class)
	public void testDeleteCourseError() {
		CourseRestClient client = new CourseRestClientMock(500, null);
		client.deleteCourse(UUID);
	}
}

/**
 * CourseRestClientImpl extended to mock jersey API. This class requires
 * implementation details.
 */
class CourseRestClientMock extends CourseRestClientImpl {
	static final String RESOURCE = "test://rest/course/";
	private Client client;
	private WebResource webResource;
	private WebResource.Builder webResourceBuilder;
	private ClientResponse response;
	private final int status;
	private final Object results;

	CourseRestClientMock(int status, Object results) {
		super(RESOURCE);
		this.status = status;
		this.results = results;
	}

	/**
	 * Override createClient() so it returns mocked object. These expectations
	 * will handle basic CRUD operations, more advanced functionality will
	 * require inspecting JSON payload of POST call.
	 */
	Client createClient() {
		client = Mockito.mock(Client.class);
		webResource = Mockito.mock(WebResource.class);
		webResourceBuilder = Mockito.mock(WebResource.Builder.class);
		response = Mockito.mock(ClientResponse.class);
		when(client.resource(any(String.class))).thenReturn(webResource);
		when(webResource.accept(any(String.class))).thenReturn(
				webResourceBuilder);
		when(webResource.type(any(String.class)))
				.thenReturn(webResourceBuilder);
		when(webResourceBuilder.accept(any(String.class))).thenReturn(
				webResourceBuilder);
		when(webResourceBuilder.type(any(String.class))).thenReturn(
				webResourceBuilder);
		when(webResourceBuilder.get(eq(ClientResponse.class))).thenReturn(
				response);
		when(
				webResourceBuilder.post(eq(ClientResponse.class),
						any(String.class))).thenReturn(response);
		when(
				webResourceBuilder.put(eq(ClientResponse.class),
						any(String.class))).thenReturn(response);
		when(webResourceBuilder.delete(eq(ClientResponse.class))).thenReturn(
				response);
		when(response.getStatus()).thenReturn(status);
		when(response.getEntity(any(Class.class))).thenReturn(results);
		return client;
	}
}
