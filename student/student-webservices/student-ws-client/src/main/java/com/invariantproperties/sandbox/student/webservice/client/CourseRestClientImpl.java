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

import com.invariantproperties.sandbox.student.domain.Course;
import com.invariantproperties.sandbox.student.domain.TestRun;

/**
 * Implementation of CourseRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class CourseRestClientImpl extends AbstractRestClientImpl<Course> implements CourseRestClient {
    private static final Course[] EMPTY_COURSE_ARRAY = new Course[0];

    /**
     * Constructor.
     * 
     * @param courseResource
     */
    public CourseRestClientImpl(final String resource) {
        super(resource, Course.class, Course[].class);
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
     * Create JSON string.
     * 
     * @param name
     * @return
     */
    String createJson(final String name, final TestRun testRun) {
        return String.format("{ \"name\": \"%s\", \"testUuid\": \"%s\" }", name, testRun.getUuid());
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.CourseRestClient#getAllCourses()
     */
    @Override
    public Course[] getAllCourses() {
        return super.getAllObjects(EMPTY_COURSE_ARRAY);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.CourseRestClient#getCourse(java.lang.String)
     */
    @Override
    public Course getCourse(final String uuid) {
        return super.getObject(uuid);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.CourseRestClient#createCourse(java.lang.String)
     */
    @Override
    public Course createCourse(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        return createObject(createJson(name));
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.CourseRestClient#createCourseForTesting(java.lang.String,
     *      com.invariantproperties.sandbox.student.common.TestRun)
     */
    @Override
    public Course createCourseForTesting(final String name, final TestRun testRun) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        if (testRun == null || testRun.getUuid() == null || testRun.getUuid().isEmpty()) {
            throw new IllegalArgumentException("'testRun' is required");
        }

        return createObject(createJson(name, testRun));
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.CourseRestClient#updateCourse(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public Course updateCourse(final String uuid, final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        return super.updateObject(createJson(name), uuid);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.CourseRestClient#deleteCourse(java.lang.String)
     */
    @Override
    public void deleteCourse(final String uuid) {
        super.deleteObject(uuid);
    }
}
