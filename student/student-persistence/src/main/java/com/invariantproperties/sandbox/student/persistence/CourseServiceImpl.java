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
package com.invariantproperties.sandbox.student.persistence;

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
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Service
public class CourseServiceImpl implements CourseService {
    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Resource
    private CourseRepository courseRepository;

    /**
     * @see com.invariantproperties.sandbox.course.persistence.CourseService#
     *      findAllCourses()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Course> findAllCourses() {
        final List<Course> courses = courseRepository.findAll();
        return courses;
    }

    /**
     * @see com.invariantproperties.sandbox.course.persistence.CourseService#
     *      findCourseById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public Course findCourseById(Integer id) {
        Course course = null;
        try {
            course = courseRepository.findOne(id);
        } catch (DataAccessException e) {
            log.info("internal error retrieving course: " + id);

            return null;
        }

        if (course == null) {
            log.debug("did not find course: " + id);
            return null;
        }

        return course;
    }

    /**
     * @see com.invariantproperties.sandbox.course.persistence.CourseService#
     *      findCourseByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Course findCourseByUuid(String uuid) {
        Course course = null;
        try {
            course = courseRepository.findCourseByUuid(uuid);
        } catch (DataAccessException e) {
            log.info("internal error retrieving course: " + uuid);

            return null;
        }

        if (course == null) {
            log.debug("did not find course: " + uuid);
            return null;
        }

        return course;
    }

    /**
     * @see com.invariantproperties.sandbox.course.persistence.CourseService#
     *      createCourse(java.lang.String)
     */
    @Transactional
    @Override
    public Course createCourse(String name) {
        final Course course = new Course();
        course.setName(name);
        final Course actual = courseRepository.saveAndFlush(course);

        return actual;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.invariantproperties.sandbox.course.persistence.CourseService#
     * updateCourse(com.invariantproperties.sandbox.course.domain.Course,
     * java.lang.String)
     */
    public Course updateCourse(Course course, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.invariantproperties.sandbox.course.persistence.CourseService#
     *      deleteCourse(java.lang.String)
     */
    @Transactional
    @Override
    public void deleteCourse(String uuid) {
        Course course = null;
        try {
            course = courseRepository.findCourseByUuid(uuid);
        } catch (DataAccessException e) {
            log.info("internal error retrieving course: " + uuid);
            throw new ObjectNotFoundException("delete", uuid);
        }

        if (course == null) {
            log.debug("did not find course: " + uuid);
            throw new ObjectNotFoundException("delete", uuid);
        }

        courseRepository.delete(course);
    }
}
