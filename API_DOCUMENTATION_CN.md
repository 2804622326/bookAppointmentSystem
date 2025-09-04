# 宠物护理预约系统 API 文档

## API 概述

本文档详细介绍了宠物护理预约系统的RESTful API接口。所有API都基于HTTP协议，数据格式为JSON。

### 基础信息
- **基础URL**: `http://localhost:9192`
- **API版本**: v1
- **认证方式**: JWT Bearer Token
- **内容类型**: `application/json`

## 认证机制

### JWT Token 认证
系统使用JWT（JSON Web Token）进行用户认证。在请求需要认证的API时，需要在HTTP头部携带token：

```
Authorization: Bearer <your-jwt-token>
```

## API 端点分类

### 1. 认证相关 API (`/auth`)

#### 用户注册
```http
POST /auth/register
Content-Type: application/json

{
  "firstName": "张",
  "lastName": "三",
  "email": "zhangsan@example.com",
  "password": "password123",
  "userType": "PATIENT",
  "phoneNumber": "13800138000",
  "gender": "男"
}
```

**响应**:
```json
{
  "message": "用户注册成功",
  "data": {
    "id": 1,
    "firstName": "张",
    "lastName": "三",
    "email": "zhangsan@example.com",
    "userType": "PATIENT",
    "isEnabled": false
  }
}
```

#### 用户登录
```http
POST /auth/login
Content-Type: application/json

{
  "email": "zhangsan@example.com",
  "password": "password123"
}
```

**响应**:
```json
{
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "firstName": "张",
      "lastName": "三",
      "email": "zhangsan@example.com",
      "userType": "PATIENT"
    }
  }
}
```

#### 邮箱验证
```http
GET /auth/verify-email?token=<verification-token>
```

#### 密码重置请求
```http
POST /auth/password-reset-request
Content-Type: application/json

{
  "email": "zhangsan@example.com"
}
```

#### 重置密码
```http
POST /auth/reset-password
Content-Type: application/json

{
  "token": "<reset-token>",
  "newPassword": "newpassword123"
}
```

### 2. 用户管理 API (`/users`)

#### 获取用户信息
```http
GET /users/{userId}
Authorization: Bearer <token>
```

#### 更新用户信息
```http
PUT /users/{userId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "张",
  "lastName": "三丰",
  "phoneNumber": "13800138001",
  "gender": "男"
}
```

#### 获取所有用户（管理员）
```http
GET /users/all
Authorization: Bearer <admin-token>
```

#### 删除用户（管理员）
```http
DELETE /users/{userId}
Authorization: Bearer <admin-token>
```

### 3. 预约管理 API (`/appointments`)

#### 创建预约
```http
POST /appointments/book?senderId=1&recipientId=2
Authorization: Bearer <token>
Content-Type: application/json

{
  "appointmentDate": "2024-12-25",
  "appointmentTime": "14:30",
  "reason": "常规检查",
  "petIds": [1, 2]
}
```

#### 获取所有预约
```http
GET /appointments/all
Authorization: Bearer <token>
```

#### 根据ID获取预约
```http
GET /appointments/{appointmentId}
Authorization: Bearer <token>
```

#### 更新预约状态
```http
PUT /appointments/{appointmentId}/update
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "APPROVED"
}
```

#### 取消预约
```http
DELETE /appointments/{appointmentId}/cancel
Authorization: Bearer <token>
```

#### 获取用户预约
```http
GET /appointments/user/{userId}/appointments
Authorization: Bearer <token>
```

### 4. 兽医管理 API (`/veterinarians`)

#### 获取所有兽医
```http
GET /veterinarians/all
```

#### 根据ID获取兽医
```http
GET /veterinarians/{vetId}
```

#### 搜索兽医
```http
GET /veterinarians/search?specialization=心脏科
```

#### 获取兽医可用时间
```http
GET /veterinarians/{vetId}/available-times?date=2024-12-25
```

#### 更新兽医信息
```http
PUT /veterinarians/{vetId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "specialization": "心脏科",
  "biography": "专业的宠物心脏科医生"
}
```

### 5. 宠物管理 API (`/pets`)

#### 注册宠物
```http
POST /pets/register
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "小白",
  "type": "狗",
  "breed": "金毛",
  "age": 3,
  "color": "金色"
}
```

#### 获取用户的宠物
```http
GET /pets/user/{userId}/pets
Authorization: Bearer <token>
```

#### 更新宠物信息
```http
PUT /pets/{petId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "小白白",
  "age": 4
}
```

#### 删除宠物
```http
DELETE /pets/{petId}
Authorization: Bearer <token>
```

### 6. 评价管理 API (`/reviews`)

#### 提交评价
```http
POST /reviews/submit
Authorization: Bearer <token>
Content-Type: application/json

{
  "veterinarianId": 2,
  "patientId": 1,
  "rating": 5,
  "feedback": "服务非常好，医生很专业"
}
```

#### 获取兽医的评价
```http
GET /reviews/veterinarian/{vetId}/reviews
```

#### 更新评价
```http
PUT /reviews/{reviewId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "rating": 4,
  "feedback": "更新后的评价内容"
}
```

#### 删除评价
```http
DELETE /reviews/{reviewId}
Authorization: Bearer <token>
```

### 7. 图片管理 API (`/photos`)

#### 上传图片
```http
POST /photos/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

{
  "file": <binary-data>,
  "entityId": 1,
  "entityType": "USER"
}
```

#### 获取图片
```http
GET /photos/{photoId}
```

#### 删除图片
```http
DELETE /photos/{photoId}
Authorization: Bearer <token>
```

### 8. 管理员 API (`/admin`)

#### 获取系统统计
```http
GET /admin/stats
Authorization: Bearer <admin-token>
```

**响应**:
```json
{
  "totalUsers": 150,
  "totalAppointments": 320,
  "totalVeterinarians": 15,
  "totalPets": 280,
  "monthlyAppointments": [
    {"month": "1月", "count": 25},
    {"month": "2月", "count": 30}
  ]
}
```

## 数据模型

### 用户 (User)
```json
{
  "id": 1,
  "firstName": "张",
  "lastName": "三",
  "email": "zhangsan@example.com",
  "phoneNumber": "13800138000",
  "gender": "男",
  "userType": "PATIENT",
  "isEnabled": true,
  "createdAt": "2024-01-15",
  "photo": {
    "id": 1,
    "fileName": "avatar.jpg",
    "fileType": "image/jpeg",
    "filePath": "/uploads/avatars/avatar.jpg"
  },
  "roles": ["ROLE_PATIENT"]
}
```

### 预约 (Appointment)
```json
{
  "id": 1,
  "appointmentNo": "APT-20241225-001",
  "appointmentDate": "2024-12-25",
  "appointmentTime": "14:30",
  "reason": "常规检查",
  "status": "WAITING_FOR_APPROVAL",
  "createdAt": "2024-12-20",
  "patient": {
    "id": 1,
    "firstName": "张",
    "lastName": "三"
  },
  "veterinarian": {
    "id": 2,
    "firstName": "李",
    "lastName": "医生",
    "specialization": "内科"
  },
  "pets": [
    {
      "id": 1,
      "name": "小白",
      "type": "狗",
      "breed": "金毛"
    }
  ]
}
```

### 宠物 (Pet)
```json
{
  "id": 1,
  "name": "小白",
  "type": "狗",
  "breed": "金毛",
  "age": 3,
  "color": "金色",
  "weight": 25.5,
  "owner": {
    "id": 1,
    "firstName": "张",
    "lastName": "三"
  }
}
```

### 评价 (Review)
```json
{
  "id": 1,
  "rating": 5,
  "feedback": "服务非常好，医生很专业",
  "createdAt": "2024-12-20",
  "patient": {
    "id": 1,
    "firstName": "张",
    "lastName": "三"
  },
  "veterinarian": {
    "id": 2,
    "firstName": "李",
    "lastName": "医生"
  }
}
```

## 预约状态枚举

```java
public enum AppointmentStatus {
    CANCELLED,          // 已取消
    ON_GOING,           // 进行中
    UP_COMING,          // 即将到来
    APPROVED,           // 已批准
    NOT_APPROVED,       // 未批准
    WAITING_FOR_APPROVAL, // 等待批准
    PENDING,            // 待处理
    COMPLETED           // 已完成
}
```

## 错误响应格式

所有错误响应都遵循统一格式：

```json
{
  "message": "错误描述",
  "timestamp": "2024-12-20T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/appointments/book"
}
```

### 常见错误码

- `400 Bad Request`: 请求参数错误
- `401 Unauthorized`: 未认证或token无效
- `403 Forbidden`: 权限不足
- `404 Not Found`: 资源不存在
- `409 Conflict`: 资源冲突（如重复预约）
- `500 Internal Server Error`: 服务器内部错误

## 分页响应格式

对于返回列表的API，支持分页查询：

```http
GET /appointments/all?page=0&size=10&sort=createdAt,desc
```

**响应**:
```json
{
  "content": [...],
  "pageable": {
    "page": 0,
    "size": 10,
    "sort": "createdAt,desc"
  },
  "totalElements": 100,
  "totalPages": 10,
  "first": true,
  "last": false
}
```

## 安全注意事项

1. **Token 过期**: JWT token有过期时间，需要定期刷新
2. **权限检查**: 不同角色有不同的API访问权限
3. **数据验证**: 所有输入数据都会进行验证
4. **SQL注入防护**: 使用参数化查询防止SQL注入
5. **跨域请求**: 配置了CORS支持前端跨域访问

## 使用示例

### JavaScript/Axios 示例

```javascript
// 登录
const login = async (email, password) => {
  try {
    const response = await axios.post('/auth/login', {
      email,
      password
    });
    
    const token = response.data.data.token;
    localStorage.setItem('token', token);
    
    // 设置默认请求头
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    
    return response.data;
  } catch (error) {
    console.error('登录失败:', error.response.data.message);
  }
};

// 创建预约
const bookAppointment = async (appointmentData, senderId, recipientId) => {
  try {
    const response = await axios.post(
      `/appointments/book?senderId=${senderId}&recipientId=${recipientId}`,
      appointmentData
    );
    return response.data;
  } catch (error) {
    console.error('预约失败:', error.response.data.message);
  }
};
```

这个API文档涵盖了系统的所有主要功能接口，开发者可以根据此文档进行前端开发或第三方系统集成。