"""
Database initialization script for AWS Lambda.
Initializes the database schema and tables for the:
    - Follow Service
    - Content Service
Database: PostgreSQL
Version: 17.4
"""

import json
import logging
import os
import traceback
from typing import Dict
import signal

log = logging.getLogger()
log.setLevel(logging.INFO)

try:
    import psycopg2
    PSYCOPG2_AVAILABLE = True
except ImportError as e:
    print(f"CRITICAL: import failed: {e}")
    PSYCOPG2_AVAILABLE = False
    error_msg = f"CRITICAL: psycopg2 import failed: {e}"
    psycopg2 = None

# Steps:
# 1. Get DB connection
# 2. Create schema if not exists.
# 3. Create tables if not exists.
# 4. Create indexes if not exists.
# 5. Close connection.


def _get_db_conn(config: Dict[str, str]):
    """Get PostgreSQL DB connection using psycopg2"""
    try:
        conn = psycopg2.connect(
            host=config['host'],
            port=config['port'],
            database=config['database'],
            user=config['username'],
            password=config['password'],
            connect_timeout=30
        )
        log.info(f"Successfully connected to DB: {config['host']}")
        return conn
    except Exception as e:
        log.error(f"Failed to connect to DB: {str(e)}")
        raise


def _create_schema(cursor, schema_name: str):
    """Create schema if not exists according to the schema_name and db connection"""
    try:
        log.info(f"Creating schema: {schema_name}")
        SQL = f"CREATE SCHEMA IF NOT EXISTS {schema_name}"
        cursor.execute(SQL)
        log.info(f"Schema {schema_name} created successfully.")
    except Exception as e:
        log.error(f"Failed to create schema {schema_name}: {str(e)}")
        raise


def _create_follow_service_tables(cursor, schema_name: str):
    """Create tables for follow service, including indexes"""
    try:
        log.info(f"Creating Follow Service tables in schema: {schema_name}")
        # Set to the schema
        cursor.execute(f"SET search_path TO {schema_name}")
        CREATE_TABLE_SQL = """
                           CREATE TABLE IF NOT EXISTS "follows"
                           (
                               "followee_id" varchar,
                               "follower_id" varchar,
                               "created_at"  timestamp WITH TIME ZONE NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'),
                               PRIMARY KEY ("followee_id", "follower_id")
                           ); \
 \
                           """
        cursor.execute(CREATE_TABLE_SQL)
        log.info("Follow Service tables created successfully.")

        # Create indexes
        log.info("Creating indexes for Follow Service tables...")
        CREATE_INDEX_SQL = """
                           CREATE INDEX IF NOT EXISTS idx_follower_followee ON "follows" ("follower_id", "followee_id"); \
                           CREATE INDEX IF NOT EXISTS idx_followee_follower ON "follows" ("followee_id", "follower_id"); \
                           CREATE INDEX IF NOT EXISTS idx_created_at_follower_id ON "follows" ("created_at", "follower_id"); \
                           CREATE INDEX IF NOT EXISTS idx_created_at_followee_id ON "follows" ("created_at", "followee_id"); \
                           """
        cursor.execute(CREATE_INDEX_SQL)
        log.info("Indexes for Follow Service tables created successfully.")
    except Exception as e:
        log.error(f"Failed to create Follow Service tables: {str(e)}")
        raise


def _create_content_service_tables(cursor, schema_name: str):
    """Create tables for content service, including indexes"""
    try:
        log.info(f"Creating Content Service tables in schema: {schema_name}")
        # Set to the schema
        cursor.execute(f"SET search_path TO {schema_name}")
        CREATE_TABLE_SQL = """
                           CREATE TABLE IF NOT EXISTS "posts"
                           (
                               "id"          BIGSERIAL PRIMARY KEY,
                               "content"     text                     NOT NULL,
                               "account_id"  varchar                  NOT NULL,
                               "created_at"  timestamptz          NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'),
                               "modified_at" timestamptz,
                               "image_links" text,
                               "labels"      text,
                               "like_count"       bigint               NOT NULL DEFAULT (0),
                               "view_count"       bigint               NOT NULL DEFAULT (0),
                               "comment_count" bigint                  NOT NULL DEFAULT (0),
                               "repost_count" bigint                  NOT NULL DEFAULT (0),
                               "status"      varchar                  NOT NULL DEFAULT 'active',
                               CONSTRAINT chk_posts_status CHECK (status IN ('active', 'deleted', 'hidden'))
                           ); \
 \
                            COMMENT ON TABLE  posts                    IS 'Financial Pulse user posts';
                            COMMENT ON COLUMN posts.content            IS 'Rich text HTML content';
                            COMMENT ON COLUMN posts.account_id         IS 'Author account id';
                            COMMENT ON COLUMN posts.image_links        IS 'Comma-separated or JSON-serialized image URLs/keys';
                            COMMENT ON COLUMN posts.labels             IS 'Tags/labels (consider jsonb for structure)';
                            COMMENT ON COLUMN posts.like_count              IS 'Cached like count';
                            COMMENT ON COLUMN posts.view_count              IS 'Cached view count';
                            COMMENT ON COLUMN posts.comment_count      IS 'Cached comment count';
                            COMMENT ON COLUMN posts.repost_count       IS 'Cached repost/quote count';
                            COMMENT ON COLUMN posts.status             IS 'active/deleted/hidden (soft delete)';

                           CREATE TABLE IF NOT EXISTS "comments"
                           (
                               "id"                BIGSERIAL            PRIMARY KEY,
                               "content"           text                 NOT NULL,
                               "post_id"           bigint               NOT NULL,
                               "account_id"        varchar              NOT NULL,
                               "created_at"        timestamptz          NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'),
                               "modified_at"       timestamptz,
                               "status"            varchar              NOT NULL DEFAULT 'active',
                               "like_count"        bigint               NOT NULL DEFAULT (0),
                               "reply_count"       bigint               NOT NULL DEFAULT (0),
                               "parent_comment_id" bigint,
                               CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
                               CONSTRAINT fk_comments_parent FOREIGN KEY (parent_comment_id) REFERENCES comments (id) ON DELETE SET NULL,
                               CONSTRAINT chk_comments_status CHECK (status IN ('active', 'deleted'))
                           );
                            COMMENT ON TABLE  comments                     IS 'Comments on posts (one-level threading via parent_comment_id)';
                            COMMENT ON COLUMN comments.post_id             IS 'FK to posts.id';
                            COMMENT ON COLUMN comments.account_id          IS 'Author account id';
                            COMMENT ON COLUMN comments.status              IS 'active/deleted/hidden';
                            COMMENT ON COLUMN comments.like_count               IS 'Cached like count';
                            COMMENT ON COLUMN comments.reply_count             IS 'Cached direct reply count';
                            COMMENT ON COLUMN comments.parent_comment_id   IS 'Nullable parent for replies';

                            CREATE TABLE IF NOT EXISTS post_reactions (
  post_id    bigint      NOT NULL,
  account_id varchar     NOT NULL,
  type       varchar     NOT NULL,
  created_at timestamptz NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'),
  PRIMARY KEY (post_id, account_id, type),
  CONSTRAINT fk_post_reactions_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
  CONSTRAINT chk_post_reactions_type CHECK (type IN ('LIKE','REPOST','QUOTE'))
);

CREATE TABLE IF NOT EXISTS comment_reactions (
  comment_id bigint      NOT NULL,
  account_id varchar     NOT NULL,
  created_at timestamptz NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'),
  PRIMARY KEY (comment_id, account_id),
  CONSTRAINT fk_comment_reactions FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_views(
	id BIGSERIAL PRIMARY KEY,
	post_id  bigint NOT NULL,
	account_id varchar NULL,
	client_hash varchar NULL,
	created_at timestamptz NOT NULL DEFAULT (now() AT TIME ZONE 'UTC'),
	CONSTRAINT uq_post_views_account UNIQUE(post_id, account_id),
	CONSTRAINT uq_post_views_client UNIQUE(post_id, client_hash),
	CONSTRAINT fk_post_views_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);
                           """
        cursor.execute(CREATE_TABLE_SQL)
        log.info("Content Service tables created successfully.")
        # Create indexes
        log.info("Creating indexes for Content Service tables...")
        CREATE_INDEX_SQL = """
                CREATE INDEX IF NOT EXISTS idx_posts_account_created
                    ON posts (account_id, created_at DESC);
                CREATE INDEX IF NOT EXISTS idx_posts_created
                    ON posts (created_at DESC);
                CREATE INDEX IF NOT EXISTS idx_comments_post_created
                    ON comments (post_id, created_at DESC);
                CREATE INDEX IF NOT EXISTS idx_comments_parent_created
                    ON comments (parent_comment_id, created_at DESC);
                    """
        cursor.execute(CREATE_INDEX_SQL)
        log.info("Indexes for Content Service tables created successfully.")

    except Exception as e:
        log.error(f"Failed to create Content Service tables: {str(e)}")
        raise


def initialize_follow_db(config: Dict[str, str], schema_name: str):
    """Initialize Follow Service database schema and tables"""
    conn = None
    try:
        conn = _get_db_conn(config)
        cursor = conn.cursor()
        _create_schema(cursor, schema_name)
        _create_follow_service_tables(cursor, schema_name)
        conn.commit()
        cursor.close()
        log.info("Follow Service database initialized successfully.")
        return True
    except Exception as e:
        if conn:
            conn.rollback()
        log.error(f"Failed to initialize Follow Service database: {str(e)}")
        log.error(f"Traceback: {traceback.format_exc()}")
        return False
    finally:
        if conn:
            conn.close()
            log.info("Database connection closed.")


def initialize_content_db(config: Dict[str, str], schema_name: str):
    """Initialize Content Service database schema and tables"""
    conn = None
    try:
        conn = _get_db_conn(config)
        cursor = conn.cursor()
        _create_schema(cursor, schema_name)
        _create_content_service_tables(cursor, schema_name)
        conn.commit()
        cursor.close()
        log.info("Content Service database initialized successfully.")
        return True
    except Exception as e:
        if conn:
            conn.rollback()
        log.error(f"Failed to initialize Content Service database: {str(e)}")
        log.error(f"Traceback: {traceback.format_exc()}")
        return False
    finally:
        if conn:
            conn.close()
            log.info("Database connection closed.")


def lambda_handler(event, context):
    """
    AWS Lambda handler for CloudFormation custom resource

    Args:
        event: Lambda event containing CloudFormation parameters
        context: Lambda context object

    Returns:
        Response for CloudFormation
    """
    try:
        import cfnresponse
        # Import cfnresponse for CloudFormation custom resource
        log.info(f"Lambda event: {json.dumps(event, default=str)}")

        # If the resource already exists, no changes will be made
        # Extract parameters from event
        properties = event['ResourceProperties']
        # Timeout handling
        timeout = int(properties.get('ServiceTimeout', 300))  # Default 5 minutes
        def timeout_handler(signum, frame):
            raise TimeoutError(f"Function timed out after {timeout} seconds")
        signal.signal(signal.SIGALRM, timeout_handler)
        signal.alarm(timeout)

        if event['RequestType'] == 'Delete':
            log.info("Delete operation - no action required")
            cfnresponse.send(event, context, cfnresponse.SUCCESS, {})
            return
        # Check if psycopg2 is available before proceeding
        if not PSYCOPG2_AVAILABLE:
            log.error(error_msg)
            cfnresponse.send(event, context, cfnresponse.FAILED, {
                'Message': error_msg,
                'Error': 'psycopg2 import failed'
            })
            return


        # Construct DB config Dict
        db_config = {
            'host': properties['DBHost'],
            'port': int(properties['DBPort']),
            'database': properties['DBName'],
            'username': properties['DBUsername'],
            'password': properties['DBPassword']
        }
        # Get schema name
        follow_schema_name = properties['FollowSchemaName']
        content_schema_name = properties['ContentSchemaName']

        results = {}
        # Follow Service DB initialization
        try:
            # Initialize databases
            follow_init_success = initialize_follow_db(db_config, follow_schema_name)
            results['FollowDBInit'] = cfnresponse.SUCCESS if follow_init_success else cfnresponse.FAILED
            if follow_init_success:
                log.info("Follow Service database initialization completed successfully")
            else:
                log.error("Follow Service database initialization failed")
        except Exception as e:
            results['FollowDBInit'] = f"FAILED: {str(e)}"
            log.error(f"Follow Service database initialization failed: {str(e)}")

        # Content Service DB initialization
        try:
            content_init_success = initialize_content_db(db_config, content_schema_name)
            results['ContentDBInit'] = cfnresponse.SUCCESS if content_init_success else cfnresponse.FAILED
            if content_init_success:
                log.info("Content Service database initialization completed successfully")
            else:
                log.error("Content Service database initialization failed")
        except Exception as e:
            results['ContentDBInit'] = f"FAILED: {str(e)}"
            log.error(f"Content Service database initialization failed: {str(e)}")

        # Conclude initialization results
        success_count = sum(1 for status in results.values() if status == cfnresponse.SUCCESS)
        total_count = len(results)
        response_data = {'Message': f'{success_count} / {total_count} service databases initialized successfully'}
        if success_count == total_count:
            # All succeeded
            cfnresponse.send(event, context, cfnresponse.SUCCESS, response_data)
        else:
            # Partial or total failure - FAIL the stack to trigger rollback
            error_message = f"Database initialization failed: {results}"
            log.error(error_message)
            cfnresponse.send(event, context, cfnresponse.FAILED, {
                'Message': error_message,
                'FailedOperations': results
            })
    except TimeoutError as te:
        cfnresponse.send(event, context, cfnresponse.FAILED, {
            'Message': f'Operation timed out after {timeout} seconds.'
        })
        return
    except Exception as e:
        log.error(f"Lambda handler critical error: {str(e)}")
        log.error(f"Traceback: {traceback.format_exc()}")
        # FAIL the stack to trigger rollback on any critical error
        try:
            import cfnresponse
            cfnresponse.send(event, context, cfnresponse.FAILED, {
                'Message': f'Lambda execution failed: {str(e)}',
                'ErrorDetails': traceback.format_exc()
            })
        except Exception as cfn_error:
            log.error(f"Failed to send CloudFormation response: {str(cfn_error)}")
            # If we can't even send response, CloudFormation will timeout and rollback


# Only for testing, not used in production
def drop_schema(config: Dict[str, str], schema_name: str):
    """Drop schema and all its objects"""
    conn = None
    try:
        conn = _get_db_conn(config)
        cursor = conn.cursor()
        log.info(f"Dropping schema: {schema_name}")
        SQL = f"DROP SCHEMA IF EXISTS {schema_name} CASCADE"
        cursor.execute(SQL)
        conn.commit()
        cursor.close()
        log.info(f"Schema {schema_name} dropped successfully.")
        return True
    except Exception as e:
        if conn:
            conn.rollback()
        log.error(f"Failed to drop schema {schema_name}: {str(e)}")
        log.error(f"Traceback: {traceback.format_exc()}")
        return False
    finally:
        if conn:
            conn.close()
            log.info("Database connection closed.")


# Local execution for testing
def main():
    """
    Main function for local testing
    Usage: python3 lambda-db-initialize.py
    :return:
    """
    log.info("Running database initialization locally...")

    # Example configuration - replace with your actual values
    db_config = {
        'host': os.getenv('DB_HOST', 'localhost'),
        'port': int(os.getenv('DB_PORT', '5432')),
        'database': os.getenv('DB_NAME', 'postgres'),
        'username': os.getenv('DB_USERNAME', 'postgres'),
        'password': os.getenv('DB_PASSWORD', '8866')
    }

    follow_schema = os.getenv('FOLLOW_SCHEMA_NAME', 'fp_follow_dev')
    content_schema = os.getenv('CONTENT_SCHEMA_NAME', 'fp_content_dev')
    drop_schema(db_config, follow_schema)
    drop_schema(db_config, content_schema)
    success = initialize_follow_db(db_config, follow_schema)
    success2 = initialize_content_db(db_config, content_schema)
    if success:
        log.info("Follow Service database initialization completed successfully")
    else:
        log.error("Follow Service database initialization failed")
    if success2:
        log.info("Content Service database initialization completed successfully")
    else:
        log.error("Content Service database initialization failed")


if __name__ == '__main__':
    main()
