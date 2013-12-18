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

import com.invariantproperties.sandbox.student.domain.Student;
import com.invariantproperties.sandbox.student.webservice.client.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.webservice.client.StudentRestClient;
import com.invariantproperties.sandbox.student.webservice.client.StudentRestClientImpl;

/**
 * Integration tests for StudentResource
 * 
 * @author bgiles
 */
public class StudentRestServerIntegrationTest {

	final StudentRestClient client = new StudentRestClientImpl(
			"http://localhost:8080/rest/student/");

	@Test
	public void testGetAll() throws IOException {
		final Student[] students = client.getAllStudents();
		assertNotNull(students);
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testUnknownStudent() throws IOException {
		client.getStudent("missing");
	}

	@Test
	public void testLifecycle() throws IOException {
		final String davidName = "David";
		final String davidEmail = "david@example.com";
		final Student expected = client.createStudent(davidName, davidEmail);
		assertEquals(davidName, expected.getName());
		assertEquals(davidEmail, expected.getEmailAddress());

		final Student actual1 = client.getStudent(expected.getUuid());
		assertEquals(davidName, actual1.getName());
		assertEquals(davidEmail, actual1.getEmailAddress());

		final Student[] students = client.getAllStudents();
		assertTrue(students.length > 0);

		final String edithName = "Edith";
		final String edithEmail = "edith@example.com";
		final Student actual2 = client.updateStudent(actual1.getUuid(),
				edithName, edithEmail);
		assertEquals(edithName, actual2.getName());
		assertEquals(edithEmail, actual2.getEmailAddress());

		client.deleteStudent(actual1.getUuid());
		try {
			client.getStudent(expected.getUuid());
			fail("should have thrown exception");
		} catch (ObjectNotFoundException e) {
			// do nothing
		}
	}
}
