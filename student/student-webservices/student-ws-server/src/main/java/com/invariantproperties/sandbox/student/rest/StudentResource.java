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
package com.invariantproperties.sandbox.student.rest;

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

import com.invariantproperties.sandbox.student.domain.Student;
import com.invariantproperties.sandbox.student.persistence.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.persistence.StudentService;

@Service
@Path("/student")
public class StudentResource extends AbstractResource {
    private static final Logger log = Logger.getLogger(StudentResource.class.getName());
    private static final Student[] EMPTY_STUDENT_ARRAY = new Student[0];

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Resource
    private StudentService dao;

    /**
     * Get all students.
     * 
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findAllStudents() {
        List<Student> students = dao.findAllStudents();

        List<Student> results = new ArrayList<Student>(students.size());
        for (Student student : students) {
            results.add(scrubStudent(student));
        }

        return Response.ok(results.toArray(EMPTY_STUDENT_ARRAY)).status(Status.OK).build();
    }

    /**
     * Create a student.
     * 
     * @param req
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createStudent(NameAndEmailAddress req) {
        final String name = req.getName();
        final String emailAddress = req.getEmailAddress();
        if ((name == null) || name.isEmpty() || (emailAddress == null) || emailAddress.isEmpty()) {
            return Response.ok().status(Status.BAD_REQUEST).entity("'name' and 'emailAddress' are required'").build();
        }

        final Student student = dao.createStudent(name, emailAddress);
        if (student == null) {
            return Response.noContent().status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.created(URI.create(student.getUuid())).entity(scrubStudent(student)).build();
    }

    /**
     * Get a specific student.
     * 
     * @param uuid
     * @return
     */
    @Path("/{studentId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getStudent(@PathParam("studentId") String id) {
        final Student student = dao.findStudentByUUID(id);
        if (student == null) {
            return Response.noContent().status(Status.NOT_FOUND).build();
        }

        return Response.ok(scrubStudent(student)).status(Status.OK).build();
    }

    /**
     * Update a student.
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
        Student student = null;

        try {
            student = dao.findStudentByUUID(id);
        } catch (ObjectNotFoundException exception) {
            return Response.noContent().status(Status.NOT_FOUND).build();
        }

        if (student == null) {
            return Response.noContent().status(Status.NOT_FOUND).build();
        }

        final String name = req.getName();
        final String emailAddress = req.getEmailAddress();
        if ((name == null) || name.isEmpty() || (emailAddress == null) || emailAddress.isEmpty()) {
            return Response.ok().status(Status.NOT_MODIFIED).entity("'name' and 'emailAddress' are required'").build();
        }

        final Student updatedStudent = dao.updateStudent(student, name, emailAddress);

        return Response.ok(scrubStudent(updatedStudent)).status(Status.OK).build();
    }

    /**
     * Delete a student.
     * 
     * @param id
     * @return
     */
    @Path("/{studentId}")
    @DELETE
    public Response deleteStudent(@PathParam("studentId") String id) {
        try {
            dao.deleteStudent(id);
        } catch (ObjectNotFoundException exception) {
            return Response.noContent().status(Status.NOT_FOUND).build();
        }

        return Response.noContent().status(Status.GONE).build();
    }
}