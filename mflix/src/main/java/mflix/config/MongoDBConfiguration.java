package mflix.config;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.SslSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Configuration
@Service
public class MongoDBConfiguration {

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
  public MongoClient mongoClient(@Value("${spring.mongodb.uri}") String connectionString) {

    ConnectionString connString = new ConnectionString(connectionString);

    WriteConcern concern = WriteConcern.MAJORITY.withWTimeout(2500, TimeUnit.MILLISECONDS);

    MongoClientSettings settings = MongoClientSettings.builder()
//            .writeConcern(concern)
            .applyConnectionString(connString)
            .build();

    //TODO> Ticket: Handling Timeouts - configure the expected
    // WriteConcern `wtimeout` and `connectTimeoutMS` values
    MongoClient mongoClient = MongoClients.create(settings);

    SslSettings sslSettings = settings.getSslSettings();
    ReadPreference readPreference = settings.getReadPreference();
    ReadConcern readConcern = settings.getReadConcern();
    WriteConcern writeConcern = settings.getWriteConcern();

    System.out.println(readPreference.toString());
    System.out.println(sslSettings.isEnabled());
    System.out.println(readConcern.asDocument().toString());
    System.out.println(writeConcern.asDocument().toString());
    System.out.println(sslSettings.isInvalidHostNameAllowed());

    return mongoClient;
  }
}
