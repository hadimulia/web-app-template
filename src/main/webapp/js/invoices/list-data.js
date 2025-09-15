// Invoice Management JavaScript
class InvoiceSearchTable {
    constructor() {
        this.currentPage = 0;
        this.pageSize = 10;
        this.sortField = 'createdAt';
        this.sortDir = 'desc';
        this.filters = {};
        
        this.init();
    }
    
    init() {
        this.bindEvents();
        this.loadStatistics();
        this.loadData();
        this.loadCustomers();
        this.loadBillingGroups();
        
        // Set default dates for create form
        const today = new Date().toISOString().split('T')[0];
        $('#invoiceDate').val(today);
        
        const dueDate = new Date();
        dueDate.setDate(dueDate.getDate() + 30);
        $('#dueDate').val(dueDate.toISOString().split('T')[0]);
    }
    
    bindEvents() {
        // Filter form submission
        $('#filterForm').on('submit', (e) => {
            e.preventDefault();
            this.applyFilters();
        });
        
        // Generate form submission
        $('#generateForm').on('submit', (e) => {
            e.preventDefault();
            this.generateInvoice();
        });
        
        // Create form submission
        $('#createForm').on('submit', (e) => {
            e.preventDefault();
            this.createInvoice();
        });
        
        // Payment form submission
        $('#paymentForm').on('submit', (e) => {
            e.preventDefault();
            this.recordPayment();
        });
        
        // Customer change event for billing groups
        $('#generateCustomerId, #createCustomerId').on('change', (e) => {
            this.loadCustomerBillingGroups($(e.target).val(), $(e.target).attr('id').includes('generate') ? '#generateBillingGroupId' : '#createBillingGroupId');
        });
        
        // Period start change event
        $('#periodStart').on('change', (e) => {
            const startDate = new Date($(e.target).val());
            const endDate = new Date(startDate);
            endDate.setMonth(endDate.getMonth() + 1);
            endDate.setDate(endDate.getDate() - 1);
            $('#periodEnd').val(endDate.toISOString().split('T')[0]);
        });
    }
    
    loadData() {
        const params = new URLSearchParams({
            page: this.currentPage,
            size: this.pageSize,
            sortField: this.sortField,
            sortDir: this.sortDir,
            ...this.filters
        });
        
        // Remove empty filters
        for (const [key, value] of params.entries()) {
            if (!value || value === '') {
                params.delete(key);
            }
        }
        
        $.get(`/api/invoices?${params.toString()}`)
            .done((response) => {
                this.renderTable(response.content);
                this.renderPagination(response);
            })
            .fail((xhr) => {
                console.error('Failed to load invoices:', xhr);
                this.showError('Failed to load invoices');
            });
    }
    
    loadStatistics() {
        $.get('/api/invoices/statistics')
            .done((response) => {
                if (response.success && response.data) {
                    const stats = response.data;
                    $('#total-invoices').text(stats.total_invoices || 0);
                    $('#paid-invoices').text(stats.paid_invoices || 0);
                    $('#overdue-invoices').text(stats.overdue_invoices || 0);
                    $('#pending-amount').text('IDR ' + this.formatNumber(stats.total_outstanding || 0));
                }
            })
            .fail((xhr) => {
                console.error('Failed to load statistics:', xhr);
            });
    }
    
    loadCustomers() {
        $.get('/api/customers')
            .done((response) => {
                if (response.content && Array.isArray(response.content)) {
                    const customers = response.content;
                    const options = customers.map(customer => 
                        `<option value="${customer.id}">${customer.customerCode} - ${customer.companyName || customer.contactPerson}</option>`
                    ).join('');
                    
                    $('#generateCustomerId, #createCustomerId').append(options);
                }
            })
            .fail((xhr) => {
                console.error('Failed to load customers:', xhr);
            });
    }
    
    loadBillingGroups() {
        $.get('/api/billing-groups')
            .done((response) => {
                if (response.content && Array.isArray(response.content)) {
                    const billingGroups = response.content;
                    const options = billingGroups.map(bg => 
                        `<option value="${bg.id}">${bg.groupCode} - ${bg.groupName}</option>`
                    ).join('');
                    
                    $('#generateBillingGroupId, #createBillingGroupId').append(options);
                }
            })
            .fail((xhr) => {
                console.error('Failed to load billing groups:', xhr);
            });
    }
    
    loadCustomerBillingGroups(customerId, targetSelect) {
        if (!customerId) {
            $(targetSelect).empty().append('<option value="">Select Billing Group...</option>');
            return;
        }
        
        $.get(`/api/billing-groups/customer/${customerId}`)
            .done((response) => {
                $(targetSelect).empty().append('<option value="">Select Billing Group...</option>');
                
                if (response.success && response.data && Array.isArray(response.data)) {
                    const options = response.data.map(bg => 
                        `<option value="${bg.id || bg.billing_group_id}">${bg.group_code} - ${bg.group_name}</option>`
                    ).join('');
                    
                    $(targetSelect).append(options);
                }
            })
            .fail((xhr) => {
                console.error('Failed to load customer billing groups:', xhr);
                $(targetSelect).empty().append('<option value="">Select Billing Group...</option>');
            });
    }
    
    renderTable(invoices) {
        const tbody = $('#invoicesTableBody');
        tbody.empty();
        
        if (!invoices || invoices.length === 0) {
            tbody.append(`
                <tr>
                    <td colspan="9" class="text-center">No invoices found</td>
                </tr>
            `);
            return;
        }
        
        invoices.forEach(invoice => {
            const row = `
                <tr>
                    <td>
                        <a href="/invoices/${invoice.id}" class="text-primary font-weight-bold">
                            ${invoice.invoiceNumber}
                        </a>
                    </td>
                    <td>
                        <div class="text-sm">
                            <strong>${invoice.customer?.name || 'Unknown'}</strong><br>
                            <small class="text-muted">${invoice.customer?.email || ''}</small>
                        </div>
                    </td>
                    <td>${this.formatDate(invoice.invoiceDate)}</td>
                    <td>${this.formatDate(invoice.dueDate)}</td>
                    <td>
                        <div class="text-right">
                            <strong>${this.formatCurrency(invoice.totalAmount, invoice.currency)}</strong>
                            ${invoice.paidAmount > 0 ? `<br><small class="text-success">Paid: ${this.formatCurrency(invoice.paidAmount, invoice.currency)}</small>` : ''}
                        </div>
                    </td>
                    <td>
                        <span class="badge badge-${this.getStatusColor(invoice.status)} badge-pill">
                            ${this.getStatusText(invoice.status)}
                        </span>
                    </td>
                    <td>
                        <span class="badge badge-${this.getPaymentStatusColor(invoice.paymentStatus)} badge-pill">
                            ${this.getPaymentStatusText(invoice.paymentStatus)}
                        </span>
                    </td>
                    <td>
                        ${invoice.isOverdue ? `<span class="badge badge-danger">Overdue ${invoice.daysPastDue} days</span>` : '-'}
                    </td>
                    <td>
                        <div class="dropdown">
                            <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" 
                                    data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                Actions
                            </button>
                            <div class="dropdown-menu">
                                <a class="dropdown-item" href="/invoices/${invoice.id}">
                                    <i class="fas fa-eye"></i> View
                                </a>
                                <a class="dropdown-item" href="/invoices/${invoice.id}/edit">
                                    <i class="fas fa-edit"></i> Edit
                                </a>
                                ${invoice.status === 'PENDING' ? `
                                <a class="dropdown-item" href="#" onclick="invoiceTable.sendInvoice(${invoice.id})">
                                    <i class="fas fa-paper-plane"></i> Send
                                </a>
                                ` : ''}
                                ${invoice.paymentStatus !== 'PAID' ? `
                                <a class="dropdown-item" href="#" onclick="invoiceTable.openPaymentModal(${invoice.id})">
                                    <i class="fas fa-money-bill"></i> Record Payment
                                </a>
                                ` : ''}
                                <div class="dropdown-divider"></div>
                                <a class="dropdown-item text-danger" href="#" onclick="invoiceTable.deleteInvoice(${invoice.id})">
                                    <i class="fas fa-trash"></i> Delete
                                </a>
                            </div>
                        </div>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
    }
    
    renderPagination(response) {
        const pagination = $('#pagination');
        pagination.empty();
        
        if (response.totalPages <= 1) return;
        
        // Previous button
        pagination.append(`
            <li class="page-item ${response.currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="invoiceTable.goToPage(${response.currentPage - 1})">Previous</a>
            </li>
        `);
        
        // Page numbers
        const startPage = Math.max(0, response.currentPage - 2);
        const endPage = Math.min(response.totalPages - 1, response.currentPage + 2);
        
        for (let i = startPage; i <= endPage; i++) {
            pagination.append(`
                <li class="page-item ${i === response.currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="invoiceTable.goToPage(${i})">${i + 1}</a>
                </li>
            `);
        }
        
        // Next button
        pagination.append(`
            <li class="page-item ${response.currentPage >= response.totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="invoiceTable.goToPage(${response.currentPage + 1})">Next</a>
            </li>
        `);
    }
    
    applyFilters() {
        this.filters = {
            searchTerm: $('#searchTerm').val(),
            status: $('#status').val(),
            paymentStatus: $('#paymentStatus').val(),
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val()
        };
        
        this.currentPage = 0;
        this.loadData();
    }
    
    goToPage(page) {
        if (page < 0) return;
        this.currentPage = page;
        this.loadData();
    }
    
    generateInvoice() {
        const formData = {
            customerId: $('#generateCustomerId').val(),
            billingGroupId: $('#generateBillingGroupId').val(),
            periodStart: $('#periodStart').val(),
            periodEnd: $('#periodEnd').val()
        };
        
        $.post('/api/invoices/generate', formData)
            .done((response) => {
                if (response.success) {
                    $('#generateInvoiceModal').modal('hide');
                    this.showSuccess('Invoice generated successfully');
                    this.loadData();
                    this.loadStatistics();
                    $('#generateForm')[0].reset();
                } else {
                    this.showError(response.message || 'Failed to generate invoice');
                }
            })
            .fail((xhr) => {
                const response = xhr.responseJSON;
                this.showError(response?.message || 'Failed to generate invoice');
            });
    }
    
    createInvoice() {
        const formData = {
            customerId: $('#createCustomerId').val(),
            billingGroupId: $('#createBillingGroupId').val(),
            invoiceDate: $('#invoiceDate').val(),
            dueDate: $('#dueDate').val(),
            currency: $('#currency').val(),
            notes: $('#notes').val(),
            invoiceItems: [] // Empty for manual creation
        };
        
        $.ajax({
            url: '/api/invoices',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData)
        })
        .done((response) => {
            if (response.success) {
                $('#createInvoiceModal').modal('hide');
                this.showSuccess('Invoice created successfully');
                this.loadData();
                this.loadStatistics();
                $('#createForm')[0].reset();
                
                // Redirect to edit page to add items
                window.location.href = `/invoices/${response.data.id}/edit`;
            } else {
                this.showError(response.message || 'Failed to create invoice');
            }
        })
        .fail((xhr) => {
            const response = xhr.responseJSON;
            this.showError(response?.message || 'Failed to create invoice');
        });
    }
    
    sendInvoice(invoiceId) {
        if (!confirm('Are you sure you want to mark this invoice as sent?')) {
            return;
        }
        
        $.ajax({
            url: `/api/invoices/${invoiceId}/send`,
            method: 'PUT'
        })
        .done((response) => {
            if (response.success) {
                this.showSuccess('Invoice marked as sent');
                this.loadData();
            } else {
                this.showError(response.message || 'Failed to send invoice');
            }
        })
        .fail((xhr) => {
            const response = xhr.responseJSON;
            this.showError(response?.message || 'Failed to send invoice');
        });
    }
    
    openPaymentModal(invoiceId) {
        $.get(`/api/invoices/${invoiceId}`)
            .done((response) => {
                if (response.success && response.data) {
                    const invoice = response.data;
                    $('#paymentInvoiceId').val(invoice.id);
                    $('#paymentInvoiceNumber').val(invoice.invoiceNumber);
                    $('#paymentTotalAmount').val(this.formatCurrency(invoice.totalAmount, invoice.currency));
                    
                    const remaining = invoice.totalAmount - (invoice.paidAmount || 0);
                    $('#paymentRemainingAmount').val(this.formatCurrency(remaining, invoice.currency));
                    $('#paymentAmount').attr('max', remaining.toFixed(2));
                    
                    $('#recordPaymentModal').modal('show');
                } else {
                    this.showError('Failed to load invoice details');
                }
            })
            .fail((xhr) => {
                const response = xhr.responseJSON;
                this.showError(response?.message || 'Failed to load invoice');
            });
    }
    
    recordPayment() {
        const invoiceId = $('#paymentInvoiceId').val();
        const amount = parseFloat($('#paymentAmount').val());
        
        $.ajax({
            url: `/api/invoices/${invoiceId}/payment`,
            method: 'PUT',
            data: { amount: amount }
        })
        .done((response) => {
            if (response.success) {
                $('#recordPaymentModal').modal('hide');
                this.showSuccess('Payment recorded successfully');
                this.loadData();
                this.loadStatistics();
                $('#paymentForm')[0].reset();
            } else {
                this.showError(response.message || 'Failed to record payment');
            }
        })
        .fail((xhr) => {
            const response = xhr.responseJSON;
            this.showError(response?.message || 'Failed to record payment');
        });
    }
    
    deleteInvoice(invoiceId) {
        if (!confirm('Are you sure you want to delete this invoice? This action cannot be undone.')) {
            return;
        }
        
        $.ajax({
            url: `/api/invoices/${invoiceId}`,
            method: 'DELETE'
        })
        .done((response) => {
            if (response.success) {
                this.showSuccess('Invoice deleted successfully');
                this.loadData();
                this.loadStatistics();
            } else {
                this.showError(response.message || 'Failed to delete invoice');
            }
        })
        .fail((xhr) => {
            const response = xhr.responseJSON;
            this.showError(response?.message || 'Failed to delete invoice');
        });
    }
    
    // Utility methods
    formatDate(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString('id-ID');
    }
    
    formatCurrency(amount, currency = 'IDR') {
        if (!amount) return `${currency} 0`;
        return `${currency} ${this.formatNumber(amount)}`;
    }
    
    formatNumber(num) {
        return new Intl.NumberFormat('id-ID').format(num);
    }
    
    getStatusColor(status) {
        const colors = {
            'PENDING': 'warning',
            'SENT': 'info',
            'PAID': 'success',
            'OVERDUE': 'danger',
            'CANCELLED': 'secondary'
        };
        return colors[status] || 'secondary';
    }
    
    getStatusText(status) {
        const texts = {
            'PENDING': 'Pending',
            'SENT': 'Sent',
            'PAID': 'Paid',
            'OVERDUE': 'Overdue',
            'CANCELLED': 'Cancelled'
        };
        return texts[status] || status;
    }
    
    getPaymentStatusColor(paymentStatus) {
        const colors = {
            'UNPAID': 'danger',
            'PARTIAL': 'warning',
            'PAID': 'success'
        };
        return colors[paymentStatus] || 'secondary';
    }
    
    getPaymentStatusText(paymentStatus) {
        const texts = {
            'UNPAID': 'Unpaid',
            'PARTIAL': 'Partial',
            'PAID': 'Paid'
        };
        return texts[paymentStatus] || paymentStatus;
    }
    
    showSuccess(message) {
        // Use SweetAlert or basic alert
        if (typeof Swal !== 'undefined') {
            Swal.fire('Success', message, 'success');
        } else {
            alert(message);
        }
    }
    
    showError(message) {
        // Use SweetAlert or basic alert
        if (typeof Swal !== 'undefined') {
            Swal.fire('Error', message, 'error');
        } else {
            alert('Error: ' + message);
        }
    }
}

// Initialize when document is ready
$(document).ready(function() {
    window.invoiceTable = new InvoiceSearchTable();
});

// Global functions for onclick handlers
function openGenerateModal() {
    $('#generateInvoiceModal').modal('show');
}

function openCreateModal() {
    $('#createInvoiceModal').modal('show');
}

function applyFilters() {
    window.invoiceTable.applyFilters();
}
