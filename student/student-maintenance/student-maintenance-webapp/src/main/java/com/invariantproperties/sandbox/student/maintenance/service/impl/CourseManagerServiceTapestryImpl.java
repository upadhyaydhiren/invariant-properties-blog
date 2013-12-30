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

import com.invariantproperties.sandbox.student.business.CourseManagerService;
import com.invariantproperties.sandbox.student.domain.Course;
import com.invariantproperties.sandbox.student.domain.TestRun;
import com.invariantproperties.sandbox.student.webservice.client.CourseManagerRestClient;
import com.invariantproperties.sandbox.student.webservice.client.impl.CourseManagerRestClientImpl;

/**
 * Implementation of CourseManagerService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class CourseManagerServiceTapestryImpl implements CourseManagerService {
    private final CourseManagerRestClient manager;

    public CourseManagerServiceTapestryImpl() {
        // resource should be loaded as tapestry resource
        final String resource = "http://localhost:8080/student-ws-webapp/rest/course/";
        manager = new CourseManagerRestClientImpl(resource);
    }

    /**
     * @see com.invariantproperties.sandbox.student.maintenance.service.CourseManagerService#createCourse(java.lang.String)
     */
    @Override
    public Course createCourse(String name) {
        final Course actual = manager.createCourse(name);
        return actual;
    }

    /**
     * @see com.invariantproperties.sandbox.student.maintenance.service.CourseManagerService#updateCourse(com.invariantproperties.sandbox.student.domain.Course,
     *      java.lang.String)
     */
    @Override
    public Course updateCourse(Course course, String name) {
        final Course actual = manager.updateCourse(course.getUuid(), name);
        return actual;
    }

    /**
     * @see com.invariantproperties.sandbox.student.maintenance.service.CourseManagerService#deleteCourse(java.lang.String,
     *      java.lang.Integer)
     */
    @Override
    public void deleteCourse(String uuid, Integer version) {
        manager.deleteCourse(uuid);
    }

    /**
     * @see com.invariantproperties.sandbox.student.maintenance.service.CourseManagerService#createCourse(java.lang.String,
     *      com.invariantproperties.sandbox.student.domain.TestRun)
     */
    @Override
    public Course createCourseForTesting(String name, TestRun testRun) {
        final Course actual = manager.createCourse(name);
        return actual;
    }
}
