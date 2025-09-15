# Spring Boot Admin Template - Setup Guide

## Project Overview
A complete Spring Boot 2.7.18 admin panel with JSP, TkMyBatis 4.2.1, MySQL, and SB Admin template.

## Features Implemented
✅ User Management (CRUD)
✅ Role Management (CRUD) 
✅ Menu Management (CRUD)
✅ Login Authentication with Spring Security
✅ Session Management & Force Logout
✅ Dynamic Menu from Database (Tree Structure)
✅ Session Caching for User Details & Menus
✅ SB Admin Template Layout

## Technologies Used
- Spring Boot 2.7.18
- Spring Security
- TkMyBatis 4.2.1
- MySQL 8.0
- JSP with JSTL
- Bootstrap SB Admin Template
- Maven

## Setup Instructions

### 1. Database Setup
1. Create MySQL database:
```sql
CREATE DATABASE spring_admin_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Run the schema script:
```bash
mysql -u root -p spring_admin_db < src/main/resources/schema.sql
```

### 2. Application Configuration
Update `src/main/resources/application.properties`:
- Change database URL, username, password as needed
- Modify server port if required

### 3. Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

### 4. Access Application
- URL: http://localhost:8080/admin
- Username: admin
- Password: admin123

## Default Login Credentials
- **Username:** admin
- **Password:** admin123

## Project Structure
```
src/
├── main/
│   ├── java/app/spring/web/
│   │   ├── WebAppTemplateApplication.java
│   │   ├── config/SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── MainController.java
│   │   │   ├── UserController.java
│   │   │   ├── RoleController.java
│   │   │   └── MenuController.java
│   │   ├── mapper/
│   │   │   ├── UserMapper.java
│   │   │   ├── RoleMapper.java
│   │   │   ├── MenuMapper.java
│   │   │   ├── UserRoleMapper.java
│   │   │   └── RoleMenuMapper.java
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── Role.java
│   │   │   ├── Menu.java
│   │   │   ├── UserRole.java
│   │   │   └── RoleMenu.java
│   │   ├── security/
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── CustomUserPrincipal.java
│   │   │   └── LoginSuccessHandler.java
│   │   └── service/
│   │       ├── UserService.java
│   │       ├── RoleService.java
│   │       └── MenuService.java
│   ├── resources/
│   │   ├── application.properties
│   │   └── schema.sql
│   └── webapp/WEB-INF/views/
│       ├── layout/main.jsp
│       ├── login.jsp
│       ├── dashboard.jsp
│       ├── users/ (list.jsp, form.jsp, view.jsp)
│       ├── roles/ (list.jsp, form.jsp, view.jsp)
│       └── menus/ (list.jsp, form.jsp, view.jsp)
```

## Key Features Explained

### 1. Authentication & Security
- Spring Security with custom UserDetailsService
- Session-based authentication
- Password encryption with BCrypt
- Session timeout (30 minutes)
- Force logout functionality

### 2. Dynamic Menu System
- Tree-structured menus stored in database
- Parent-child relationships
- Menu permissions per role
- Cached in user session for performance

### 3. User Management
- Complete CRUD operations
- Role assignment
- User status management
- Password encryption

### 4. Role Management
- Role-based access control
- Menu assignment to roles
- Status management

### 5. Menu Management
- Hierarchical menu structure
- Icon support (FontAwesome)
- Sort ordering
- Menu type (Menu/Button)

## API Endpoints
- `/login` - Login page
- `/dashboard` - Main dashboard
- `/users` - User management
- `/roles` - Role management
- `/menus` - Menu management
- `/logout` - Logout
- `/force-logout` - Admin force logout

## Database Schema
The system uses 5 main tables:
- `sys_user` - User information
- `sys_role` - Role definitions
- `sys_menu` - Menu structure
- `sys_user_role` - User-Role relationships
- `sys_role_menu` - Role-Menu permissions

## Session Management
- User details cached in session
- Menu tree cached for performance
- 30-minute session timeout
- Force logout by admin

## Notes
- Default admin user created automatically
- Menu structure supports unlimited nesting
- All forms include validation
- Responsive design with SB Admin template
- DataTables for list views