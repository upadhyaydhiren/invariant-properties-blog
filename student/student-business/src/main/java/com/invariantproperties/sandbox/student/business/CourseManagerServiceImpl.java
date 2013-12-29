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

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.student.domain.Course;
import com.invariantproperties.sandbox.student.domain.TestRun;
import com.invariantproperties.sandbox.student.repository.CourseRepository;

/**
 * Implementation of CourseService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class CourseManagerServiceImpl implements CourseManagerService {
    private static final Logger log = LoggerFactory.getLogger(CourseManagerServiceImpl.class);

    @Resource
    private CourseRepository courseRepository;

    /**
     * Default constructor
     */
    public CourseManagerServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    CourseManagerServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.CourseFinderService#
     *      createCourse(java.lang.String)
     */
    @Transactional
    @Override
    public Course createCourse(String name) {
        final Course course = new Course();
        course.setName(name);

        Course actual = null;
        try {
            actual = courseRepository.saveAndFlush(course);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving course: " + name, e);
            }
            throw new PersistenceException("unable to create course", e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.CourseFinderService#
     *      createCourseForTesting(java.lang.String,
     *      com.invariantproperties.sandbox.student.common.TestRun)
     */
    @Transactional
    @Override
    public Course createCourseForTesting(String name, TestRun testRun) {
        final Course course = new Course();
        course.setName(name);
        course.setTestRun(testRun);

        Course actual = null;
        try {
            actual = courseRepository.saveAndFlush(course);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving course: " + name, e);
            }
            throw new PersistenceException("unable to create course", e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.sandbox.CourseFinderService.persistence.CourseService#
     *      updateCourse(com.invariantproperties.sandbox.course.domain.Course,
     *      java.lang.String)
     */
    @Transactional
    @Override
    public Course updateCourse(Course course, String name) {
        Course updated = null;
        try {
            final Course actual = courseRepository.findCourseByUuid(course.getUuid());

            if (actual == null) {
                log.debug("did not find course: " + course.getUuid());
                throw new ObjectNotFoundException(course.getUuid());
            }

            actual.setName(name);
            updated = courseRepository.saveAndFlush(actual);
            course.setName(name);

        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error deleting course: " + course.getUuid(), e);
            }
            throw new PersistenceException("unable to delete course", e, course.getUuid());
        }

        return updated;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.CourseFinderService#
     *      deleteCourse(java.lang.String, java.lang.Integer)
     */
    @Transactional
    @Override
    public void deleteCourse(String uuid, Integer version) {
        Course course = null;
        try {
            course = courseRepository.findCourseByUuid(uuid);

            if (course == null) {
                log.debug("did not find course: " + uuid);
                throw new ObjectNotFoundException(uuid);
            }
            courseRepository.delete(course);

        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error deleting course: " + uuid, e);
            }
            throw new PersistenceException("unable to delete course", e, uuid);
        }
    }
}
