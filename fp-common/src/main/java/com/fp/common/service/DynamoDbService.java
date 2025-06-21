package com.fp.common.service;

import com.fp.common.properties.DynamoDbProperties;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

///
/// # Service class for DynamoDB operations
///
///
///
@RequiredArgsConstructor
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
