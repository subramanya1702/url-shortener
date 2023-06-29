package com.design.urlshortener.it;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

@SpringBootTest
@ActiveProfiles("it")
@AutoConfigureMockMvc
@SpringJUnitConfig
public abstract class IntegrationTest {

    static {
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.INFO);

        setUpRedisContainer();
        setUpMongoContainer();
    }

    private static void setUpRedisContainer() {
        GenericContainer<?> redis = new GenericContainer<>(
                DockerImageName.parse("redis:5.0.3-alpine")
        ).withExposedPorts(6379);
        redis.start();

        System.setProperty("spring.redis.host", redis.getHost());
        System.setProperty("spring.redis.port", redis.getMappedPort(6379).toString());
    }

    private static void setUpMongoContainer() {
        MongoDBContainer mongoDb = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
                .withExposedPorts(27017);
        mongoDb.start();

        try {
            mongoDb.execInContainer("sh", "-c", "mongo admin --eval 'db.createUser(" +
                    "{" +
                    "user: \"test\"," +
                    "pwd: \"1234\"," +
                    "roles: [ { role: \"userAdminAnyDatabase\", db: \"admin\" } ]" +
                    "}" +
                    ")'");
            mongoDb.execInContainer("sh", "-c", "mongo url_shortener --eval " + getInitialData());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.setProperty("spring.data.mongodb.host", mongoDb.getHost());
        System.setProperty("spring.data.mongodb.port", mongoDb.getMappedPort(27017).toString());
    }

    private static String getInitialData() {
        return "'db.createCollection(\"shortUrl\"); db.shortUrl.insertOne(" +
                "{_id : NumberLong(\"1\")," +
                "shortUrlId : \"abcdefghi\"," +
                "longUrl : \"https://some_domain.com/somePath\"," +
                "userId : \"somename@domain.com\"," +
                "_class : \"com.design.urlshortener.model.ShortUrl\"})'";
    }

}
