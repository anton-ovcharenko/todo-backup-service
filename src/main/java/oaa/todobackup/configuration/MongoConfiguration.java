package oaa.todobackup.configuration;

import com.mongodb.MongoClient;
import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;

@Configuration
public class MongoConfiguration {
    @Value("${embedded.mongodb.host}")
    private String MONGO_DB_HOST;
    @Value("${embedded.mongodb.port}")
    private int MONGO_DB_PORT;
    @Value("${embedded.mongodb.dbname}")
    private String MONGO_DB_DBNAME;
    @Value("${embedded.mongodb.version}")
    private String MONGO_DB_VERSION;

    @Bean
    public MongoTemplate mongoTemplate() throws IOException {
        EmbeddedMongoFactoryBean mongo = new EmbeddedMongoFactoryBean();
        mongo.setBindIp(MONGO_DB_HOST);
        mongo.setPort(MONGO_DB_PORT);
        mongo.setVersion(MONGO_DB_VERSION);
        MongoClient mongoClient = mongo.getObject();
        return new MongoTemplate(mongoClient, MONGO_DB_DBNAME);
    }
}
