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

-- CRM Menu Items
INSERT INTO sys_menu (menu_name, menu_code, menu_url, menu_icon, sort_order, menu_type) VALUES
('CRM & Billing', 'CRM', '#', 'fa-money-bill-wave', 4, 1),
('Customer Management', 'CRM_CUSTOMERS', '/customers', 'fa-users', 1, 1),
('Billing Groups', 'CRM_BILLING_GROUPS', '/billing-groups', 'fa-tags', 2, 1),
('Manage Billing Customers', 'CRM_BILLING_CUSTOMERS', '/billing-groups/manage-customers', 'fa-users-cog', 3, 1),
('Invoices', 'CRM_INVOICES', '/invoices', 'fa-file-invoice', 4, 1),
('Payments', 'CRM_PAYMENTS', '/payments', 'fa-credit-card', 5, 1),
('Payment Gateways', 'CRM_GATEWAYS', '/payment-gateways', 'fa-plug', 6, 1),
('Notifications', 'CRM_NOTIFICATIONS', '/notifications', 'fa-bell', 7, 1),
('CRM Reports', 'CRM_REPORTS', '#', 'fa-chart-line', 8, 1),
('Past Due Report', 'CRM_REPORT_PAST_DUE', '/reports/past-due', 'fa-exclamation-triangle', 1, 1),
('Customer Payment Report', 'CRM_REPORT_CUSTOMER_PAYMENT', '/reports/customer-payments', 'fa-money-check', 2, 1),
('Monthly Report', 'CRM_REPORT_MONTHLY', '/reports/monthly', 'fa-calendar', 3, 1),
('Income Report', 'CRM_REPORT_INCOME', '/reports/income', 'fa-chart-bar', 4, 1),
('Customer Portal', 'CUSTOMER_PORTAL', '#', 'fa-user-circle', 5, 1),
('Customer Login', 'CUSTOMER_LOGIN', '/customer/login', 'fa-sign-in-alt', 1, 1),
('Customer Dashboard', 'CUSTOMER_DASHBOARD', '/customer/dashboard', 'fa-tachometer-alt', 2, 1),
('Customer Billing History', 'CUSTOMER_BILLING', '/customer/billing', 'fa-receipt', 3, 1);

-- Set parent relationships for CRM submenus
UPDATE sys_menu SET parent_id = (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'CRM') temp) 
WHERE menu_code IN ('CRM_CUSTOMERS', 'CRM_BILLING_GROUPS', 'CRM_BILLING_CUSTOMERS', 'CRM_INVOICES', 'CRM_PAYMENTS', 'CRM_GATEWAYS', 'CRM_NOTIFICATIONS', 'CRM_REPORTS');

UPDATE sys_menu SET parent_id = (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'CRM_REPORTS') temp) 
WHERE menu_code IN ('CRM_REPORT_PAST_DUE', 'CRM_REPORT_CUSTOMER_PAYMENT', 'CRM_REPORT_MONTHLY', 'CRM_REPORT_INCOME');

UPDATE sys_menu SET parent_id = (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_code = 'CUSTOMER_PORTAL') temp) 
WHERE menu_code IN ('CUSTOMER_LOGIN', 'CUSTOMER_DASHBOARD', 'CUSTOMER_BILLING');

-- Assign all menus to admin role
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- ==============================================
-- CRM BILLING SYSTEM TABLES
-- ==============================================

-- Customer table
CREATE TABLE IF NOT EXISTS crm_customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_code VARCHAR(50) NOT NULL UNIQUE,
    company_name VARCHAR(255),
    contact_person VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'Indonesia',
    tax_id VARCHAR(50),
    customer_type VARCHAR(20) DEFAULT 'CORPORATE', -- CORPORATE, INDIVIDUAL
    status INT NOT NULL DEFAULT 1, -- 1=Active, 0=Inactive, 2=Suspended
    credit_limit DECIMAL(15,2) DEFAULT 0.00,
    payment_terms INT DEFAULT 30, -- Days
    customer_since DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- Customer login credentials (separate from admin users)
CREATE TABLE IF NOT EXISTS crm_customer_auth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    last_login TIMESTAMP,
    password_reset_token VARCHAR(100),
    password_reset_expires TIMESTAMP,
    email_verified BOOLEAN DEFAULT FALSE,
    email_verification_token VARCHAR(100),
    status INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES crm_customer(id) ON DELETE CASCADE
);

-- Billing groups for service categories
CREATE TABLE IF NOT EXISTS crm_billing_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_code VARCHAR(50) NOT NULL UNIQUE,
    group_name VARCHAR(100) NOT NULL,
    description TEXT,
    base_price DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'IDR',
    billing_cycle VARCHAR(20) DEFAULT 'MONTHLY', -- MONTHLY, QUARTERLY, YEARLY, ONE_TIME
    due_days INT DEFAULT 30, -- Days after billing date
    auto_generate BOOLEAN DEFAULT TRUE,
    tax_rate DECIMAL(5,2) DEFAULT 0.00, -- Percentage
    notes TEXT,
    status INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- Customer billing group assignments
CREATE TABLE IF NOT EXISTS crm_customer_billing_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    billing_group_id BIGINT NOT NULL,
    custom_price DECIMAL(15,2), -- Override group price if needed
    discount_percent DECIMAL(5,2) DEFAULT 0.00,
    start_date DATE NOT NULL,
    end_date DATE,
    status INT NOT NULL DEFAULT 1,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    FOREIGN KEY (customer_id) REFERENCES crm_customer(id) ON DELETE CASCADE,
    FOREIGN KEY (billing_group_id) REFERENCES crm_billing_group(id) ON DELETE CASCADE,
    UNIQUE(customer_id, billing_group_id)
);

-- Invoice/Bill generation
CREATE TABLE IF NOT EXISTS crm_invoice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    billing_group_id BIGINT NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'IDR',
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, SENT, PAID, OVERDUE, CANCELLED
    payment_status VARCHAR(20) DEFAULT 'UNPAID', -- UNPAID, PARTIAL, PAID
    paid_amount DECIMAL(15,2) DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    FOREIGN KEY (customer_id) REFERENCES crm_customer(id) ON DELETE CASCADE,
    FOREIGN KEY (billing_group_id) REFERENCES crm_billing_group(id)
);

-- Invoice line items
CREATE TABLE IF NOT EXISTS crm_invoice_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    item_description VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,2) DEFAULT 1.00,
    unit_price DECIMAL(15,2) NOT NULL,
    line_total DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES crm_invoice(id) ON DELETE CASCADE
);

-- Payment records
CREATE TABLE IF NOT EXISTS crm_payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_number VARCHAR(50) NOT NULL UNIQUE,
    invoice_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'IDR',
    payment_method VARCHAR(50), -- BANK_TRANSFER, CREDIT_CARD, CASH, CHECK, DIGITAL_WALLET
    reference_number VARCHAR(100),
    gateway_transaction_id VARCHAR(100),
    gateway_name VARCHAR(50), -- MIDTRANS, XENDIT, DOKU, etc.
    gateway_response TEXT,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, SUCCESS, FAILED, CANCELLED
    notes TEXT,
    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    FOREIGN KEY (invoice_id) REFERENCES crm_invoice(id),
    FOREIGN KEY (customer_id) REFERENCES crm_customer(id)
);

-- Payment gateway configuration
CREATE TABLE IF NOT EXISTS crm_payment_gateway (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gateway_name VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    gateway_type VARCHAR(20) NOT NULL, -- BANK, EWALLET, CARD, VA
    api_key VARCHAR(255),
    secret_key VARCHAR(255),
    merchant_id VARCHAR(100),
    environment VARCHAR(10) DEFAULT 'sandbox', -- sandbox, production
    webhook_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    supported_currencies VARCHAR(50) DEFAULT 'IDR',
    fee_percent DECIMAL(5,2) DEFAULT 0.00,
    fee_fixed DECIMAL(10,2) DEFAULT 0.00,
    config_json TEXT, -- Additional gateway-specific configuration
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notification templates
CREATE TABLE IF NOT EXISTS crm_notification_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_code VARCHAR(50) NOT NULL UNIQUE,
    template_name VARCHAR(100) NOT NULL,
    template_type VARCHAR(20) NOT NULL, -- EMAIL, SMS, PUSH
    event_trigger VARCHAR(50) NOT NULL, -- INVOICE_CREATED, PAYMENT_DUE, PAYMENT_RECEIVED, etc.
    subject VARCHAR(255),
    body_template TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- Payment transactions for gateway processing
CREATE TABLE IF NOT EXISTS crm_payment_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT,
    gateway_id BIGINT NOT NULL,
    external_transaction_id VARCHAR(255),
    gateway_reference VARCHAR(255),
    transaction_type VARCHAR(20) NOT NULL, -- PAYMENT, REFUND, CHARGEBACK
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, PROCESSING, SUCCESS, FAILED, CANCELLED, EXPIRED
    amount DECIMAL(15,2) NOT NULL,
    gateway_fee DECIMAL(10,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'IDR',
    redirect_url VARCHAR(500),
    callback_url VARCHAR(500),
    webhook_data TEXT,
    gateway_response TEXT,
    error_code VARCHAR(50),
    error_message TEXT,
    processed_at TIMESTAMP,
    expires_at TIMESTAMP,
    metadata_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES crm_payment(id),
    FOREIGN KEY (gateway_id) REFERENCES crm_payment_gateway(id)
);

-- Notification logs
CREATE TABLE IF NOT EXISTS crm_notification_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    template_id BIGINT NOT NULL,
    notification_type VARCHAR(20) NOT NULL, -- EMAIL, SMS, PUSH
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, SENT, FAILED, DELIVERED, READ
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    read_at TIMESTAMP,
    error_message TEXT,
    retry_count INT DEFAULT 0,
    metadata_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES crm_customer(id),
    FOREIGN KEY (template_id) REFERENCES crm_notification_template(id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_customer_email ON crm_customer(email);
CREATE INDEX IF NOT EXISTS idx_customer_status ON crm_customer(status);
CREATE INDEX IF NOT EXISTS idx_customer_code ON crm_customer(customer_code);
CREATE INDEX IF NOT EXISTS idx_customer_auth_username ON crm_customer_auth(username);
CREATE INDEX IF NOT EXISTS idx_customer_auth_customer_id ON crm_customer_auth(customer_id);
CREATE INDEX IF NOT EXISTS idx_billing_group_code ON crm_billing_group(group_code);
CREATE INDEX IF NOT EXISTS idx_invoice_customer_id ON crm_invoice(customer_id);
CREATE INDEX IF NOT EXISTS idx_invoice_number ON crm_invoice(invoice_number);
CREATE INDEX IF NOT EXISTS idx_invoice_status ON crm_invoice(status);
CREATE INDEX IF NOT EXISTS idx_invoice_due_date ON crm_invoice(due_date);
CREATE INDEX IF NOT EXISTS idx_payment_invoice_id ON crm_payment(invoice_id);
CREATE INDEX IF NOT EXISTS idx_payment_customer_id ON crm_payment(customer_id);
CREATE INDEX IF NOT EXISTS idx_payment_status ON crm_payment(status);
CREATE INDEX IF NOT EXISTS idx_payment_transaction_payment_id ON crm_payment_transaction(payment_id);
CREATE INDEX IF NOT EXISTS idx_payment_transaction_gateway_id ON crm_payment_transaction(gateway_id);
CREATE INDEX IF NOT EXISTS idx_payment_transaction_status ON crm_payment_transaction(status);
CREATE INDEX IF NOT EXISTS idx_payment_transaction_external_id ON crm_payment_transaction(external_transaction_id);
CREATE INDEX IF NOT EXISTS idx_notification_log_customer_id ON crm_notification_log(customer_id);
CREATE INDEX IF NOT EXISTS idx_notification_log_status ON crm_notification_log(status);

-- Insert sample billing groups
INSERT INTO crm_billing_group (group_code, group_name, description, base_price, billing_cycle, due_days) VALUES
('BASIC_HOSTING', 'Basic Hosting Package', 'Basic web hosting with 5GB storage', 150000.00, 'MONTHLY', 7),
('PRO_HOSTING', 'Professional Hosting Package', 'Professional hosting with 20GB storage and SSL', 350000.00, 'MONTHLY', 7),
('ENTERPRISE_HOSTING', 'Enterprise Hosting Package', 'Enterprise hosting with unlimited storage', 750000.00, 'MONTHLY', 14),
('DOMAIN_RENEWAL', 'Domain Renewal', 'Annual domain name renewal', 180000.00, 'YEARLY', 30),
('SSL_CERTIFICATE', 'SSL Certificate', 'Annual SSL certificate', 250000.00, 'YEARLY', 30),
('MAINTENANCE', 'Website Maintenance', 'Monthly website maintenance and updates', 500000.00, 'MONTHLY', 15);

-- Insert sample payment gateways
INSERT INTO crm_payment_gateway (gateway_name, display_name, gateway_type, is_active) VALUES
('MIDTRANS_SNAP', 'Midtrans Payment Gateway', 'CARD', TRUE),
('BANK_TRANSFER', 'Manual Bank Transfer', 'BANK', TRUE),
('GOPAY', 'GoPay Digital Wallet', 'EWALLET', TRUE),
('OVO', 'OVO Digital Wallet', 'EWALLET', TRUE),
('DANA', 'DANA Digital Wallet', 'EWALLET', TRUE);

-- Insert comprehensive notification templates
INSERT INTO crm_notification_template (template_code, template_name, template_type, event_trigger, subject, body_template, is_active) VALUES
-- Customer Registration Templates
('CUSTOMER_WELCOME_EMAIL', 'Customer Welcome Email', 'EMAIL', 'CUSTOMER_REGISTERED', 
 'Welcome to CRM System - {customer_name}', 
 'Dear {customer_name},\n\nWelcome to our CRM System! Your account has been successfully created.\n\nAccount Details:\n- Customer Code: {customer_code}\n- Company: {company_name}\n- Email: {email}\n- Member Since: {customer_since}\n\nWe are excited to serve you and help manage your business needs.\n\nIf you have any questions, please don''t hesitate to contact our support team.\n\nBest regards,\nCRM System Team', 
 TRUE),

('CUSTOMER_WELCOME_SMS', 'Customer Welcome SMS', 'SMS', 'CUSTOMER_REGISTERED',
 NULL,
 'Welcome {customer_name}! Your CRM account {customer_code} is ready. Email: {email}. Thank you for joining us!',
 TRUE),

-- Invoice Created Templates
('INVOICE_CREATED_EMAIL', 'Invoice Created Email', 'EMAIL', 'INVOICE_CREATED', 
 'New Invoice {invoice_number} - {total_amount}',
 'Dear {customer_name},\n\nA new invoice has been created for your account.\n\nInvoice Details:\n- Invoice Number: {invoice_number}\n- Invoice Date: {invoice_date}\n- Due Date: {due_date}\n- Total Amount: {total_amount}\n- Status: {status}\n\nPlease ensure payment is made by the due date to avoid any inconvenience.\n\nYou can view and pay your invoice online through our customer portal.\n\nThank you for your business!\n\nBest regards,\nAccounting Department\nCRM System',
 TRUE),

('INVOICE_CREATED_SMS', 'Invoice Created SMS', 'SMS', 'INVOICE_CREATED',
 NULL,
 'Hi {customer_name}, Invoice {invoice_number} created for {total_amount}. Due: {due_date}. Pay online or contact us. Thanks!',
 TRUE),

-- Payment Due Reminder Templates
('PAYMENT_DUE_EMAIL', 'Payment Due Reminder Email', 'EMAIL', 'PAYMENT_DUE',
 'Payment Reminder - Invoice {invoice_number} Due Soon',
 'Dear {customer_name},\n\nThis is a friendly reminder that your invoice payment is due soon.\n\nInvoice Details:\n- Invoice Number: {invoice_number}\n- Total Amount: {total_amount}\n- Due Date: {due_date}\n- Days Until Due: {days_until_due}\n\nTo avoid any late fees, please ensure your payment is processed before the due date.\n\nPayment Options:\n- Online payment through our portal\n- Bank transfer\n- Credit card payment\n- Mobile wallet (GoPay, OVO, DANA)\n\nIf you have any questions about this invoice, please contact our billing department.\n\nThank you for your prompt attention to this matter.\n\nBest regards,\nBilling Department\nCRM System',
 TRUE),

('PAYMENT_DUE_SMS', 'Payment Due Reminder SMS', 'SMS', 'PAYMENT_DUE',
 NULL,
 'Reminder: Invoice {invoice_number} ({total_amount}) due in {days_until_due} days on {due_date}. Pay now to avoid late fees.',
 TRUE),

-- Payment Overdue Templates
('PAYMENT_OVERDUE_EMAIL', 'Payment Overdue Email', 'EMAIL', 'PAYMENT_OVERDUE',
 'URGENT: Payment Overdue - Invoice {invoice_number}',
 'Dear {customer_name},\n\nYour invoice payment is now OVERDUE. Immediate action is required.\n\nInvoice Details:\n- Invoice Number: {invoice_number}\n- Total Amount: {total_amount}\n- Original Due Date: {due_date}\n- Days Overdue: {days_overdue}\n\nPLEASE PAY IMMEDIATELY to avoid:\n- Additional late fees\n- Service interruption\n- Collection procedures\n\nPayment Methods Available:\n- Online payment (fastest option)\n- Bank transfer\n- Credit card\n- Mobile wallets\n\nIf payment has already been made, please send proof of payment to our billing department.\n\nFor payment arrangements or questions, contact us immediately.\n\nUrgent action required!\n\nAccounting Department\nCRM System',
 TRUE),

('PAYMENT_OVERDUE_SMS', 'Payment Overdue SMS', 'SMS', 'PAYMENT_OVERDUE',
 NULL,
 'URGENT: Invoice {invoice_number} is {days_overdue} days overdue! Amount: {total_amount}. Pay now to avoid additional fees. Call us immediately!',
 TRUE),

-- Payment Received Templates
('PAYMENT_RECEIVED_EMAIL', 'Payment Received Email', 'EMAIL', 'PAYMENT_RECEIVED',
 'Payment Received - Invoice {invoice_number}',
 'Dear {customer_name},\n\nThank you! We have successfully received your payment.\n\nPayment Details:\n- Invoice Number: {invoice_number}\n- Payment Amount: {payment_amount}\n- Payment Date: {payment_date}\n- Payment Method: {payment_method}\n\nYour account has been updated and the invoice is now marked as paid.\n\nPayment Summary:\n- Original Amount: {total_amount}\n- Amount Paid: {payment_amount}\n- Invoice Status: PAID\n\nA receipt for this payment will be available in your customer portal.\n\nThank you for your business and prompt payment!\n\nBest regards,\nAccounting Department\nCRM System',
 TRUE),

('PAYMENT_RECEIVED_SMS', 'Payment Received SMS', 'SMS', 'PAYMENT_RECEIVED',
 NULL,
 'Payment received! Invoice {invoice_number} - {payment_amount} paid on {payment_date}. Thank you for your business!',
 TRUE),

-- Multi-language Templates (Indonesian)
('INVOICE_CREATED_EMAIL_ID', 'Email Invoice Dibuat (Indonesian)', 'EMAIL', 'INVOICE_CREATED',
 'Invoice Baru {invoice_number} - {total_amount}',
 'Yth. {customer_name},\n\nInvoice baru telah dibuat untuk akun Anda.\n\nDetail Invoice:\n- Nomor Invoice: {invoice_number}\n- Tanggal Invoice: {invoice_date}\n- Tanggal Jatuh Tempo: {due_date}\n- Total Amount: {total_amount}\n- Status: {status}\n\nMohon pastikan pembayaran dilakukan sebelum tanggal jatuh tempo.\n\nAnda dapat melihat dan membayar invoice secara online melalui portal pelanggan kami.\n\nTerima kasih atas kepercayaan Anda!\n\nSalam,\nDepartemen Akuntansi\nSistem CRM',
 TRUE),

('PAYMENT_RECEIVED_EMAIL_ID', 'Email Pembayaran Diterima (Indonesian)', 'EMAIL', 'PAYMENT_RECEIVED',
 'Pembayaran Diterima - Invoice {invoice_number}',
 'Yth. {customer_name},\n\nTerima kasih! Kami telah menerima pembayaran Anda.\n\nDetail Pembayaran:\n- Nomor Invoice: {invoice_number}\n- Jumlah Pembayaran: {payment_amount}\n- Tanggal Pembayaran: {payment_date}\n- Metode Pembayaran: {payment_method}\n\nAkun Anda telah diperbarui dan invoice telah ditandai sebagai lunas.\n\nRingkasan Pembayaran:\n- Jumlah Awal: {total_amount}\n- Jumlah Dibayar: {payment_amount}\n- Status Invoice: LUNAS\n\nTerima kasih atas kepercayaan dan pembayaran tepat waktu Anda!\n\nSalam,\nDepartemen Akuntansi\nSistem CRM',
 TRUE);

-- Insert sample customers
INSERT INTO crm_customer (customer_code, company_name, contact_person, email, phone, address, city, customer_type, customer_since) VALUES
('CUST001', 'PT. Teknologi Maju', 'John Doe', 'john@tekmaju.com', '081234567890', 'Jl. Sudirman No. 123', 'Jakarta', 'CORPORATE', '2024-01-15'),
('CUST002', 'CV. Digital Solutions', 'Jane Smith', 'jane@digitalsol.com', '081234567891', 'Jl. Gatot Subroto No. 456', 'Bandung', 'CORPORATE', '2024-02-20'),
('CUST003', 'Toko Online Sejahtera', 'Ahmad Rahman', 'ahmad@tokosejahtera.com', '081234567892', 'Jl. Malioboro No. 789', 'Yogyakarta', 'CORPORATE', '2024-03-10');

-- Create customer auth accounts
INSERT INTO crm_customer_auth (customer_id, username, password, email_verified) VALUES
(1, 'tekmaju', '$2a$10$Xi3CulDW4cJqg9HHsR1.zuf4XHcz1L1JNJmpDTdtltH.QKO26u8iy', TRUE), -- password: customer123
(2, 'digitalsol', '$2a$10$Xi3CulDW4cJqg9HHsR1.zuf4XHcz1L1JNJmpDTdtltH.QKO26u8iy', TRUE),
(3, 'tokosejahtera', '$2a$10$Xi3CulDW4cJqg9HHsR1.zuf4XHcz1L1JNJmpDTdtltH.QKO26u8iy', TRUE);