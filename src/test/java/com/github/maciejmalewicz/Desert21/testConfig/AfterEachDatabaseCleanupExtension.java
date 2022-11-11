package com.github.maciejmalewicz.Desert21.testConfig;

import com.github.maciejmalewicz.Desert21.domain.accountManagement.AccountAcceptanceRequest;
import com.github.maciejmalewicz.Desert21.domain.accountManagement.PasswordResetLink;
import com.github.maciejmalewicz.Desert21.domain.games.Game;
import com.github.maciejmalewicz.Desert21.domain.users.ApplicationUser;
import com.github.maciejmalewicz.Desert21.domain.users.FriendsInvitation;
import com.github.maciejmalewicz.Desert21.domain.users.GameInvitation;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
public class AfterEachDatabaseCleanupExtension implements AfterEachCallback {

    private final static String CONNECTION_STRING_ADDRESS = "mongodb://localhost:27017/test";

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var connectionString = new ConnectionString(CONNECTION_STRING_ADDRESS);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        var mongoClient = MongoClients.create(mongoClientSettings);
        var mongoTemplate = new MongoTemplate(mongoClient, "test");
        mongoTemplate.dropCollection(AccountAcceptanceRequest.class);
        mongoTemplate.dropCollection(Game.class);
        mongoTemplate.dropCollection(ApplicationUser.class);
        mongoTemplate.dropCollection(PasswordResetLink.class);
        mongoTemplate.dropCollection(FriendsInvitation.class);
        mongoTemplate.dropCollection(GameInvitation.class);
    }
}