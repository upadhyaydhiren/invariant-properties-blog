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
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.student.domain.Term;
import com.invariantproperties.sandbox.student.repository.TermRepository;

public class TermServiceImpl implements TermService {
	private static final Logger log = LoggerFactory
	        .getLogger(TermServiceImpl.class);

	@Resource
	private TermRepository termRepository;

	/**
	 * Default constructor
	 */
	public TermServiceImpl() {

	}

	/**
	 * Constructor used in unit tests
	 */
	TermServiceImpl(TermRepository termRepository) {
		this.termRepository = termRepository;
	}

	/**
	 * @see com.invariantproperties.sandbox.student.business.TermService#
	 *      findAllTerms()
	 */
	@Transactional(readOnly = true)
	@Override
	public List<Term> findAllTerms() {
		List<Term> terms = null;

		try {
			terms = termRepository.findAll();
		} catch (DataAccessException e) {
			if (!(e instanceof UnitTestException)) {
				log.info("error loading list of terms: " + e.getMessage(), e);
			}
			throw new PersistenceException("unable to get list of terms.", e);
		}

		return terms;
	}

	/**
	 * @see com.invariantproperties.sandbox.student.business.TermService#
	 *      findTermById(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public Term findTermById(Integer id) {
		Term term = null;
		try {
			term = termRepository.findOne(id);
		} catch (DataAccessException e) {
			if (!(e instanceof UnitTestException)) {
				log.info("internal error retrieving term: " + id, e);
			}
			throw new PersistenceException("unable to find term by id", e, id);
		}

		if (term == null) {
			throw new ObjectNotFoundException(id);
		}

		return term;
	}

	/**
	 * @see com.invariantproperties.sandbox.student.business.TermService#
	 *      findTermByUuid(java.lang.String)
	 */
	@Transactional(readOnly = true)
	@Override
	public Term findTermByUuid(String uuid) {
		Term term = null;
		try {
			term = termRepository.findTermByUuid(uuid);
		} catch (DataAccessException e) {
			if (!(e instanceof UnitTestException)) {
				log.info("internal error retrieving term: " + uuid, e);
			}
			throw new PersistenceException("unable to find term by uuid", e,
			        uuid);
		}

		if (term == null) {
			throw new ObjectNotFoundException(uuid);
		}

		return term;
	}

	/**
	 * @see com.invariantproperties.sandbox.student.business.TermService#
	 *      createTerm(java.lang.String)
	 */
	@Transactional
	@Override
	public Term createTerm(String name) {
		final Term term = new Term();
		term.setName(name);

		Term actual = null;
		try {
			actual = termRepository.saveAndFlush(term);
		} catch (DataAccessException e) {
			if (!(e instanceof UnitTestException)) {
				log.info("internal error retrieving term: " + name, e);
			}
			throw new PersistenceException("unable to create term", e);
		}

		return actual;
	}

	/**
	 * @see com.invariantproperties.sandbox.term.persistence.TermService#
	 *      updateTerm(com.invariantproperties.sandbox.term.domain.Term,
	 *      java.lang.String)
	 */
	public Term updateTerm(Term term, String name) {
		Term updated = null;
		try {
			final Term actual = termRepository.findTermByUuid(term.getUuid());

			if (actual == null) {
				log.debug("did not find term: " + term.getUuid());
				throw new ObjectNotFoundException(term.getUuid());
			}

			actual.setName(name);
			updated = termRepository.saveAndFlush(actual);
			term.setName(name);

		} catch (DataAccessException e) {
			if (!(e instanceof UnitTestException)) {
				log.info("internal error deleting term: " + term.getUuid(), e);
			}
			throw new PersistenceException("unable to delete term", e,
			        term.getUuid());
		}

		return updated;
	}

	/**
	 * @see com.invariantproperties.sandbox.student.business.TermService#
	 *      deleteTerm(java.lang.String)
	 */
	@Transactional
	@Override
	public void deleteTerm(String uuid) {
		Term term = null;
		try {
			term = termRepository.findTermByUuid(uuid);

			if (term == null) {
				log.debug("did not find term: " + uuid);
				throw new ObjectNotFoundException(uuid);
			}
			termRepository.delete(term);

		} catch (DataAccessException e) {
			if (!(e instanceof UnitTestException)) {
				log.info("internal error deleting term: " + uuid, e);
			}
			throw new PersistenceException("unable to delete term", e, uuid);
		}
	}
}
