package com.thundashop.core.config;

import com.thundashop.repository.db.Database;
import com.thundashop.core.pmsmanager.PmsLogManager;
import com.thundashop.repository.pmsmanager.PmsLogRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean(name = "repositoryDatabase")
    public Database database() {
        // TODO: Read from config file
        return Database.of("localhost", 27018);
    }

    @Bean
    public PmsLogRepository pmsLogRepository(@Qualifier("repositoryDatabase") Database database) {
        return new PmsLogRepository(database, PmsLogManager.class.getSimpleName());
    }
}
