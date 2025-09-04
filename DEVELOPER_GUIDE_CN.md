# 开发者指南 - 宠物护理预约系统

## 开发环境搭建

### 系统要求
- **操作系统**: Windows 10+, macOS 10.15+, Ubuntu 18.04+
- **Java**: JDK 17 或更高版本
- **Node.js**: 16.x 或更高版本
- **MySQL**: 8.0 或更高版本
- **Docker**: 20.10+ (可选，用于容器化开发)
- **Git**: 2.x

### 开发工具推荐
- **后端IDE**: IntelliJ IDEA Ultimate 或 Visual Studio Code
- **前端IDE**: Visual Studio Code 或 WebStorm
- **数据库管理**: MySQL Workbench 或 DBeaver
- **API测试**: Postman 或 Insomnia
- **版本控制**: Git + GitHub Desktop

## 项目克隆和设置

### 1. 克隆项目
```bash
git clone https://github.com/2804622326/bookAppointmentSystem.git
cd bookAppointmentSystem
```

### 2. 数据库设置
#### 使用Docker (推荐)
```bash
# 启动MySQL容器
docker run --name pet-care-mysql \
  -e MYSQL_ROOT_PASSWORD=Liminghao2001 \
  -e MYSQL_DATABASE=pet-care-system \
  -p 3307:3306 \
  -d mysql:8
```

#### 手动安装MySQL
```sql
-- 创建数据库
CREATE DATABASE `pet-care-system` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户 (可选)
CREATE USER 'petcare'@'localhost' IDENTIFIED BY 'Liminghao2001';
GRANT ALL PRIVILEGES ON `pet-care-system`.* TO 'petcare'@'localhost';
FLUSH PRIVILEGES;
```

### 3. 后端设置

#### 环境配置
在 `backend/src/main/resources/` 目录下创建 `application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/pet-care-system
    username: root
    password: Liminghao2001
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

security:
  jwt:
    secret-key: mySecretKey123456789
    expiration-time: 86400000 # 24小时

app:
  base-url: http://localhost:3000
```

#### 启动后端
```bash
cd backend

# 使用Maven Wrapper
./mvnw clean install
./mvnw spring-boot:run -Dspring.profiles.active=dev

# 或使用系统安装的Maven
mvn clean install
mvn spring-boot:run -Dspring.profiles.active=dev
```

后端将在 `http://localhost:9192` 启动

### 4. 前端设置

#### 安装依赖
```bash
cd frontend
npm install
```

#### 环境配置
创建 `.env.local` 文件:
```env
VITE_API_BASE_URL=http://localhost:9192
VITE_APP_TITLE=宠物护理预约系统
```

#### 启动前端
```bash
npm run dev
```

前端将在 `http://localhost:5174` 启动 (Vite默认端口)

## 开发工作流

### 代码规范

#### 后端 (Java)
- 使用驼峰命名法 (camelCase)
- 类名使用帕斯卡命名法 (PascalCase)
- 常量使用大写字母和下划线
- 使用Lombok减少样板代码
- 遵循Spring Boot最佳实践

```java
// 示例：实体类
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String appointmentNo;
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
}
```

#### 前端 (React)
- 组件使用PascalCase命名
- 变量和函数使用camelCase
- 常量使用UPPER_SNAKE_CASE
- 使用ESLint和Prettier格式化代码

```javascript
// 示例：React组件
const AppointmentCard = ({ appointment, onStatusChange }) => {
  const [isLoading, setIsLoading] = useState(false);
  
  const handleStatusUpdate = async (newStatus) => {
    setIsLoading(true);
    try {
      await onStatusChange(appointment.id, newStatus);
    } catch (error) {
      console.error('状态更新失败:', error);
    } finally {
      setIsLoading(false);
    }
  };
  
  return (
    <Card>
      {/* 组件内容 */}
    </Card>
  );
};
```

### Git 工作流

#### 分支策略
```bash
# 主分支
main            # 生产代码
develop         # 开发主分支

# 功能分支
feature/xxx     # 新功能开发
bugfix/xxx      # Bug修复
hotfix/xxx      # 紧急修复
```

#### 提交规范
```bash
# 提交信息格式
<type>(<scope>): <description>

# 类型说明
feat:     新功能
fix:      Bug修复
docs:     文档更新
style:    代码格式（不影响功能）
refactor: 重构
test:     测试相关
chore:    构建过程或辅助工具的变动

# 示例
feat(appointment): 添加预约取消功能
fix(auth): 修复JWT过期问题
docs(api): 更新API文档
```

## 测试指南

### 后端测试

#### 单元测试
```java
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    
    @Mock
    private AppointmentRepository appointmentRepository;
    
    @InjectMocks
    private AppointmentService appointmentService;
    
    @Test
    void shouldCreateAppointmentSuccessfully() {
        // Given
        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setReason("常规检查");
        
        // When
        Appointment result = appointmentService.bookAppointment(request, 1L, 2L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReason()).isEqualTo("常规检查");
    }
}
```

#### 集成测试
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppointmentControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldBookAppointmentSuccessfully() {
        // 测试完整的预约流程
    }
}
```

#### 运行测试
```bash
# 运行所有测试
./mvnw test

# 运行特定测试类
./mvnw test -Dtest=AppointmentServiceTest

# 生成测试报告
./mvnw surefire-report:report
```

### 前端测试

#### 组件测试
```javascript
// AppointmentCard.test.jsx
import { render, screen, fireEvent } from '@testing-library/react';
import AppointmentCard from './AppointmentCard';

describe('AppointmentCard', () => {
  const mockAppointment = {
    id: 1,
    appointmentNo: 'APT-001',
    status: 'PENDING',
    reason: '常规检查'
  };

  test('应该渲染预约信息', () => {
    render(<AppointmentCard appointment={mockAppointment} />);
    
    expect(screen.getByText('APT-001')).toBeInTheDocument();
    expect(screen.getByText('常规检查')).toBeInTheDocument();
  });

  test('应该处理状态更新点击', () => {
    const mockOnStatusChange = jest.fn();
    render(
      <AppointmentCard 
        appointment={mockAppointment} 
        onStatusChange={mockOnStatusChange} 
      />
    );
    
    fireEvent.click(screen.getByRole('button', { name: /批准/i }));
    expect(mockOnStatusChange).toHaveBeenCalledWith(1, 'APPROVED');
  });
});
```

#### 运行测试
```bash
# 运行所有测试
npm test

# 运行测试并生成覆盖率报告
npm run test:coverage

# 监视模式（文件变化时自动运行）
npm test -- --watch
```

## 调试指南

### 后端调试

#### IntelliJ IDEA
1. 在IDE中设置断点
2. 以Debug模式运行Spring Boot应用
3. 使用调试控制台查看变量值

#### 日志调试
```java
// 添加日志
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AppointmentService {
    
    public Appointment bookAppointment(BookAppointmentRequest request, Long senderId, Long recipientId) {
        log.info("开始创建预约: senderId={}, recipientId={}", senderId, recipientId);
        
        try {
            // 业务逻辑
            log.debug("预约创建成功: {}", appointment.getAppointmentNo());
            return appointment;
        } catch (Exception e) {
            log.error("预约创建失败", e);
            throw e;
        }
    }
}
```

### 前端调试

#### Chrome DevTools
1. 打开开发者工具 (F12)
2. 在Sources标签页设置断点
3. 使用Console查看变量和执行代码

#### React Developer Tools
安装Chrome扩展，查看组件状态和props：
```javascript
// 在组件中添加调试信息
const AppointmentCard = ({ appointment }) => {
  // 开发环境下的调试日志
  if (process.env.NODE_ENV === 'development') {
    console.log('AppointmentCard props:', { appointment });
  }
  
  return <Card>...</Card>;
};
```

## 性能优化

### 后端优化

#### 数据库查询优化
```java
// 使用@Query注解优化查询
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.patient " +
           "JOIN FETCH a.veterinarian " +
           "WHERE a.patient.id = :patientId")
    List<Appointment> findByPatientIdWithDetails(@Param("patientId") Long patientId);
}
```

#### 缓存配置
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }
    
    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(5, TimeUnit.MINUTES);
    }
}

// 在服务层使用缓存
@Service
public class VeterinarianService {
    
    @Cacheable("veterinarians")
    public List<Veterinarian> getAllVeterinarians() {
        return veterinarianRepository.findAll();
    }
}
```

### 前端优化

#### 组件懒加载
```javascript
// 使用React.lazy进行代码分割
const AdminDashboard = lazy(() => import('./components/admin/AdminDashboard'));
const UserDashboard = lazy(() => import('./components/user/UserDashboard'));

function App() {
  return (
    <Router>
      <Suspense fallback={<div>加载中...</div>}>
        <Routes>
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/user" element={<UserDashboard />} />
        </Routes>
      </Suspense>
    </Router>
  );
}
```

#### 状态管理优化
```javascript
// 使用useMemo和useCallback避免不必要的重渲染
const AppointmentList = ({ appointments, onStatusChange }) => {
  const filteredAppointments = useMemo(() => {
    return appointments.filter(apt => apt.status === 'PENDING');
  }, [appointments]);
  
  const handleStatusChange = useCallback((id, status) => {
    onStatusChange(id, status);
  }, [onStatusChange]);
  
  return (
    <div>
      {filteredAppointments.map(appointment => (
        <AppointmentCard 
          key={appointment.id}
          appointment={appointment}
          onStatusChange={handleStatusChange}
        />
      ))}
    </div>
  );
};
```

## 部署指南

### Docker部署 (推荐)

#### 本地开发环境
```bash
# 构建并启动所有服务
docker-compose up --build

# 后台运行
docker-compose up -d --build

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

#### 生产环境配置
```yaml
# docker-compose.prod.yml
version: "3.8"
services:
  db:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: pet-care-system
    volumes:
      - mysql_data:/var/lib/mysql
    restart: always

  backend:
    build: 
      context: ./backend
      dockerfile: Dockerfile.prod
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/pet-care-system
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    restart: always

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile.prod
    restart: always

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/ssl
    restart: always

volumes:
  mysql_data:
```

### 传统部署

#### 后端部署
```bash
# 构建jar包
./mvnw clean package -DskipTests

# 运行应用
java -jar target/universal-pet-care-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --server.port=9192
```

#### 前端部署
```bash
# 构建生产版本
npm run build

# 使用nginx或其他web服务器部署dist目录
```

## 故障排除

### 常见问题

#### 1. 数据库连接失败
```
错误: Communications link failure
解决: 检查MySQL服务是否启动，端口是否正确
```

#### 2. JWT token过期
```
错误: 401 Unauthorized
解决: 前端需要处理token刷新或重新登录
```

#### 3. 跨域问题
```
错误: CORS policy blocked
解决: 后端配置CORS或使用代理
```

#### 4. 前端构建失败
```
错误: npm ERR! peer dep missing
解决: 删除node_modules重新安装依赖
```

### 调试命令

```bash
# 查看应用日志
docker-compose logs backend
docker-compose logs frontend

# 进入容器调试
docker-compose exec backend bash
docker-compose exec db mysql -u root -p

# 检查网络连接
docker-compose exec backend ping db
curl http://localhost:9192/actuator/health
```

## 贡献指南

### 代码贡献流程
1. Fork项目到个人仓库
2. 创建功能分支: `git checkout -b feature/my-feature`
3. 提交更改: `git commit -m 'feat: 添加新功能'`
4. 推送分支: `git push origin feature/my-feature`
5. 创建Pull Request

### 代码审查标准
- 代码符合项目规范
- 包含相应的测试
- 文档已更新
- 所有测试通过
- 性能影响评估

### 问题报告
提交Issue时请包含：
- 问题描述
- 复现步骤
- 期望行为
- 实际行为
- 环境信息 (OS, Java版本, Node版本等)

这个开发者指南提供了从环境搭建到部署的完整流程，帮助新开发者快速上手项目开发。