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

import static com.invariantproperties.sandbox.student.specification.CourseSpecifications.testRunIs;

import java.util.List;

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
public class CourseFinderServiceImpl implements CourseFinderService {
    private static final Logger log = LoggerFactory.getLogger(CourseFinderServiceImpl.class);

    @Resource
    private CourseRepository courseRepository;

    /**
     * Default constructor
     */
    public CourseFinderServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    CourseFinderServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.FinderService#
     *      count()
     */
    @Transactional(readOnly = true)
    @Override
    public long count() {
        return countByTestRun(null);
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.FinderService#
     *      countByTestRun(com.invariantproperties.sandbox.student.domain.TestRun)
     */
    @Transactional(readOnly = true)
    @Override
    public long countByTestRun(TestRun testRun) {
        long count = 0;
        try {
            count = courseRepository.count(testRunIs(testRun));
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving classroom count by " + testRun, e);
            }
            throw new PersistenceException("unable to count classrooms by " + testRun, e, 0);
        }

        return count;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.CourseFinderService#
     *      findAllCourses()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllCourses() {
        return findCoursesByTestRun(null);
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.CourseFinderService#
     *      findCourseById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public Course findCourseById(Integer id) {
        Course course = null;
        try {
            course = courseRepository.findOne(id);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving course: " + id, e);
            }
            throw new PersistenceException("unable to find course by id", e, id);
        }

        if (course == null) {
            throw new ObjectNotFoundException(id);
        }

        return course;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.CourseFinderService#
     *      findCourseByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Course findCourseByUuid(String uuid) {
        Course course = null;
        try {
            course = courseRepository.findCourseByUuid(uuid);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving course: " + uuid, e);
            }
            throw new PersistenceException("unable to find course by uuid", e, uuid);
        }

        if (course == null) {
            throw new ObjectNotFoundException(uuid);
        }

        return course;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.CourseFinderService#
     *      findCoursesByTestRun(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public List<Course> findCoursesByTestRun(TestRun testRun) {
        List<Course> courses = null;

        try {
            courses = courseRepository.findAll(testRunIs(testRun));
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("error loading list of courses: " + e.getMessage(), e);
            }
            throw new PersistenceException("unable to get list of courses.", e);
        }

        return courses;
    }
}
