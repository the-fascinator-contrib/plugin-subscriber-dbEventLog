/*
 * The Fascinator - Solr Event Log Subscriber
 * Copyright (C) 2010-2011 University of Southern Queensland
 * Copyright (C) 2011 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
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
package com.googlecode.fascinator.subscriber.eventLog;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.fascinator.api.PluginDescription;
import com.googlecode.fascinator.api.PluginException;
import com.googlecode.fascinator.api.authentication.AuthenticationException;
import com.googlecode.fascinator.api.subscriber.Subscriber;
import com.googlecode.fascinator.api.subscriber.SubscriberException;
import com.googlecode.fascinator.common.JsonSimpleConfig;
import com.googlecode.fascinator.model.EventLog;
import com.googlecode.fascinator.service.EventLogService;
import com.googlecode.fascinator.spring.ApplicationContextProvider;

/**
 * <p>
 * This plugin implements Event log subscriber using security solr core within
 * the Fascinator.
 * 
 * <h3>Configuration</h3>
 * <p>
 * Standard configuration table:
 * </p>
 * <table border="1">
 * <tr>
 * <th>Option</th>
 * <th>Description</th>
 * <th>Required</th>
 * <th>Default</th>
 * </tr>
 * 
 * <tr>
 * <td>uri</td>
 * <td>The URI of the Solr event log service</td>
 * <td><b>Yes</b></td>
 * <td>http://localhost:9997/solr/eventlog</td>
 * </tr>
 * 
 * <tr>
 * <td>docLimit</td>
 * <td>Document Limit before commit to Solr</td>
 * <td></td>
 * <td>200</td>
 * </tr>
 * 
 * <tr>
 * <td>sizeLimit</td>
 * <td>Size Limit before commit to Solr</td>
 * <td></td>
 * <td>204800</td>
 * </tr>
 * 
 * <tr>
 * <td>timeLimit</td>
 * <td>Time Limit in minutes before commit to Solr</td>
 * <td></td>
 * <td>30</td>
 * </tr>
 * 
 * </table>
 * 
 * <h3>Examples</h3>
 * <ol>
 * <li>
 * Using Solr event subscriber plugin in The Fascinator
 * 
 * <pre>
 *      "subscriber": {
 *      "solr": {
 *          "uri": "http://localhost:9997/solr/eventlog",
 *          "buffer": {
 *              "docLimit" : "200",
 *              "sizeLimit" : "204800",
 *              "timeLimit" : "30"
 *          }
 *      }
 *  }
 * </pre>
 * 
 * </li>
 * </ol>
 * 
 * <h3>Wiki Link</h3>
 * <p>
 * None
 * </p>
 * 
 * @author Linda Octalina
 */

public class SolrEventLogSubscriber implements Subscriber {

	/** Logging */
	private static final Logger log = LoggerFactory.getLogger(SolrEventLogSubscriber.class);

	/** Solr URI */
	private URI uri;

	/** Solr Core */
	private CommonsHttpSolrServer core;

	
	private final EventLogService dbEventLogService;
	/**
	 * Gets an identifier for this type of plugin. This should be a simple name
	 * such as "file-system" for a storage plugin, for example.
	 * 
	 * @return the plugin type id
	 */
	
	public SolrEventLogSubscriber() {
		this.dbEventLogService = ApplicationContextProvider.getApplicationContext().getBean("dbEventLogService", EventLogService.class);
	}

	@Override
	public String getId() {
		return "solr-event-log";
	}

	/**
	 * Gets a name for this plugin. This should be a descriptive name.
	 * 
	 * @return the plugin name
	 */
	@Override
	public String getName() {
		return "Solr Event Log Subscriber";
	}

	/**
	 * Gets a PluginDescription object relating to this plugin.
	 * 
	 * @return a PluginDescription
	 */
	@Override
	public PluginDescription getPluginDetails() {
		return new PluginDescription(this);
	}

	/**
	 * Initializes the plugin using the specified JSON String
	 * 
	 * @param jsonString
	 *            JSON configuration string
	 * @throws PluginException
	 *             if there was an error in initialization
	 */
	@Override
	public void init(String jsonString) throws SubscriberException {
		try {
			setConfig(new JsonSimpleConfig(jsonString));
		} catch (IOException e) {
			throw new SubscriberException(e);
		}
	}

	/**
	 * Initializes the plugin using the specified JSON configuration
	 * 
	 * @param jsonFile
	 *            JSON configuration file
	 * @throws SubscriberException
	 *             if there was an error in initialization
	 */
	@Override
	public void init(File jsonFile) throws SubscriberException {
		try {
			setConfig(new JsonSimpleConfig(jsonFile));
		} catch (IOException ioe) {
			throw new SubscriberException(ioe);
		}
	}

	/**
	 * Initialization of Solr Access Control plugin
	 * 
	 * @param config
	 *            The configuration to use
	 * @throws AuthenticationException
	 *             if fails to initialize
	 */
	private void setConfig(JsonSimpleConfig config) throws SubscriberException {
		try {
			// Find our solr index
			uri = new URI(config.getString(null, "subscriber", getId(), "uri"));
			if (uri == null) {
				throw new SubscriberException("No Solr URI provided");
			}
			core = new CommonsHttpSolrServer(uri.toURL());

			// Small sleep whilst the solr index is still coming online
			Thread.sleep(200);
			int pingCount = 0;
			while (pingCount < 20) {
				// Make sure it is online
				try {
					core.ping();
					break;
				} catch (Exception e) {
					pingCount++;
					log.error("Can't connect to Solr Server. Attempt " + pingCount + "of 20", e);
					if (pingCount == 20) {
						throw e;
					}
					Thread.sleep(1000);
				}

			}
		} catch (Exception ex) {
			throw new SubscriberException(ex);
		}
	}


	/**
	 * Shuts down the plugin
	 * 
	 * @throws SubscriberException
	 *             if there was an error during shutdown
	 */
	@Override
	public void shutdown() throws SubscriberException {
		log.debug("Reached plugin shutdown...");
	}


	/**
	 * Method to fire for incoming events	
	 * 
	 * @param param
	 *            : Map of key/value pairs to add to the index
	 * @throws SubscriberException
	 *             if there was an error
	 */
	@Override
	public void onEvent(Map<String, String> param) throws SubscriberException {
		try {
			EventLog eventLog = this.dbEventLogService.persist(param);
			core.addBean(eventLog);
			log.debug("eventLog added to core solr server.");
			core.commit();
			log.info("eventLog committed to solr server.");
		} catch (Exception e) {
			throw new SubscriberException("Fail to add log to solr" + e.getMessage());
		}
	}

}
