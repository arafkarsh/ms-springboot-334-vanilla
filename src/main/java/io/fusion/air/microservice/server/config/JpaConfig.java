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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Configuration
@EntityScan("io.fusion.air.microservice.domain.*")
@EnableJpaRepositories(basePackages = { "io.fusion.air.microservice.domain.ports", "io.fusion.air.microservice.adapters.repository" })
@EnableTransactionManagement
public class JpaConfig {

    @Autowired
    private ServiceConfiguration serviceConfig;

    /**
     * Create the DataSource for H2 Database
     * @return
     */
    @Bean
    public DataSource dataSource() {
        switch(serviceConfig.getDataSourceVendor()) {
            case ServiceConfiguration.DB_H2:
                // For H2 Database
                EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
                return builder.setType(EmbeddedDatabaseType.H2).build();
            case ServiceConfiguration.DB_POSTGRESQL:
                // For PostgreSQL Database
                HikariConfig config = new HikariConfig();

                config.setDataSourceClassName(serviceConfig.getDataSourceDriverClassName());
                config.addDataSourceProperty("serverName", serviceConfig.getDataSourceServer());
                config.addDataSourceProperty("portNumber", ""+serviceConfig.getDataSourcePort());
                config.addDataSourceProperty("databaseName", serviceConfig.getDataSourceName());
                config.addDataSourceProperty("user", serviceConfig.getDataSourceUserName());
                config.addDataSourceProperty("password", serviceConfig.getDataSourcePassword());
                config.setSchema(serviceConfig.getDataSourceSchema());

                // postgress configuration for Hikari
                return new HikariDataSource(config);
        }
        // Returns H2 Database if Nothing Matches
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.H2).build();
    }

    /**
     * Create EntityManagerFactory
     * @return
     */
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        // vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
        vendorAdapter.setDatabasePlatform(serviceConfig.getDataSourceDialect());

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        String[] pkgs = {"io.fusion.air.microservice.domain.*"};
        factory.setPackagesToScan(pkgs);
        // Set Database Source
        factory.setDataSource(dataSource());
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    /**
     * Create PlatformTransactionManager
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory());
        return txManager;
    }

}
