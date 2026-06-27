-- Create customer table
CREATE TABLE admin.customer (
                                id BIGSERIAL PRIMARY KEY,
                                name VARCHAR(255) NOT NULL
);

-- Create order table
CREATE TABLE admin."order" (
                               id BIGSERIAL PRIMARY KEY,
                               description VARCHAR(255) NOT NULL,
                               customer_id BIGINT NOT NULL,
                               CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES admin.customer (id)
);

--Create product table
CREATE TABLE admin."product" (
                                 id BIGSERIAL PRIMARY KEY,
                                 description VARCHAR(100) NOT NULL
);
--Create order_products Mapping table
CREATE TABLE admin."order_products" (

                                        order_id BIGINT NOT NULL,
                                        product_id BIGINT NOT NULL,
                                        PRIMARY KEY(order_id, product_id),

                                        CONSTRAINT fk_order
                                            FOREIGN KEY(order_id)
                                                REFERENCES admin.order(id),

                                        CONSTRAINT fk_product
                                            FOREIGN KEY(product_id)
                                                REFERENCES admin.product(id)
);