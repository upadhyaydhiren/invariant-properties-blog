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

import com.invariantproperties.sandbox.student.business.CourseService;
import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.domain.Course;

@Service
@Path("/course")
public class CourseResource extends AbstractResource {
	private static final Logger log = Logger.getLogger(CourseResource.class);
	private static final Course[] EMPTY_COURSE_ARRAY = new Course[0];

	@Context
	UriInfo uriInfo;

	@Context
	Request request;

	@Resource
	private CourseService service;

	/**
	 * Default constructor.
	 */
	public CourseResource() {

	}

	/**
	 * Unit test constructor.
	 * 
	 * @param service
	 */
	CourseResource(CourseService service) {
		this.service = service;
	}

	/**
	 * Get all Courses.
	 * 
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
	public Response findAllCourses() {
		log.debug("CourseResource: findAllCourses()");

		Response response = null;
		try {
			List<Course> courses = service.findAllCourses();

			List<Course> results = new ArrayList<Course>(courses.size());
			for (Course course : courses) {
				results.add(scrubCourse(course));
			}

			response = Response.ok(results.toArray(EMPTY_COURSE_ARRAY))
					.status(Status.OK).build();
		} catch (Exception e) {
			if (!(e instanceof UnitTestException)) {
				log.info("unhandled exception", e);
			}
			response = Response.noContent()
					.status(Status.INTERNAL_SERVER_ERROR).build();
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
	public Response createCourse(Name req) {
		log.debug("CourseResource: createCourse()");

		final String name = req.getName();
		if ((name == null) || name.isEmpty()) {
			return Response.ok().status(Status.BAD_REQUEST)
					.entity("'name' is required'").build();
		}

		Response response = null;

		try {
			Course course = service.createCourse(name);
			if (course == null) {
				response = Response.noContent()
						.status(Status.INTERNAL_SERVER_ERROR).build();
			} else {
				response = Response.created(URI.create(course.getUuid()))
						.entity(scrubCourse(course)).build();
			}
		} catch (Exception e) {
			if (!(e instanceof UnitTestException)) {
				log.info("unhandled exception", e);
			}
			response = Response.noContent()
					.status(Status.INTERNAL_SERVER_ERROR).build();
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
		log.debug("CourseResource: getCourse()");

		Response response = null;
		try {
			Course course = service.findCourseByUuid(id);
			response = Response.ok(scrubCourse(course)).status(Status.OK)
					.build();
		} catch (ObjectNotFoundException e) {
			response = Response.noContent().status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			if (!"[unit test]".equals(e.getMessage())) {
				log.info("unhandled exception", e);
			}
			response = Response.noContent()
					.status(Status.INTERNAL_SERVER_ERROR).build();
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
	public Response updateCourse(@PathParam("courseId") String id, Name req) {
		log.debug("CourseResource: updateCourse()");

		final String name = req.getName();
		if ((name == null) || name.isEmpty()) {
			return Response.ok().status(Status.BAD_REQUEST)
					.entity("'name' is required'").build();
		}

		Response response = null;
		try {
			final Course course = service.findCourseByUuid(id);
			final Course updatedCourse = service.updateCourse(course, name);
			response = Response.ok(scrubCourse(updatedCourse))
					.status(Status.OK).build();
		} catch (ObjectNotFoundException exception) {
			response = Response.noContent().status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			if (!(e instanceof UnitTestException)) {
				log.info("unhandled exception", e);
			}
			response = Response.noContent()
					.status(Status.INTERNAL_SERVER_ERROR).build();
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
	public Response deleteCourse(@PathParam("courseId") String id) {
		log.debug("CourseResource: deleteCourse()");

		Response response = null;
		try {
			service.deleteCourse(id);
			response = Response.noContent().status(Status.NO_CONTENT).build();
		} catch (ObjectNotFoundException exception) {
			response = Response.noContent().status(Status.NO_CONTENT).build();
		} catch (Exception e) {
			if (!(e instanceof UnitTestException)) {
				log.info("unhandled exception", e);
			}
			response = Response.noContent()
					.status(Status.INTERNAL_SERVER_ERROR).build();
		}

		return response;
	}
}