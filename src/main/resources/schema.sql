-- Spring Boot Admin Template Database Schema
-- H2 Database Schema

-- User table
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    phone VARCHAR(20),
    status INT NOT NULL DEFAULT 1,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_user_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_user_status ON sys_user(status);

-- Role table
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    status INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_role_code ON sys_role(role_code);
CREATE INDEX IF NOT EXISTS idx_role_status ON sys_role(status);

-- Menu table
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_name VARCHAR(50) NOT NULL,
    menu_code VARCHAR(50) NOT NULL UNIQUE,
    parent_id BIGINT,
    menu_url VARCHAR(255),
    menu_icon VARCHAR(50),
    sort_order INT DEFAULT 0,
    menu_type INT NOT NULL DEFAULT 1,
    status INT NOT NULL DEFAULT 1,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

CREATE INDEX IF NOT EXISTS idx_menu_parent_id ON sys_menu(parent_id);
CREATE INDEX IF NOT EXISTS idx_menu_code ON sys_menu(menu_code);
CREATE INDEX IF NOT EXISTS idx_menu_status ON sys_menu(status);
CREATE INDEX IF NOT EXISTS idx_menu_sort_order ON sys_menu(sort_order);

-- User-Role relationship table
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    UNIQUE(user_id, role_id)
);

-- Role-Menu relationship table
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    UNIQUE(role_id, menu_id)
);

-- Insert sample data
-- Default roles
INSERT INTO sys_role (role_name, role_code, description) VALUES
('Administrator', 'ADMIN', 'System administrator with full access'),
('Manager', 'MANAGER', 'Manager with limited administrative access'),
('User', 'USER', 'Regular user with basic access');

-- Default admin user (password: admin123)
INSERT INTO sys_user (username, password, full_name, email, status) VALUES
('admin', '$2a$10$Xi3CulDW4cJqg9HHsR1.zuf4XHcz1L1JNJmpDTdtltH.QKO26u8iy', 'System Administrator', 'admin@example.com', 1);

-- Assign admin role to admin user
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1);

-- Sample menus
INSERT INTO sys_menu (menu_name, menu_code, menu_url, menu_icon, sort_order, menu_type) VALUES
('Dashboard', 'DASHBOARD', '/dashboard', 'fa-tachometer-alt', 1, 1),
('System Management', 'SYSTEM', '#', 'fa-cogs', 2, 1),
('User Management', 'USER_MGMT', '/users', 'fa-users', 1, 1),
('Role Management', 'ROLE_MGMT', '/roles', 'fa-user-shield', 2, 1),
('Menu Management', 'MENU_MGMT', '/menus', 'fa-list', 3, 1),
('Reports', 'REPORTS', '#', 'fa-chart-bar', 3, 1),
('User Report', 'USER_REPORT', '/reports/users', 'fa-file-alt', 1, 1),
('Activity Log', 'ACTIVITY_LOG', '/reports/activity', 'fa-history', 2, 1);

-- Set parent relationships for submenus
UPDATE sys_menu SET parent_id = 2 WHERE menu_code IN ('USER_MGMT', 'ROLE_MGMT', 'MENU_MGMT');
UPDATE sys_menu SET parent_id = 6 WHERE menu_code IN ('USER_REPORT', 'ACTIVITY_LOG');

-- Assign all menus to admin role
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;