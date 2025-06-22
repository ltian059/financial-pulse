package com.fp.common.service;

import com.fp.common.properties.DynamoDbProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

import java.util.Optional;
import java.util.stream.Stream;

///
/// # Service class for DynamoDB operations
///
///
///
@RequiredArgsConstructor
@Slf4j
public class DynamoDbService {
    private final DynamoDbClient client;
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbProperties dynamoDbProperties;

    /**
     * Get the table for the specified entity class using the DynamoDB Enhanced Client.
     * @param entityClass
     * @return <T> Entity Type
     * @param <T> DynamDb table instance
     */
    public <T> DynamoDbTable<T> getTable(Class<T> entityClass) {
        TableSchema<T> tableSchema = TableSchema.fromBean(entityClass);
        return enhancedClient.table(buildTableName(entityClass), tableSchema);
    }

    /**
     * Create a DynamoDB table if it does not already exist, using default settings.
     * @param entityClass Entity class representing the table schema.
     * @return DynamoDbTable instance for the specified entity class.
     * @param <T> Entity Type
     */
    public <T> DynamoDbTable<T> createTableIfNotExists(Class<T> entityClass){
        DynamoDbTable<T> table = getTable(entityClass);
        try {
            table.createTable();
            log.info("Table created successfully");
        } catch (ResourceInUseException e) {
            log.debug("Table {} already exists, skipping creation.", table.tableName());
        }
        return table;
    }

    public <T> void putItem(T item, Class<T> entityClass) {
        DynamoDbTable<T> table = getTable(entityClass);
        table.putItem(item);
    }

    public <T> Optional<T> deleteItem(Key key, Class<T> entityClass) {
        DynamoDbTable<T> table = getTable(entityClass);
        T t = table.deleteItem(key);
        return Optional.ofNullable(t);
    }

    public <T> Stream<T> scanAllItems(Class<T> entityClass) {
        DynamoDbTable<T> table = getTable(entityClass);
        return table.scan().items().stream();
    }

    public <T> Stream<T> queryByPartitionKey(String partitionKey, Class<T> entityClass) {
        DynamoDbTable<T> table = getTable(entityClass);
        Key key = Key.builder().partitionValue(partitionKey).build();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(key);
        return table.query(queryConditional).items().stream();
    }
    public <T> Stream<T> query(Class<T> entityClass, QueryConditional queryConditional) {
        DynamoDbTable<T> table = getTable(entityClass);
        return table.query(queryConditional).items().stream();
    }

    public <T> void batchPutItems(Class<T> entityClass, Iterable<T> items) {

        DynamoDbTable<T> table = getTable(entityClass);
        WriteBatch.Builder<T> tBuilder = WriteBatch.builder(entityClass).mappedTableResource(table);

        items.forEach(tBuilder::addPutItem);


        enhancedClient.batchWriteItem(builder -> builder
                .writeBatches(tBuilder.build())
                .build()
        );

    }
    /**
     * Build the table name based on the entity class name.
     * @Example: input: TokenBlacklist.class --> fp-token-blacklist-dev
     * @param entityClass
     * @return
     */
    private String buildTableName(Class<?> entityClass){
        //1. Get the simple name of the class
        String simpleName = entityClass.getSimpleName();// e.g., TokenBlacklist
        //2. Convert to lowercase and replace camel case with hyphens
        String tableName = simpleName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();

        //3. Build the full table name using the DynamoDbProperties
        // e.g., fp-token-blacklist-dev
        return dynamoDbProperties.getFullTableName(tableName);
    }
}
