package com.thundashop.repository.db;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class LazyMongoClientProvider implements MongoClientProvider {

    private final Supplier<MongoClient> supplier;

    LazyMongoClientProvider(String connectionString) {
        this.supplier = Suppliers.memoize(() -> new MongoClient(new MongoClientURI(connectionString)));
    }

    @Override
    public MongoClient getMongoClient() {
        return supplier.get();
    }

    public static LazyMongoClientProviderBuilder builder() {
        return new LazyMongoClientProviderBuilder();
    }

    public static class LazyMongoClientProviderBuilder {
        private String connectionString;

        public LazyMongoClientProviderBuilder setConnectionString(Supplier<String> connectionStringSupplier) {
            this.connectionString = connectionStringSupplier.get();
            return this;
        }

        public LazyMongoClientProvider build() {
            return new LazyMongoClientProvider(connectionString);
        }
    }

}
