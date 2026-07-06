# 软件测试实践教学管理平台

基于 SpringBoot + MyBatis-Plus + Vue + ElementUI 的全栈教学管理平台。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | SpringBoot 2.7.18 |
| ORM | MyBatis-Plus 3.5.3.1 |
| 数据库 | H2 (开发) / MySQL (生产) |
| 前端框架 | Vue 2.x |
| UI 组件库 | ElementUI 2.15 |
| 状态管理 | Vuex |
| 路由 | Vue Router |
| HTTP 客户端 | Axios |
| 认证 | JWT |

## 功能模块

### 教师端
- **实验任务管理** - 发布/编辑任务，上传测试用例模板、缺陷报告模板、环境配置文档
- **教学评价管理** - 查看提交列表、在线批改打分、填写评语
- **成绩统计分析** - 平均分/最高分/最低分统计，导出班级成绩Excel
- **教学资源管理** - 上传/分类/预览/下载课件和参考资料
- **论坛管理** - 管理帖子，置顶/设为FAQ/删除

### 学生端
- **查看实验任务** - 查看任务详情，下载模板和环境配置文档
- **在线提交作业** - 上传测试用例、缺陷报告、测试总结，截止日前可重复修改
- **自动化脚本** - 上传测试脚本，查看模拟执行日志和运行结果
- **论坛功能** - 发帖提问、查看FAQ、回复讨论

## 快速启动

### 1. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端运行在 http://localhost:8080
H2 控制台: http://localhost:8080/h2-console

### 2. 启动前端

```bash
cd frontend
npm install
npm run serve
```

前端运行在 

### 3. 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 教师 | teacher | 123456 |
| 学生1 | student1 | 123456 |
| 学生2 | student2 | 123456 |
| 学生3 | student3 | 123456 |

## 项目结构

```
projectone/
├── backend/                          # SpringBoot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/software/testing/
│       │   ├── TeachingPlatformApplication.java
│       │   ├── config/               # 配置类
│       │   ├── controller/           # 控制器
│       │   ├── service/              # 服务接口
│       │   │   └── impl/             # 服务实现
│       │   ├── mapper/               # MyBatis Mapper
│       │   ├── entity/               # 实体类
│       │   ├── dto/                  # 数据传输对象
│       │   ├── common/               # 通用类
│       │   └── util/                 # 工具类
│       └── resources/
│           ├── application.yml
│           ├── schema.sql            # 建表语句
│           └── data.sql              # 初始化数据
└── frontend/                         # Vue 前端
    ├── package.json
    ├── vue.config.js
    └── src/
        ├── main.js
        ├── App.vue
        ├── router/index.js           # 路由配置
        ├── store/index.js            # Vuex 状态管理
        ├── api/index.js              # API 接口
        ├── utils/request.js          # Axios 封装
        ├── layout/MainLayout.vue     # 主布局
        └── views/
            ├── common/               # 公共页面
            │   ├── Login.vue
            │   └── Dashboard.vue
            ├── teacher/              # 教师端页面
            │   ├── TaskManage.vue
            │   ├── SubmissionReview.vue
            │   ├── ScoreStatistics.vue
            │   ├── ResourceManage.vue
            │   └── ForumManage.vue
            └── student/              # 学生端页面
                ├── TaskList.vue
                ├── HomeworkSubmit.vue
                ├── AutoScript.vue
                ├── ResourceView.vue
                └── ForumView.vue
```
