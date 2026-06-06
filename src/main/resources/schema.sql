ALTER TABLE food MODIFY COLUMN category_id BIGINT NULL;
ALTER TABLE food MODIFY COLUMN image VARCHAR(500);

-- 订单软删除字段由JPA自动创建（ddl-auto: update）
