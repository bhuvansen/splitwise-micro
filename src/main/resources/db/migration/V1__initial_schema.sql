CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE expense_groups (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_group_creator
        FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE group_members (
    id VARCHAR(36) PRIMARY KEY,
    group_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_gm_group FOREIGN KEY (group_id) REFERENCES expense_groups(id),
    CONSTRAINT fk_gm_user FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE (group_id, user_id)
);

CREATE TABLE expenses (
    id VARCHAR(36) PRIMARY KEY,
    group_id VARCHAR(255) NOT NULL,
    paid_by VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exp_group FOREIGN KEY (group_id) REFERENCES expense_groups(id),
    CONSTRAINT fk_exp_payer FOREIGN KEY (paid_by) REFERENCES users(id)
);

CREATE TABLE expense_splits (
    id VARCHAR(36) PRIMARY KEY,
    expense_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    share_amount DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_es_expense FOREIGN KEY (expense_id) REFERENCES expenses(id),
    CONSTRAINT fk_es_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE settlements (
    id VARCHAR(36) PRIMARY KEY,

    group_id VARCHAR(255) NOT NULL,
    from_user_id VARCHAR(255) NOT NULL,
    to_user_id VARCHAR(255) NOT NULL,

    amount DECIMAL(12,2) NOT NULL,
    settled_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_settlement_group
        FOREIGN KEY (group_id) REFERENCES expense_groups(id),

    CONSTRAINT fk_settlement_from_user
        FOREIGN KEY (from_user_id) REFERENCES users(id),

    CONSTRAINT fk_settlement_to_user
        FOREIGN KEY (to_user_id) REFERENCES users(id)
);
