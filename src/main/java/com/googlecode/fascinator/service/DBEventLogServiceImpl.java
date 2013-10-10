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
 * 
 * @author mulhollm
 * 
 */
package com.googlecode.fascinator.service;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.googlecode.fascinator.dao.EventLogDao;
import com.googlecode.fascinator.model.EventLog;


@Service("dbEventLogService")
public class DBEventLogServiceImpl implements EventLogService {

	private final Logger LOG = LoggerFactory.getLogger(EventLogService.class);

	@Resource
	private EventLogDao eventLogDao;

	/* (non-Javadoc)
	 * @see com.googlecode.fascinator.service.EventLogService#persist(java.util.Map)
	 */
	public void persist(Map<String, String> param) {
		EventLog eventLog = EventLog.getInstance(param);

		Long eventLogPK = this.eventLogDao.create(eventLog);
		LOG.debug("event log peristed with id: " + eventLogPK);
	}

}
