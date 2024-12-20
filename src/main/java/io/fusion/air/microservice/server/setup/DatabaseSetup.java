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
package io.fusion.air.microservice.server.setup;
// Spring
import io.fusion.air.microservice.server.config.DatabaseConfig;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
// DB
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */
@Configuration
@EntityScan("io.fusion.air.microservice.domain.*")
@EnableJpaRepositories(basePackages = { "io.fusion.air.microservice.domain.ports", "io.fusion.air.microservice.adapters.repository" })
@EnableTransactionManagement
public class DatabaseSetup {

    // Autowired using the Constructor
    private DatabaseConfig dbConfig;

    /**
     * Autowired using the Constructor
     * @param dbCfg
     */
    public DatabaseSetup(DatabaseConfig dbCfg) {
        dbConfig = dbCfg;
    }

    /**
     * Create the DataSource for H2 Database
     * @return
     */
    public DataSource dataSource() {
        switch(dbConfig.getDataSourceVendor()) {
            case DatabaseConfig.DB_H2:
                return h2DataSource();
            case DatabaseConfig.DB_POSTGRESQL:
                return postgreSQLDataSource();
            default:
                // Returns H2 Database if Nothing Matches
                EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
                return builder.setType(EmbeddedDatabaseType.H2).build();
        }
    }

    /**
     * Profile Will be active for Staging Only
     * @return
     */
    @Bean
    @Profile("dev")
    public DataSource h2DataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.H2).build();
    }

    /**
     * Profile will be active for Staging or Prod
     * @return
     */
    @Bean
    @Profile("staging | prod")
    public DataSource postgreSQLDataSource() {
        // For PostgreSQL Database
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(dbConfig.getDataSourceDriverClassName());
        config.addDataSourceProperty("serverName", dbConfig.getDataSourceServer());
        config.addDataSourceProperty("portNumber", ""+ dbConfig.getDataSourcePort());
        config.addDataSourceProperty("databaseName", dbConfig.getDataSourceName());
        config.addDataSourceProperty("user", dbConfig.getDataSourceUserName());
        config.addDataSourceProperty("password", dbConfig.getDataSourcePassword());
        config.setSchema(dbConfig.getDataSourceSchema());

        // postgress configuration for Hikari
        return new HikariDataSource(config);
    }

    /**
     * Create EntityManagerFactory
     * @return
     */
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabasePlatform(dbConfig.getDataSourceDialect());

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
