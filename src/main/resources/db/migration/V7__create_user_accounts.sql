CREATE TABLE user_accounts (
    id BIGSERIAL PRIMARY KEY,

    email VARCHAR(255) NOT NULL UNIQUE,

    password_hash VARCHAR(255) NOT NULL,

    passenger_id BIGINT NOT NULL UNIQUE,

    CONSTRAINT fk_user_account_passenger
        FOREIGN KEY (passenger_id)
        REFERENCES passengers(id)
);