/* 
 * Copyright (C) 2013 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.googlecode.fascinator.dao.impl;

import org.springframework.stereotype.Repository;

import com.googlecode.fascinator.dao.EventLogDao;
import com.googlecode.fascinator.model.EventLog;

/**
 * @author mulhollm
 *
 */

@Repository("eventLogDao")
public class EventLogDaoImpl extends GenericDaoHibernateImpl<EventLog, Long> implements EventLogDao {

	public EventLogDaoImpl() {
		super(EventLog.class);
	}

}
