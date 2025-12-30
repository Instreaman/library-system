-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_db;

-- 创建图书表
CREATE TABLE IF NOT EXISTS book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '图书ID',
    title VARCHAR(200) NOT NULL COMMENT '图书名称',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    isbn VARCHAR(50) UNIQUE NOT NULL COMMENT 'ISBN编号',
    publisher VARCHAR(100) COMMENT '出版社',
    total_quantity INT DEFAULT 0 COMMENT '馆藏总量',
    available_quantity INT DEFAULT 0 COMMENT '可借数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(100) COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-正常，DISABLED-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建借阅记录表
CREATE TABLE IF NOT EXISTS borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    borrow_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
    return_date DATETIME COMMENT '归还时间',
    due_date DATETIME COMMENT '应还时间',
    status VARCHAR(20) DEFAULT 'BORROWED' COMMENT '状态：BORROWED-已借出，RETURNED-已归还，OVERDUE-已逾期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (book_id) REFERENCES book(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 插入测试用户数据
INSERT INTO user (username, password, real_name, role, email, phone) VALUES
('admin', 'admin123', '系统管理员', 'ADMIN', 'admin@library.com', '13800000000'),
('user1', 'user123', '张三', 'USER', 'user1@library.com', '13800000001');

-- 插入测试图书数据
INSERT INTO book (title, author, isbn, publisher, total_quantity, available_quantity) VALUES
('Java核心技术', 'Cay S. Horstmann', '978-7-111-58595-5', '机械工业出版社', 5, 5),
('Spring Boot实战', 'Craig Walls', '978-7-115-46393-0', '人民邮电出版社', 3, 3),
('Clean Code', 'Robert C. Martin', '9780132350884', 'Prentice Hall', 6, 6),
('Effective Java', 'Joshua Bloch', '9780134685991', 'Addison-Wesley', 5, 5),
('JavaScript: The Good Parts', 'Douglas Crockford', '9780596517748', 'O\'Reilly Media', 4, 4),
('Redis 设计与实现', '黄健宏', '9787115335500', '人民邮电出版社', 7, 7),
('深入理解计算机系统', 'Randal E. Bryant', '9787111544937', '机械工业出版社', 8, 8);
