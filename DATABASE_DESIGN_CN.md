# 数据库设计文档

## 数据库概览

宠物护理预约系统使用MySQL 8.0作为主数据库，采用JPA/Hibernate进行对象关系映射。数据库设计遵循第三范式，确保数据一致性和完整性。

## 实体关系图 (ERD)

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    User     │◄──────┤ Appointment │──────►│   Pet       │
│ (基础用户)   │       │   (预约)    │       │  (宠物)     │
└─────────────┘       └─────────────┘       └─────────────┘
       ▲                      │
       │                      │
┌─────────────┐               │              ┌─────────────┐
│  Patient    │               │              │   Review    │
│ (患者/宠物主)│               │              │  (评价)     │
└─────────────┘               │              └─────────────┘
       ▲                      │                      │
       │                      │                      │
┌─────────────┐               │              ┌─────────────┐
│Veterinarian │               │              │   Photo     │
│   (兽医)    │               │              │  (照片)     │
└─────────────┘               │              └─────────────┘
       ▲                      │                      │
       │                      │                      │
┌─────────────┐               │              ┌─────────────┐
│   Admin     │               │              │    Role     │
│  (管理员)   │               │              │  (角色)     │
└─────────────┘               │              └─────────────┘
                               │                      │
                        ┌─────────────┐               │
                        │VerifyToken  │               │
                        │ (验证令牌)  │               │
                        └─────────────┘               │
                               │                      │
                        ┌─────────────┐               │
                        │PasswordReset│               │
                        │ (密码重置)  │               │
                        └─────────────┘               │
                                                      │
                                              ┌─────────────┐
                                              │ user_roles  │
                                              │(用户角色关联)│
                                              └─────────────┘
```

## 数据表详细设计

### 1. User (用户基础表)
用户系统的基础实体，使用继承策略实现多种用户类型。

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    gender VARCHAR(10),
    mobile VARCHAR(20),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_type VARCHAR(20) NOT NULL,
    is_enabled BOOLEAN DEFAULT FALSE,
    created_at DATE DEFAULT CURRENT_DATE,
    specialization VARCHAR(100), -- @Transient 字段
    
    INDEX idx_email (email),
    INDEX idx_user_type (user_type),
    INDEX idx_created_at (created_at)
);
```

**字段说明**:
- `id`: 主键，自增长
- `first_name/last_name`: 姓名
- `email`: 邮箱，唯一标识
- `password`: 加密后的密码
- `user_type`: 用户类型（PATIENT, VETERINARIAN, ADMIN）
- `is_enabled`: 账户是否已激活
- `specialization`: 兽医专业领域（@Transient字段）

### 2. Patient (患者表)
继承自User表，表示宠物主人。

```sql
CREATE TABLE patient (
    patient_id BIGINT PRIMARY KEY,
    -- 继承User的所有字段
    
    FOREIGN KEY (patient_id) REFERENCES user(id) ON DELETE CASCADE
);
```

### 3. Veterinarian (兽医表)
继承自User表，包含兽医特有信息。

```sql
CREATE TABLE veterinarian (
    vet_id BIGINT PRIMARY KEY,
    specialization VARCHAR(100), -- 专业领域
    
    FOREIGN KEY (vet_id) REFERENCES user(id) ON DELETE CASCADE
);
```

### 4. Admin (管理员表)
继承自User表，系统管理员。

```sql
CREATE TABLE admin (
    admin_id BIGINT PRIMARY KEY,
    
    FOREIGN KEY (admin_id) REFERENCES user(id) ON DELETE CASCADE
);
```

### 5. Appointment (预约表)
系统核心表，记录所有预约信息。

```sql
CREATE TABLE appointment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reason TEXT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    appointment_no VARCHAR(50) UNIQUE NOT NULL,
    created_at DATE DEFAULT CURRENT_DATE,
    status ENUM(
        'CANCELLED',
        'ON_GOING', 
        'UP_COMING',
        'APPROVED',
        'NOT_APPROVED',
        'WAITING_FOR_APPROVAL',
        'PENDING',
        'COMPLETED'
    ) DEFAULT 'PENDING',
    sender BIGINT NOT NULL, -- 患者ID
    recipient BIGINT NOT NULL, -- 兽医ID
    
    FOREIGN KEY (sender) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient) REFERENCES user(id) ON DELETE CASCADE,
    
    INDEX idx_appointment_date (appointment_date),
    INDEX idx_status (status),
    INDEX idx_sender (sender),
    INDEX idx_recipient (recipient),
    INDEX idx_appointment_no (appointment_no)
);
```

**字段说明**:
- `appointment_no`: 预约编号，系统自动生成
- `status`: 预约状态枚举
- `sender`: 发起预约的患者
- `recipient`: 接收预约的兽医

### 6. Pet (宠物表)
记录宠物的基本信息。

```sql
CREATE TABLE pet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(30) NOT NULL, -- 宠物类型：狗、猫等
    color VARCHAR(30),
    breed VARCHAR(50), -- 品种
    age INT,
    appointment_id BIGINT,
    
    FOREIGN KEY (appointment_id) REFERENCES appointment(id) ON DELETE SET NULL,
    
    INDEX idx_type (type),
    INDEX idx_appointment (appointment_id)
);
```

### 7. Review (评价表)
用户对兽医服务的评价和反馈。

```sql
CREATE TABLE review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    patient_id BIGINT NOT NULL,
    veterinarian_id BIGINT NOT NULL,
    
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (veterinarian_id) REFERENCES veterinarian(vet_id) ON DELETE CASCADE,
    
    INDEX idx_rating (rating),
    INDEX idx_patient (patient_id),
    INDEX idx_veterinarian (veterinarian_id),
    INDEX idx_created_at (created_at)
);
```

### 8. Photo (照片表)
存储用户头像、宠物照片等图片信息。

```sql
CREATE TABLE photo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    download_url VARCHAR(500),
    file_size BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_file_name (file_name),
    INDEX idx_created_at (created_at)
);
```

### 9. Role (角色表)
系统角色定义表。

```sql
CREATE TABLE role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL, -- ROLE_PATIENT, ROLE_VETERINARIAN, ROLE_ADMIN
    description VARCHAR(200),
    
    INDEX idx_name (name)
);
```

### 10. user_roles (用户角色关联表)
多对多关联表，用户和角色的关系。

```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);
```

### 11. verification_token (邮箱验证令牌表)
用于邮箱验证的临时令牌。

```sql
CREATE TABLE verification_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    
    INDEX idx_token (token),
    INDEX idx_expiration (expiration_date),
    INDEX idx_user (user_id)
);
```

### 12. password_reset (密码重置表)
用于密码重置的临时令牌。

```sql
CREATE TABLE password_reset (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    
    INDEX idx_token (token),
    INDEX idx_expiration (expiration_date),
    INDEX idx_user (user_id)
);
```

## 关系说明

### 一对一关系
- `User` ↔ `Photo`: 每个用户可以有一个头像照片
- `User` ↔ `VerificationToken`: 每个用户有一个验证令牌
- `User` ↔ `PasswordReset`: 每个用户有一个密码重置令牌

### 一对多关系
- `User` → `Appointment`: 用户可以有多个预约（作为患者或兽医）
- `User` → `Review`: 患者可以写多个评价，兽医可以收到多个评价
- `Appointment` → `Pet`: 一个预约可以包含多个宠物

### 多对多关系
- `User` ↔ `Role`: 用户可以有多个角色，角色可以分配给多个用户

## 索引策略

### 主要索引
1. **主键索引**: 所有表的主键自动创建聚簇索引
2. **唯一索引**: email、appointment_no等唯一字段
3. **外键索引**: 所有外键字段自动创建索引

### 查询优化索引
1. **复合索引**:
   ```sql
   -- 预约查询优化
   CREATE INDEX idx_appointment_date_status ON appointment(appointment_date, status);
   
   -- 用户类型和创建时间复合索引
   CREATE INDEX idx_user_type_created ON user(user_type, created_at);
   
   -- 评价统计优化
   CREATE INDEX idx_review_vet_rating ON review(veterinarian_id, rating);
   ```

2. **部分索引** (针对特定条件):
   ```sql
   -- 只为启用的用户创建索引
   CREATE INDEX idx_enabled_users ON user(email) WHERE is_enabled = TRUE;
   ```

## 数据约束

### 检查约束
```sql
-- 评分范围约束
ALTER TABLE review ADD CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5);

-- 年龄约束
ALTER TABLE pet ADD CONSTRAINT chk_age CHECK (age >= 0 AND age <= 50);

-- 预约时间约束（不能预约过去的时间）
ALTER TABLE appointment ADD CONSTRAINT chk_appointment_date 
    CHECK (appointment_date >= CURRENT_DATE);
```

### 触发器
```sql
-- 自动生成预约编号
DELIMITER //
CREATE TRIGGER tr_appointment_no 
BEFORE INSERT ON appointment
FOR EACH ROW 
BEGIN
    IF NEW.appointment_no IS NULL THEN
        SET NEW.appointment_no = CONCAT('APT-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', 
            LPAD((SELECT COALESCE(MAX(SUBSTRING(appointment_no, -3)), 0) + 1 
                  FROM appointment 
                  WHERE DATE(created_at) = CURRENT_DATE), 3, '0'));
    END IF;
END//
DELIMITER ;

-- 更新时间戳触发器
CREATE TRIGGER tr_user_updated 
BEFORE UPDATE ON user
FOR EACH ROW 
SET NEW.updated_at = CURRENT_TIMESTAMP;
```

## 性能优化建议

### 1. 分区策略
```sql
-- 按日期分区预约表
ALTER TABLE appointment PARTITION BY RANGE (YEAR(appointment_date)) (
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

### 2. 查询优化
```sql
-- 使用覆盖索引避免回表查询
CREATE INDEX idx_appointment_cover 
ON appointment(sender, appointment_date, status, appointment_no);

-- 统计查询优化
CREATE INDEX idx_review_stats 
ON review(veterinarian_id, rating, created_at);
```

### 3. 存储引擎选择
- **InnoDB**: 主要业务表（支持事务、外键）
- **MyISAM**: 日志表、统计表（读密集型）

## 数据迁移策略

### 版本控制
使用Flyway进行数据库版本控制：

```sql
-- V1__Create_initial_schema.sql
-- V2__Add_photo_table.sql
-- V3__Add_indexes_for_performance.sql
```

### 备份策略
```bash
# 每日全量备份
mysqldump --single-transaction --routines --triggers pet-care-system > backup_$(date +%Y%m%d).sql

# 增量备份（binlog）
mysqlbinlog --start-datetime="2024-01-01 00:00:00" mysql-bin.000001 > incremental_backup.sql
```

## 安全考虑

### 1. 数据加密
- 密码字段使用BCrypt加密
- 敏感信息字段考虑AES加密

### 2. 访问控制
```sql
-- 创建应用专用数据库用户
CREATE USER 'petcare_app'@'%' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON pet_care_system.* TO 'petcare_app'@'%';

-- 只读用户（用于报表查询）
CREATE USER 'petcare_readonly'@'%' IDENTIFIED BY 'readonly_password';
GRANT SELECT ON pet_care_system.* TO 'petcare_readonly'@'%';
```

### 3. 审计日志
```sql
-- 创建操作日志表
CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(50) NOT NULL,
    operation VARCHAR(10) NOT NULL, -- INSERT, UPDATE, DELETE
    old_values JSON,
    new_values JSON,
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_table_operation (table_name, operation),
    INDEX idx_created_at (created_at)
);
```

这个数据库设计文档提供了完整的表结构、关系定义、索引策略和性能优化建议，为系统的数据持久化提供了坚实的基础。