CREATE SCHEMA `INTERNET_SHOP` DEFAULT CHARACTER SET utf8;

CREATE TABLE products (
    id           BIGINT          PRIMARY KEY   AUTO_INCREMENT,
    name         VARCHAR(255)                        NOT NULL,
    price        DOUBLE                              NOT NULL,
    deleted      BOOLEAN               NOT NULL DEFAULT false
);
