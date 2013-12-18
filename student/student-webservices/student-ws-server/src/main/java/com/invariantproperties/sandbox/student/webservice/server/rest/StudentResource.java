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

import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.business.StudentService;
import com.invariantproperties.sandbox.student.domain.Student;

@Service
@Path("/student")
public class StudentResource extends AbstractResource {
    private static final Logger log = Logger.getLogger(StudentResource.class);
    private static final Student[] EMPTY_STUDENT_ARRAY = new Student[0];

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Resource
    private StudentService service;

    /**
     * Default constructor.
     */
    public StudentResource() {

    }

    /**
     * Unit test constructor.
     * 
     * @param service
     */
    StudentResource(StudentService service) {
        this.service = service;
    }

    /**
     * Get all Students.
     * 
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findAllStudents() {
        log.debug("StudentResource: findAllStudents()");

        Response response = null;
        try {
            List<Student> students = service.findAllStudents();

            List<Student> results = new ArrayList<Student>(students.size());
            for (Student student : students) {
                results.add(scrubStudent(student));
            }

            response = Response.ok(results.toArray(EMPTY_STUDENT_ARRAY)).build();
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                log.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Create a Student.
     * 
     * @param req
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createStudent(NameAndEmailAddress req) {
        log.debug("StudentResource: createStudent()");

        final String name = req.getName();
        if ((name == null) || name.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("'name' is required'").build();
        }

        final String email = req.getEmailAddress();
        if ((email == null) || email.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("'email' is required'").build();
        }

        Response response = null;

        try {
            Student student = service.createStudent(name, email);
            if (student == null) {
                response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
            } else {
                response = Response.created(URI.create(student.getUuid())).entity(scrubStudent(student)).build();
            }
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                log.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Get a specific Student.
     * 
     * @param uuid
     * @return
     */
    @Path("/{studentId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getStudent(@PathParam("studentId") String id) {
        log.debug("StudentResource: getStudent()");

        Response response = null;
        try {
            Student student = service.findStudentByUuid(id);
            response = Response.ok(scrubStudent(student)).build();
        } catch (ObjectNotFoundException e) {
            response = Response.status(Status.NOT_FOUND).build();
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                log.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Update a Student.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param id
     * @param req
     * @return
     */
    @Path("/{studentId}")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response updateStudent(@PathParam("studentId") String id, NameAndEmailAddress req) {
        log.debug("StudentResource: updateStudent()");

        final String name = req.getName();
        if ((name == null) || name.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("'name' is required'").build();
        }

        final String email = req.getEmailAddress();
        if ((email == null) || email.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("'email' is required'").build();
        }

        Response response = null;
        try {
            final Student student = service.findStudentByUuid(id);
            final Student updatedStudent = service.updateStudent(student, name, email);
            response = Response.ok(scrubStudent(updatedStudent)).build();
        } catch (ObjectNotFoundException exception) {
            response = Response.status(Status.NOT_FOUND).build();
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                log.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Delete a Student.
     * 
     * @param id
     * @return
     */
    @Path("/{studentId}")
    @DELETE
    public Response deleteStudent(@PathParam("studentId") String id) {
        log.debug("StudentResource: deleteStudent()");

        Response response = null;
        try {
            service.deleteStudent(id);
            response = Response.noContent().build();
        } catch (ObjectNotFoundException exception) {
            response = Response.noContent().build();
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                log.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }
}