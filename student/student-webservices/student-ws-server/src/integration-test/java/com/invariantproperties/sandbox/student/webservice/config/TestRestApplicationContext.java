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
package com.invariantproperties.sandbox.student.webservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.invariantproperties.sandbox.student.business.ClassroomFinderService;
import com.invariantproperties.sandbox.student.business.CourseFinderService;
import com.invariantproperties.sandbox.student.business.DummyClassroomService;
import com.invariantproperties.sandbox.student.business.DummyCourseService;
import com.invariantproperties.sandbox.student.business.DummyInstructorService;
import com.invariantproperties.sandbox.student.business.DummySectionService;
import com.invariantproperties.sandbox.student.business.DummyStudentService;
import com.invariantproperties.sandbox.student.business.DummyTermService;
import com.invariantproperties.sandbox.student.business.DummyTestRunService;
import com.invariantproperties.sandbox.student.business.InstructorFinderService;
import com.invariantproperties.sandbox.student.business.SectionFinderService;
import com.invariantproperties.sandbox.student.business.StudentFinderService;
import com.invariantproperties.sandbox.student.business.TermFinderService;
import com.invariantproperties.sandbox.student.business.TestRunService;

@Configuration
// @Profile("test")
public class TestRestApplicationContext {

    private DummyClassroomService classroomService = new DummyClassroomService();
    private DummyCourseService courseService = new DummyCourseService();
    private DummyInstructorService instructorService = new DummyInstructorService();
    private DummySectionService sectionService = new DummySectionService();
    private DummyStudentService studentService = new DummyStudentService();
    private DummyTermService termService = new DummyTermService();
    private DummyTestRunService testRunService = new DummyTestRunService();

    @Bean
    String resourceBase() {
        return "http://localhost:18080/rest/";
    }

    @Bean
    ClassroomFinderService classroomFinderService() {
        return classroomService;
    }

    // @Bean
    // ClassroomManagerService classroomManagerService() {
    // return classroomService;
    // }

    @Bean
    CourseFinderService courseFinderService() {
        return courseService;
    }

    // @Bean
    // CourseManagerService courseManagerService() {
    // return courseService;
    // }

    @Bean
    InstructorFinderService instructorFinderService() {
        return instructorService;
    }

    // @Bean
    // InstructorManagerService instructorManagerService() {
    // return instructorService;
    // }

    @Bean
    SectionFinderService sectionFinderService() {
        return sectionService;
    }

    // @Bean
    // SectionManagerService sectionManagerService() {
    // return sectionService;
    // }

    @Bean
    StudentFinderService studentFinderService() {
        return studentService;
    }

    // @Bean
    // StudentManagerService studentManagerService() {
    // return studentService;
    // }

    @Bean
    TermFinderService termFinderService() {
        return termService;
    }

    // @Bean
    // TermManagerService termManagerService() {
    // return termService;
    // }

    @Bean
    TestRunService testRunService() {
        return testRunService;
    }
}
