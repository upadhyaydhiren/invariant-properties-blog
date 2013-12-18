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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.invariantproperties.sandbox.student.domain.Course;

public class DummyCourseService implements CourseService {
    private static final Logger log = Logger.getLogger(DummyCourseService.class);
    private Map<String, Course> cache = Collections.synchronizedMap(new HashMap<String, Course>());

    public List<Course> findAllCourses() {
        log.debug("CourseServer: findAllCourses()");
        return new ArrayList<Course>(cache.values());
    }

    public Course findCourseById(Integer id) {
        throw new ObjectNotFoundException(id);
    }

    public Course findCourseByUuid(String uuid) {
        log.debug("CourseServer: findCourseByUuid()");
        if (!cache.containsKey(uuid)) {
            throw new ObjectNotFoundException(uuid);
        }
        return cache.get(uuid);
    }

    public Course createCourse(String name) {
        log.debug("CourseServer: createCourse()");
        Course course = new Course();
        course.setUuid(UUID.randomUUID().toString());
        course.setName(name);
        cache.put(course.getUuid(), course);
        return course;
    }

    public Course updateCourse(Course oldCourse, String name) {
        log.debug("CourseServer: updateCourse()");
        if (!cache.containsKey(oldCourse.getUuid())) {
            throw new ObjectNotFoundException(oldCourse.getUuid());
        }

        Course course = cache.get(oldCourse.getUuid());
        course.setUuid(UUID.randomUUID().toString());
        course.setName(name);
        return course;
    }

    public void deleteCourse(String uuid) {
        log.debug("CourseServer: deleteCourse()");
        if (cache.containsKey(uuid)) {
            cache.remove(uuid);
        }
    }
}