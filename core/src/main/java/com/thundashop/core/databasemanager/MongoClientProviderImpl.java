package com.thundashop.core.databasemanager;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.function.Function;

public class MongoClientProviderImpl implements MongoClientProvider {

    private final MongoClient mongo;

    private MongoClientProviderImpl(String host, int port, MongoClientOptions options) throws UnknownHostException {
        this.mongo = new MongoClient(new ServerAddress(host, port), options);
    }

    public static MongoClientProviderBuilder builder() {
        return new MongoClientProviderBuilder();
    }

    @Override
    public MongoClient getMongoClient() {
        return mongo;
    }

    public static class MongoClientProviderBuilder {
        private String host;
        private int port;
        private MongoClientOptions options;

        public MongoClientProviderBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public MongoClientProviderBuilder setPort(int port) {
            this.port = port;
            return this;
        }

        public MongoClientProviderBuilder setOptions(Function<MongoClientOptions.Builder, MongoClientOptions> optionsBuilder) {
            this.options = optionsBuilder.apply(MongoClientOptions.builder());
            return this;
        }

        public MongoClientProviderImpl build() throws UnknownHostException {
            if (this.options == null) {
                this.options = MongoClientOptions.builder().build();
            }
            return new MongoClientProviderImpl(host, port, options);
        }
    }
}