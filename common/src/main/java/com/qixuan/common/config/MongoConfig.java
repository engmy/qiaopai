package com.qixuan.common.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.qixuan.common.utils.CamelCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration
{
    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.uri}")
    private String mongoConnectionUri;

    @Override
    protected String getDatabaseName()
    {
        return databaseName;
    }

    @Override
    public MongoClient mongoClient()
    {
        final ConnectionString connectionString = new ConnectionString(mongoConnectionUri);
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    // 开启事务
    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    // 转换器
    @Bean
    @Override
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory databaseFactory, MongoCustomConversions customConversions, MongoMappingContext mappingContext)
    {
        MappingMongoConverter mmc = super.mappingMongoConverter(databaseFactory, customConversions, mappingContext);

        // 去掉_class
        mmc.setTypeMapper(defaultMongoTypeMapper());

        // 驼峰替换
        mappingContext.setFieldNamingStrategy(setFieldNamingStrategy());
        return mmc;
    }

    @Bean
    public MongoTypeMapper defaultMongoTypeMapper()
    {
        return new DefaultMongoTypeMapper(null);
    }

    @Bean
    public FieldNamingStrategy setFieldNamingStrategy()
    {
        return new CamelCase();
    }
}