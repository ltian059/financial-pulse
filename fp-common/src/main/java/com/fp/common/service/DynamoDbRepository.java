package com.fp.common.service;

import com.fp.common.properties.DynamoDbProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.stream.Stream;

/// # Service class for DynamoDB operations
///
@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ConditionalOnBean({DynamoDbClient.class, DynamoDbEnhancedClient.class, DynamoDbProperties.class})
public abstract class DynamoDbRepository<T> {
    @Autowired
    protected  DynamoDbClient client;
    @Autowired
    protected  DynamoDbEnhancedClient enhancedClient;
    @Autowired
    protected  DynamoDbProperties dynamoDbProperties;


    protected Class<T> entityClass;
    protected DynamoDbTable<T> table;

    @PostConstruct
    @SuppressWarnings("unchecked")
    private void init() {
        //Get generic type by reflection
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.table = initializeTable();
    }

    private DynamoDbTable<T> initializeTable() {
        BeanTableSchema<T> tBeanTableSchema = TableSchema.fromBean(entityClass);
        String tableName = buildTableName(entityClass);
        return enhancedClient.table(tableName, tBeanTableSchema);
    }

    /**
     * Get the table for the specified entity class using the DynamoDB Enhanced Client.
     *
     * @param entityClass
     * @param <T>         DynamDb table instance
     * @return <T> Entity Type
     */
    public <T> DynamoDbTable<T> getTable(Class<T> entityClass) {
        TableSchema<T> tableSchema = TableSchema.fromBean(entityClass);
        return enhancedClient.table(buildTableName(entityClass), tableSchema);
    }

    /**
     * Create a DynamoDB table if it does not already exist, using default settings.
     *
     * @param entityClass Entity class representing the table schema.
     * @param <T>         Entity Type
     * @return DynamoDbTable instance for the specified entity class.
     */
    public <T> DynamoDbTable<T> createTableIfNotExists(Class<T> entityClass) {
        DynamoDbTable<T> table = getTable(entityClass);
        try {
            table.createTable();
            log.info("Table created successfully");
        } catch (ResourceInUseException e) {
            log.debug("Table {} already exists, skipping creation.", table.tableName());
        }
        return table;
    }

    /**
     * Save/Put item
     */
    public T save(T item) {
        table.putItem(item);
        return item;
    }

    public Optional<T> findByKey(Key key) {
        T item = table.getItem(key);
        return Optional.ofNullable(item);
    }

    /**
     * Delete item by key
     */
    public Optional<T> deleteByKey(Key key) {
        T deletedItem = table.deleteItem(key);
        return Optional.ofNullable(deletedItem);
    }

    /**
     * Query by partition key
     */
    public Stream<T> queryByPartitionKey(String partitionKey) {
        Key key = Key.builder().partitionValue(partitionKey).build();
        QueryConditional queryConditional = QueryConditional.keyEqualTo(key);
        return table.query(queryConditional).items().stream();
    }

    /**
     * Query with custom conditional
     */
    public Stream<T> query(QueryConditional queryConditional) {
        return table.query(queryConditional).items().stream();
    }

    /**
     * Query using GSI
     */
    public Stream<Page<T>> queryIndex(String indexName, QueryConditional queryConditional) {
        return table.index(indexName).query(queryConditional).stream();
    }

    /**
     * Scan all items
     */
    public Stream<T> scanAll() {
        return table.scan().items().stream();
    }

    /**
     * Update item using the item-based approach
     */
    public T updateItem(T item, IgnoreNullsMode ignoreNullsMode) {
        return table.updateItem(UpdateItemEnhancedRequest.builder(entityClass)
                .item(item)
                .ignoreNullsMode(ignoreNullsMode)
                .build());
    }

    /**
     * Batch put items
     */
    public void batchPutItems(Iterable<T> items) {
        WriteBatch.Builder<T> batchBuilder = WriteBatch.builder(entityClass)
                .mappedTableResource(table);

        items.forEach(batchBuilder::addPutItem);

        enhancedClient.batchWriteItem(builder -> builder
                .writeBatches(batchBuilder.build())
                .build());
    }

    /**
     * Check if item exists
     */
    public boolean exists(Key key) {
        return findByKey(key).isPresent();
    }

    /**
     * Count items (expensive operation - use carefully)
     */
    public long count() {
        return scanAll().count();
    }

    /**
     * Get table instance for advanced operations
     */
    public DynamoDbTable<T> getTable() {
        return table;
    }

    /**
     * Build the table name based on the entity class name.
     *
     * @param entityClass
     * @return
     * @Example: input: TokenBlacklist.class --> fp-token-blacklist-dev
     */
    private String buildTableName(Class<?> entityClass) {
        //1. Get the simple name of the class
        String simpleName = entityClass.getSimpleName();// e.g., TokenBlacklist
        //2. Convert to lowercase and replace camel case with hyphens
        String tableName = simpleName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();

        //3. Build the full table name using the DynamoDbProperties
        // e.g., fp-token-blacklist-dev
        return dynamoDbProperties.getFullTableName(tableName);
    }
}
