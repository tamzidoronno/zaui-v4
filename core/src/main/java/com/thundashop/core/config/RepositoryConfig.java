package com.thundashop.core.config;

import com.thundashop.core.pmsmanager.ConferenceData;
import com.thundashop.core.pmsmanager.PmsLogManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.db.EntityMappersImpl;
import com.thundashop.repository.pmsmanager.ConferenceDataRepository;
import com.thundashop.repository.pmsmanager.PmsLogRepository;
import com.thundashop.repository.pmsmanager.PmsPricingRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean(name = "repositoryDatabase")
    public Database database() {
        // TODO: Read from config file
        return Database.of("localhost", 27018, new EntityMappersImpl());
    }

    @Bean
    public PmsLogRepository pmsLogRepository(@Qualifier("repositoryDatabase") Database database) {
        return new PmsLogRepository(database, PmsLogManager.class.getSimpleName());
    }

    @Bean
    public ConferenceDataRepository conferenceDataRepository(@Qualifier("repositoryDatabase") Database database) {
        return new ConferenceDataRepository(database, PmsManager.class.getSimpleName() + "_default",
                ConferenceData.class.getSimpleName());
    }

    @Bean
    public PmsPricingRepository pmsPricingRepository(@Qualifier("repositoryDatabase") Database database) {
        return new PmsPricingRepository(database, PmsManager.class.getSimpleName() + "_default",
                PmsPricing.class.getName());
    }
}
