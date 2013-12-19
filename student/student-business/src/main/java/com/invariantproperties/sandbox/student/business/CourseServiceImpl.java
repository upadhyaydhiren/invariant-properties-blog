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

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.student.domain.Course;
import com.invariantproperties.sandbox.student.repository.CourseRepository;

/**
 * Implementation of CourseService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class CourseServiceImpl implements CourseService {
    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Resource
    private CourseRepository courseRepository;

    /**
     * Default constructor
     */
    public CourseServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.CourseService#
     *      findAllCourses()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllCourses() {
        List<Course> courses = null;

        try {
            courses = courseRepository.findAll();
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("error loading list of courses: " + e.getMessage(), e);
            }
            throw new PersistenceException("unable to get list of courses.", e);
        }

        return courses;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.CourseService#
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
     * @see com.invariantproperties.sandbox.student.business.CourseService#
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
     * @see com.invariantproperties.sandbox.student.business.CourseService#
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
     * @see com.invariantproperties.sandbox.course.persistence.CourseService#
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
     * @see com.invariantproperties.sandbox.student.business.CourseService#
     *      deleteCourse(java.lang.String)
     */
    @Transactional
    @Override
    public void deleteCourse(String uuid) {
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
