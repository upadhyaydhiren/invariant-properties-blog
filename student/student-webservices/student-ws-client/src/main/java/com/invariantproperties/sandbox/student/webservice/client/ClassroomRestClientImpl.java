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

import com.invariantproperties.sandbox.student.domain.Classroom;

/**
 * Implementation of ClassroomRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class ClassroomRestClientImpl extends AbstractRestClientImpl<Classroom>
		implements ClassroomRestClient {
	private static final Classroom[] EMPTY_CLASSROOM_ARRAY = new Classroom[0];

	/**
	 * Constructor.
	 * 
	 * @param classroomResource
	 */
	public ClassroomRestClientImpl(final String resource) {
		super(resource, Classroom.class, Classroom[].class);
	}

	/**
	 * Create JSON string.
	 * 
	 * @param name
	 * @return
	 */
	String createJson(final String name) {
		return String.format("{ \"name\": \"%s\" }", name);
	}

	/**
	 * @see com.invariantproperties.sandbox.student.webservice.client.ClassroomRestClient#getAllClassrooms()
	 */
	public Classroom[] getAllClassrooms() {
		return super.getAllObjects(EMPTY_CLASSROOM_ARRAY);
	}

	/**
	 * @see com.invariantproperties.sandbox.student.webservice.client.ClassroomRestClient#getClassroom(java.lang.String)
	 */
	public Classroom getClassroom(final String uuid) {
		return super.getObject(uuid);
	}

	/**
	 * @see com.invariantproperties.sandbox.student.webservice.client.ClassroomRestClient#createClassroom(java.lang.String)
	 */
	public Classroom createClassroom(final String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("'name' is required");
		}

		return createObject(createJson(name));
	}

	/**
	 * @see com.invariantproperties.sandbox.student.webservice.client.ClassroomRestClient#updateClassroom(java.lang.String,
	 *      java.lang.String)
	 */
	public Classroom updateClassroom(final String uuid, final String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("'name' is required");
		}

		return super.updateObject(createJson(name), uuid);
	}

	/**
	 * @see com.invariantproperties.sandbox.student.webservice.client.ClassroomRestClient#deleteClassroom(java.lang.String)
	 */
	public void deleteClassroom(final String uuid) {
		super.deleteObject(uuid);
	}
}
