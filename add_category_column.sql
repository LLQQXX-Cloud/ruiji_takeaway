-- 为商家表添加分类字段
ALTER TABLE business 
ADD COLUMN category_id BIGINT NULL 
AFTER status;

-- 可选：设置默认值为1（快餐便当）
-- UPDATE business SET category_id = 1 WHERE category_id IS NULL;

-- 查看结果
DESCRIBE business;