/**
 * (C) Copyright 2022 Araf Karsh Hamid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fusion.air.microservice.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Service Configuration
 *
 * @author arafkarsh
 *
 */
@Component
@Configuration
@PropertySource(
		name = "databaseConfig",
		// Expects file in the directory the jar is executed
		value = "file:./application.properties")
		// Expects the file in src/main/resources folder
		// value = "classpath:application.properties")
		// value = "classpath:application2.properties,file:./application.properties")
public class DatabaseConfig implements Serializable {

	public static final String DB_H2 		= "H2";
	public static final String DB_POSTGRESQL = "PostgreSQL";
	public static final String DB_MYSQL 		= "MySQL";
	public static final String DB_ORACLE 	= "Oracle";

	// Database Configurations
	@Value("${db.server:localhost}")
	private String dataSourceServer;

	@Value("${db.port:5432}")
	private int dataSourcePort;

	@Value("${db.name:demo}")
	private String dataSourceName;

	@Value("${db.schema:demo}")
	private String dataSourceSchema;

	@Value("${db.vendor:H2}")
	private String dataSourceVendor;

	@Value("${spring.datasource.url:jdbc:h2:mem:demo;DB_CLOSE_ON_EXIT=FALSE}")
	private String dataSourceURL;

	@Value("${spring.datasource.driverClassName:org.h2.Driver}")
	private String dataSourceDriverClassName;

	@Value("${spring.datasource.username:sa}")
	private String dataSourceUserName;

	@Value("${spring.datasource.password:password}")
	private String dataSourcePassword;

	@Value("${spring.jpa.database-platform:org.hibernate.dialect.H2Dialect}")
	private String dataSourceDialect;

	/**
	 * Returns Database URL
	 * @return
	 */
	public String getDataSourceURL() {
		return dataSourceURL;
	}

	/**
	 * Returns Driver ClassNames
	 * @return
	 */
	public String getDataSourceDriverClassName() {
		return dataSourceDriverClassName;
	}

	/**
	 * Returns Database User Name
	 * @return
	 */
	public String getDataSourceUserName() {
		return dataSourceUserName;
	}

	/**
	 * Returns Database Password
	 * @return
	 */
	public String getDataSourcePassword() {
		return dataSourcePassword;
	}

	/***
	 * Returns Dialect
	 * @return
	 */
	public String getDataSourceDialect() {
		return dataSourceDialect;
	}

	/**
	 * DataSource Server
	 * @return
	 */
	public String getDataSourceServer() {
		return dataSourceServer;
	}

	/**
	 * DataSource Port
	 * @return
	 */
	public int getDataSourcePort() {
		return dataSourcePort;
	}

	/**
	 * DataSource DB Name
	 * @return
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * Returns DB Schema Name
	 * @return
	 */
	public String getDataSourceSchema() {
		return dataSourceSchema;
	}

	/**
	 * Returns the Data Source Vendor (Ex. H2, PostgreSQL)
	 * @return
	 */
	public String getDataSourceVendor() {
		return dataSourceVendor;
	}
}
