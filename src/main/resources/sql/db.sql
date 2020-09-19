CREATE SCHEMA `INTERNET_SHOP` DEFAULT CHARACTER SET utf8;

CREATE TABLE PRODUCTS (
    ID           BIGINT          PRIMARY KEY   AUTO_INCREMENT,
    NAME         VARCHAR(255)                        NOT NULL,
    PRICE        DOUBLE                              NOT NULL,
    DELETED      BOOLEAN               NOT NULL DEFAULT false
);
