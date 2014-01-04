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
package com.invariantproperties.sandbox.student.maintenance.service.impl;

import java.util.Arrays;
import java.util.List;

import com.invariantproperties.sandbox.student.business.CourseFinderService;
import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.domain.Course;
import com.invariantproperties.sandbox.student.domain.TestRun;
import com.invariantproperties.sandbox.student.webservice.client.CourseFinderRestClient;
import com.invariantproperties.sandbox.student.webservice.client.CourseManagerRestClient;
import com.invariantproperties.sandbox.student.webservice.client.impl.CourseFinderRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.client.impl.CourseManagerRestClientImpl;

public class CourseFinderServiceTapestryImpl implements CourseFinderService {
    private final CourseFinderRestClient finder;

    // private final Map<String, Course> cache = new HashMap<>();

    public CourseFinderServiceTapestryImpl() {
        // resource should be loaded as tapestry resource
        final String resource = "http://localhost:8080/student-ws-webapp/rest/course/";
        finder = new CourseFinderRestClientImpl(resource);
        initCache(new CourseManagerRestClientImpl(resource));
    }

    @Override
    public long count() {
        // FIXME: grossly inefficient but good enough for now.
        return finder.getAllCourses().length;
    }

    @Override
    public long countByTestRun(TestRun testRun) {
        // FIXME: grossly inefficient but good enough for now.
        return finder.getAllCourses().length;
    }

    @Override
    public Course findCourseById(Integer id) {
        // unsupported operation!
        throw new ObjectNotFoundException(id);
    }

    @Override
    public Course findCourseByUuid(String uuid) {
        return finder.getCourse(uuid);
    }

    @Override
    public Course findCourseByCode(String code) {
        // unsupported operation!
        throw new ObjectNotFoundException(code);
    }

    @Override
    public List<Course> findAllCourses() {
        // return new ArrayList<Course>(cache.values());
        return Arrays.asList(finder.getAllCourses());
    }

    @Override
    public List<Course> findCoursesByTestRun(TestRun testRun) {
        // return new ArrayList<Classroom>(cache.values());
        return Arrays.asList(finder.getAllCourses());
    }

    private void initCache(CourseManagerRestClient manager) {
        for (int i = 0; i < 5; i++) {
            long x = System.currentTimeMillis() % 10000;
            manager.createCourse("TST-" + x, "Physics " + x, "summary", "description", 3);
        }
    }
}