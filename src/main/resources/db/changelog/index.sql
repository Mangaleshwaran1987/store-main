CREATE INDEX IDX_CUSTOMER_NAME ON admin.customer(name);
CREATE INDEX IDX_ORDER_CUSTOMER_ID ON admin."order"(customer_id);