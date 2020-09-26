CREATE SCHEMA `INTERNET_SHOP` DEFAULT CHARACTER SET utf8;

CREATE TABLE users (
    id           BIGINT          PRIMARY KEY   AUTO_INCREMENT,
    name         VARCHAR(256)                        NOT NULL,
    login        VARCHAR(256)                NOT NULL, UNIQUE,
    password     VARCHAR(256)                        NOT NULL,
    deleted      BOOLEAN               NOT NULL DEFAULT false
);

CREATE TABLE roles (
    id           BIGINT          PRIMARY KEY   AUTO_INCREMENT,
    role_name    VARCHAR(256)                        NOT NULL
);

CREATE TABLE user_roles (
    user_id       BIGINT          NOT NULL,
    role_id       BIGINT          NOT NULL,
        CONSTRAINT fk_ur_users FOREIGN KEY (user_id)
            REFERENCES users(id),
        CONSTRAINT fk_ur_roles FOREIGN KEY (role_id)
            REFERENCES roles(id)
);

CREATE TABLE orders (
    id           BIGINT          PRIMARY KEY   AUTO_INCREMENT,
    fk_user_id   BIGINT          NOT NULL,
    deleted      BOOLEAN         NOT NULL DEFAULT false
        CONSTRAINT fk_users_orders FOREIGN KEY (fk_user_id)
            REFERENCES users(id)
);

CREATE TABLE shopping_cart (
    id           BIGINT          PRIMARY KEY   AUTO_INCREMENT,
    fk_user_id   BIGINT          NOT NULL,
    deleted      BOOLEAN         NOT NULL DEFAULT false
        CONSTRAINT fk_shopping_cart_orders FOREIGN KEY (fk_user_id)
            REFERENCES users(id)
);

CREATE TABLE products (
    id           BIGINT          PRIMARY KEY   AUTO_INCREMENT,
    name         VARCHAR(255)                        NOT NULL,
    price        DOUBLE                              NOT NULL,
    deleted      BOOLEAN               NOT NULL DEFAULT false
);

CREATE TABLE shopping_cart_products (
    cart_id      BIGINT          NOT NULL,
    product_id   BIGINT          NOT NULL,
        CONSTRAINT fk_scp_shopping_cart FOREIGN KEY (cart_id)
            REFERENCES shopping_cart(id),
        CONSTRAINT fk_scp_products FOREIGN KEY (product_id)
            REFERENCES products(id)
);

CREATE TABLE orders_products (
    order_id     BIGINT          NOT NULL,
    product_id   BIGINT          NOT NULL,
        CONSTRAINT fk_op_shopping_cart FOREIGN KEY (order_id)
            REFERENCES orders(id),
        CONSTRAINT fk_op_products FOREIGN KEY (product_id)
            REFERENCES products(id)
);

INSERT INTO `roles`(name)
VALUES ('ADMIN');
INSERT INTO `roles`(name)
VALUES ('USER');