-- Bootstrap an initial ADMIN role and account for environments
-- where user creation is intentionally restricted in the UI/API.

-- 0) Create required tables when schema was dropped
CREATE TABLE IF NOT EXISTS role (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NULL,
    created_date DATETIME(6) NOT NULL,
    last_modified_date DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_name (name)
);

CREATE TABLE IF NOT EXISTS _user (
    id INT NOT NULL AUTO_INCREMENT,
    firstname VARCHAR(255) NULL,
    lastname VARCHAR(255) NULL,
    date_of_birth DATE NULL,
    email VARCHAR(255) NULL,
    password VARCHAR(255) NULL,
    is_temporary_password BIT(1) NOT NULL DEFAULT b'0',
    account_locked BIT(1) NOT NULL DEFAULT b'0',
    enabled BIT(1) NOT NULL DEFAULT b'0',
    user_id INT NULL,
    created_date DATETIME(6) NOT NULL,
    last_modified_date DATETIME(6) NULL,
    password_reset_token VARCHAR(255) NULL,
    token_expiration_time DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_email (email)
);

CREATE TABLE IF NOT EXISTS _user_roles (
    user_id INT NOT NULL,
    roles_id INT NOT NULL,
    PRIMARY KEY (user_id, roles_id)
);

-- Add FKs only if they are missing
SET @fk_user_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = '_user'
      AND constraint_name = 'fk_user_parent'
      AND constraint_type = 'FOREIGN KEY'
);

SET @sql_fk_user = IF(
    @fk_user_exists = 0,
    'ALTER TABLE _user ADD CONSTRAINT fk_user_parent FOREIGN KEY (user_id) REFERENCES _user (id)',
    'SELECT 1'
);
PREPARE stmt_fk_user FROM @sql_fk_user;
EXECUTE stmt_fk_user;
DEALLOCATE PREPARE stmt_fk_user;

SET @fk_user_roles_user_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = '_user_roles'
      AND constraint_name = 'fk_user_roles_user'
      AND constraint_type = 'FOREIGN KEY'
);

SET @sql_fk_user_roles_user = IF(
    @fk_user_roles_user_exists = 0,
    'ALTER TABLE _user_roles ADD CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES _user (id)',
    'SELECT 1'
);
PREPARE stmt_fk_user_roles_user FROM @sql_fk_user_roles_user;
EXECUTE stmt_fk_user_roles_user;
DEALLOCATE PREPARE stmt_fk_user_roles_user;

SET @fk_user_roles_role_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE table_schema = DATABASE()
      AND table_name = '_user_roles'
      AND constraint_name = 'fk_user_roles_role'
      AND constraint_type = 'FOREIGN KEY'
);

SET @sql_fk_user_roles_role = IF(
    @fk_user_roles_role_exists = 0,
    'ALTER TABLE _user_roles ADD CONSTRAINT fk_user_roles_role FOREIGN KEY (roles_id) REFERENCES role (id)',
    'SELECT 1'
);
PREPARE stmt_fk_user_roles_role FROM @sql_fk_user_roles_role;
EXECUTE stmt_fk_user_roles_role;
DEALLOCATE PREPARE stmt_fk_user_roles_role;

-- 1) Ensure ADMIN role exists
SET @next_role_id = (SELECT COALESCE(MAX(id), 0) + 1 FROM role);

INSERT INTO role (id, name, created_date)
SELECT @next_role_id, 'ADMIN', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM role WHERE name = 'ADMIN'
);

-- 2) Ensure bootstrap admin user exists
-- plaintext password used to generate the hash below: Admin@12345
SET @next_user_id = (SELECT COALESCE(MAX(id), 0) + 1 FROM _user);

INSERT INTO _user (
    id,
    firstname,
    lastname,
    date_of_birth,
    email,
    password,
    is_temporary_password,
    account_locked,
    enabled,
    created_date
)
SELECT
    @next_user_id,
    'System',
    'Admin',
    '1990-01-01',
    'crm@local.org',
    '$2a$10$a4Krmi.xUAEKktMMqnTBTO121kNRdzs2rpdExBQGPplUwKHeqJCcu',
    0,
    0,
    1,
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM _user WHERE email = 'crm@local.org'
);

-- 3) Ensure user-role link exists (join table name may vary by JPA naming)
SET @user_id = (SELECT id FROM _user WHERE email = 'crm@local.org');
SET @role_id = (SELECT id FROM role WHERE name = 'ADMIN');

SET @join_table = NULL;
SET @user_col = NULL;
SET @role_col = NULL;

SELECT
    k1.TABLE_NAME,
    k1.COLUMN_NAME,
    k2.COLUMN_NAME
INTO @join_table, @user_col, @role_col
FROM information_schema.KEY_COLUMN_USAGE k1
JOIN information_schema.KEY_COLUMN_USAGE k2
  ON k1.TABLE_SCHEMA = k2.TABLE_SCHEMA
 AND k1.TABLE_NAME = k2.TABLE_NAME
WHERE k1.TABLE_SCHEMA = DATABASE()
  AND k1.REFERENCED_TABLE_NAME = '_user'
  AND k2.REFERENCED_TABLE_NAME = 'role'
LIMIT 1;

SET @link_sql = IF(
    @join_table IS NULL,
    'SELECT 1',
    CONCAT(
        'INSERT IGNORE INTO ', @join_table,
        ' (', @user_col, ', ', @role_col, ') VALUES (', @user_id, ', ', @role_id, ')'
    )
);

PREPARE stmt FROM @link_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
