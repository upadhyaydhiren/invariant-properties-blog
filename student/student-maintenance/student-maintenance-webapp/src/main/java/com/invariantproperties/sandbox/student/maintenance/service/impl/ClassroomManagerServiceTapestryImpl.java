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

import com.invariantproperties.sandbox.student.business.ClassroomManagerService;
import com.invariantproperties.sandbox.student.domain.Classroom;
import com.invariantproperties.sandbox.student.domain.TestRun;
import com.invariantproperties.sandbox.student.webservice.client.ClassroomManagerRestClient;
import com.invariantproperties.sandbox.student.webservice.client.impl.ClassroomManagerRestClientImpl;

/**
 * Implementation of ClassroomManagerService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class ClassroomManagerServiceTapestryImpl implements ClassroomManagerService {
    private final ClassroomManagerRestClient manager;

    public ClassroomManagerServiceTapestryImpl() {
        // resource should be loaded as tapestry resource
        final String resource = "http://localhost:8080/student-ws-webapp/rest/classroom/";
        manager = new ClassroomManagerRestClientImpl(resource);
    }

    /**
     * @see com.invariantproperties.sandbox.student.maintenance.service.ClassroomManagerService#createClassroom(com.invariantproperties.sandbox.student.domain.Classroom)
     */
    @Override
    public Classroom createClassroom(String name) {
        final Classroom actual = manager.createClassroom(name);
        return actual;
    }

    /**
     * @see com.invariantproperties.sandbox.student.maintenance.service.ClassroomManagerService#updateClassroom(com.invariantproperties.sandbox.student.domain.Classroom,
     *      java.lang.String)
     */
    @Override
    public Classroom updateClassroom(Classroom classroom, String name) {
        final Classroom actual = manager.updateClassroom(classroom.getUuid(), name);
        return actual;
    }

    /**
     * @see com.invariantproperties.sandbox.student.maintenance.service.ClassroomManagerService#deleteClassroom(java.lang.String,
     *      java.lang.Integer)
     */
    @Override
    public void deleteClassroom(String uuid, Integer version) {
        manager.deleteClassroom(uuid);
    }

    /**
     * @see com.invariantproperties.sandbox.student.maintenance.service.ClassroomManagerService#createClassroom(java.lang.String,
     *      com.invariantproperties.sandbox.student.domain.TestRun)
     */
    @Override
    public Classroom createClassroomForTesting(String name, TestRun testRun) {
        final Classroom actual = manager.createClassroom(name);
        return actual;
    }
}
