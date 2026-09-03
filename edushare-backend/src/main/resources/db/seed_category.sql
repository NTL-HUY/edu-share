-- db/seed_category.sql
INSERT INTO category (name)
SELECT v.name FROM (VALUES
    ('Công nghệ Thông tin'),
    ('Kỹ thuật Phần mềm'),
    ('Khoa học Máy tính'),
    ('Toán học'),
    ('Vật lý'),
    ('Kinh tế'),
    ('Quản trị Kinh doanh'),
    ('Tiếng Anh')
) AS v(name)
WHERE NOT EXISTS (
    SELECT 1 FROM category c WHERE c.name = v.name
);