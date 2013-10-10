/*
 * The Fascinator - Sequence
 * Copyright (C) 2008-2010 University of Southern Queensland
 * Copyright (C) 2012 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
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
package com.googlecode.fascinator.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "EventLog")
public class EventLog implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6877027475109845751L;

	@Id
	@Column(name = "EVENT_LOG_ID", nullable = false, insertable = false, updatable = false)
	@SequenceGenerator(name = "SEQUENCE_GEN", sequenceName = "EVENT_LOG_ID_SEQ", initialValue=1, allocationSize=1)
	@GeneratedValue(strategy=GenerationType.AUTO, generator="SEQUENCE_GEN")
	private Long eventLogId;
	
	@Field
	@Column(name = "DOCUMENT_ID", nullable = false, unique = true)
	private String documentId;
	
	@Field
	@Column(name = "EVENT_TYPE")
	private String eventType;
	
	@Field
	@Column(name = "OID")
	private String oid;
	
	@Field
	@Column(name = "EVENT_TIME", columnDefinition = "date")
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime eventTime;
	
	@Field
	@Column(name = "USER_NAME")
	private String userName;
	
	@Field
	@Column(name = "CONTEXT")
	private String context;
	
	@Field
	@Column(name = "TEXT_DETAILS")
	private String textDetails;
	
	@Field
	@Column(name = "EXTRA")
	private String extra;


	
	public Long getEventLogId() {
		return eventLogId;
	}

	public void setEventLogId(Long eventLogId) {
		this.eventLogId = eventLogId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public DateTime getEventTime() {
		return eventTime;
	}

	public void setEventTime(DateTime eventTime) {
		this.eventTime = eventTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getTextDetails() {
		return textDetails;
	}

	public void setTextDetails(String textDetails) {
		this.textDetails = textDetails;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public static EventLog getInstance(Map<String, String> param) {
		return new Builder(param).build();
	}
		
	private static class Builder {
		private final Map<String, String> param;
		private final DateTime dateTime;
		
		public Builder(Map<String, String> param) {
			this.param = Collections.unmodifiableMap(param);
			this.dateTime = DateTime.parse(param.get("eventTime"));
		}
		
		private EventLog build() {	
			EventLog eventLog = new EventLog();
			eventLog.setDocumentId(param.get("id"));
			eventLog.setOid(param.get("oid"));
			eventLog.setContext(param.get("context"));
			eventLog.setEventTime(this.dateTime);
			eventLog.setUserName(param.get("user"));
			eventLog.setTextDetails(param.get("text"));
			eventLog.setEventType(param.get("eventType"));
			
			return eventLog;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EventLog)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		EventLog rhs = (EventLog)obj;
		boolean isEqual = new EqualsBuilder()
         .appendSuper(super.equals(obj))
         .append(this.eventLogId, rhs.getEventLogId())
         .append(this.documentId, rhs.getDocumentId())
         .append(this.oid, rhs.getOid())
         .isEquals();

		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int hashCode = new HashCodeBuilder(17, 37)
				.append(this.eventLogId)
				.append(this.documentId)
				.append(this.oid)
				.hashCode();
		return hashCode;
	}
}

	
