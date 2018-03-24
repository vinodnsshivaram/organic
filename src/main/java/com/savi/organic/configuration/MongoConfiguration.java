package com.savi.organic.configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Override
    @Bean
    public MongoClient mongoClient() {
        if (mongoUri == null)
            throw new RuntimeException("Mongo Uri Cannot be empty");

        MongoClientURI clientURI = new MongoClientURI(mongoUri);
        return new MongoClient(clientURI);
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
