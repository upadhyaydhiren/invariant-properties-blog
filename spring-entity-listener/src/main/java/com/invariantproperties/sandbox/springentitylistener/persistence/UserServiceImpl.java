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

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.springentitylistener.domain.User;
import com.invariantproperties.sandbox.springentitylistener.repository.UserRepository;

/**
 * Implementation of UserService.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Component
public class UserServiceImpl implements UserService {
    private static final Logger log = Logger.getLogger(UserServiceImpl.class);

    @Resource
    private UserRepository userRepository;

    /**
     * Default constructor.
     */
    public UserServiceImpl() {

    }

    /**
     * Constructor used during testing.
     * 
     * @param userRepository
     */
    protected UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @see com.cybersource.globalservices.vaa.service.UserService#getAllUsers()
     */
    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers() {
        final List<User> users = userRepository.findAll();

        return users;
    }

    /**
     */
    @Transactional(readOnly = true)
    @Override
    public User getUserById(Integer id) {
        User user = null;
        try {
            user = userRepository.findOne(id);
        } catch (DataAccessException e) {
            log.info("internal error retrieving user: " + id);
            return null;
        }

        if (user == null) {
            log.info("did not find user: " + id);
            return null;
        }

        return user;
    }

    /**
     */
    @Transactional(readOnly = true)
    @Override
    public User getUserByUuid(String uuid) {
        User user = null;
        try {
            user = userRepository.findUserByUuid(uuid);
        } catch (DataAccessException e) {
            log.info("internal error retrieving user: " + uuid);
            return null;
        }

        if (user == null) {
            log.info("did not find user: " + uuid);
            return null;
        }

        return user;
    }

    /**
     */
    @Transactional(readOnly = true)
    @Override
    public User getUserByEmailAddress(String emailAddress) {
        User user = null;
        try {
            user = userRepository.findUserByEmailAddress(emailAddress);
        } catch (DataAccessException e) {
            log.info("internal error retrieving user: " + emailAddress);
            return null;
        }

        if (user == null) {
            log.info("did not find user: " + emailAddress);
            return null;
        }

        return user;
    }

    /**
     */
    @Transactional
    @Override
    public User createUser(String name, String emailAddress) {
        final User user = new User();
        user.setName(name);
        user.setEmailAddress(emailAddress);
        final User actual = userRepository.saveAndFlush(user);

        return actual;
    }

    /**
     */
    @Transactional(rollbackFor = ObjectNotFoundException.class)
    @Override
    public User updateUser(User user, String name, String emailAddress) {
        User existingUser = null;
        try {
            existingUser = userRepository.findUserByUuid(user.getUuid());
        } catch (DataAccessException e) {
            log.info("internal error retrieving user: " + user.getUuid());
            throw new ObjectNotFoundException("update", user.getUuid());
        }

        if (existingUser == null) {
            log.info("unable to find user " + user.getUuid() + " for update");
            throw new ObjectNotFoundException("update", user.getUuid());
        }

        // we only allow a few fields to be set
        existingUser.setName(name);
        existingUser.setEmailAddress(emailAddress);

        // (also update original object in case caller doesn't use returned
        // value)
        user.setName(name);
        user.setEmailAddress(emailAddress);

        // update record
        final User newUser = userRepository.saveAndFlush(existingUser);

        return newUser;
    }

    /**
     */
    @Transactional(rollbackFor = ObjectNotFoundException.class)
    @Override
    public void deleteUser(String uuid) {
        User user = null;
        try {
            user = userRepository.findUserByUuid(uuid);
        } catch (DataAccessException e) {
            log.info("internal error retrieving user: " + uuid);
            throw new ObjectNotFoundException("update", uuid);
        }

        if (user == null) {
            log.info("unable to find user " + uuid + " for update");
            throw new ObjectNotFoundException("update", uuid);
        }

        userRepository.delete(user);
    }
}
