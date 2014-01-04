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
package com.invariantproperties.sandbox.student.webservice.server.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.invariantproperties.sandbox.student.business.CourseFinderService;
import com.invariantproperties.sandbox.student.business.CourseManagerService;
import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.business.TestRunService;
import com.invariantproperties.sandbox.student.domain.Course;
import com.invariantproperties.sandbox.student.domain.TestRun;

@Service
@Path("/course")
public class CourseResource extends AbstractResource {
    private static final Logger LOG = Logger.getLogger(CourseResource.class);
    private static final Course[] EMPTY_COURSE_ARRAY = new Course[0];

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Resource
    private CourseFinderService finder;

    @Resource
    private CourseManagerService manager;

    @Resource
    private TestRunService testRunService;

    /**
     * Default constructor.
     */
    public CourseResource() {

    }

    /**
     * Unit test constructor.
     * 
     * @param finder
     */
    CourseResource(CourseFinderService finder, TestRunService testRunService) {
        this(finder, null, testRunService);
    }

    /**
     * Unit test constructor.
     * 
     * @param service
     */
    CourseResource(CourseManagerService manager, TestRunService testRunService) {
        this(null, manager, testRunService);
    }

    /**
     * Unit test constructor.
     * 
     * @param service
     */
    CourseResource(CourseFinderService finder, CourseManagerService manager, TestRunService testRunService) {
        this.finder = finder;
        this.manager = manager;
        this.testRunService = testRunService;
    }

    /**
     * Get all Courses.
     * 
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findAllCourses() {
        LOG.debug("CourseResource: findAllCourses()");

        Response response = null;
        try {
            List<Course> courses = finder.findAllCourses();

            List<Course> results = new ArrayList<Course>(courses.size());
            for (Course course : courses) {
                results.add(scrubCourse(course));
            }

            response = Response.ok(results.toArray(EMPTY_COURSE_ARRAY)).build();
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                LOG.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Create a Course.
     * 
     * @param req
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createCourse(CourseInfo req) {
        LOG.debug("CourseResource: createCourse()");

        final String code = req.getCode();
        if ((code == null) || code.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("'code' is required'").build();
        }

        final String name = req.getName();
        if ((name == null) || name.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("'name' is required'").build();
        }

        Response response = null;

        try {
            Course course = null;

            if (req.getTestUuid() != null) {
                TestRun testRun = testRunService.findTestRunByUuid(req.getTestUuid());
                if (testRun != null) {
                    course = manager.createCourseForTesting(code, name, req.getSummary(), req.getDescription(),
                            req.getCreditHours(), testRun);
                } else {
                    response = Response.status(Status.BAD_REQUEST).entity("unknown test UUID").build();
                }
            } else {
                course = manager.createCourse(code, name, req.getSummary(), req.getDescription(), req.getCreditHours());
            }
            if (course == null) {
                response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
            } else {
                response = Response.created(URI.create(course.getUuid())).entity(scrubCourse(course)).build();
            }
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                LOG.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Get a specific Course.
     * 
     * @param uuid
     * @return
     */
    @Path("/{courseId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getCourse(@PathParam("courseId") String id) {
        LOG.debug("CourseResource: getCourse()");

        Response response = null;
        try {
            Course course = finder.findCourseByUuid(id);
            response = Response.ok(scrubCourse(course)).build();
        } catch (ObjectNotFoundException e) {
            response = Response.status(Status.NOT_FOUND).build();
            LOG.debug("course not found: " + id);
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                LOG.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Update a Course.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param id
     * @param req
     * @return
     */
    @Path("/{courseId}")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response updateCourse(@PathParam("courseId") String id, CourseInfo req) {
        LOG.debug("CourseResource: updateCourse()");

        final String name = req.getName();
        if ((name == null) || name.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("'name' is required'").build();
        }

        Response response = null;
        try {
            final Course course = finder.findCourseByUuid(id);
            final Course updatedCourse = manager.updateCourse(course, name, req.getSummary(), req.getDescription(),
                    req.getCreditHours());
            response = Response.ok(scrubCourse(updatedCourse)).build();
        } catch (ObjectNotFoundException exception) {
            response = Response.status(Status.NOT_FOUND).build();
            LOG.debug("course not found: " + id);
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                LOG.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Delete a Course.
     * 
     * @param id
     * @return
     */
    @Path("/{courseId}")
    @DELETE
    public Response deleteCourse(@PathParam("courseId") String id, @PathParam("version") int version) {
        LOG.debug("CourseResource: deleteCourse()");

        Response response = null;
        try {
            manager.deleteCourse(id, version);
            response = Response.noContent().build();
        } catch (ObjectNotFoundException exception) {
            response = Response.noContent().build();
            LOG.info("course not found: " + id);
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                LOG.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }
}