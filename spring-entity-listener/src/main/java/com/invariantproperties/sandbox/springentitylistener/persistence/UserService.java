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
package com.invariantproperties.sandbox.springentitylistener.persistence;

import java.util.List;

import com.invariantproperties.sandbox.springentitylistener.domain.User;

/**
 * Sample service API.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public interface UserService {
    List<User> getAllUsers();

    User getUserById(Integer id);

    User getUserByUuid(String uuid);

    User getUserByEmailAddress(String emailAddress);

    User createUser(String name, String emailAddress);

    User updateUser(User user, String name, String emailAddress);

    void deleteUser(String uuid);
}