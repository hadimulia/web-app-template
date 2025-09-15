/**
 * SearchTable - Generic search and pagination component
 * Provides async search and pagination functionality for any data table
 */
class SearchTable {
    constructor(options) {
        this.options = {
            id: '',
            apiUrl: '',
            searchUrl: null,
            pageSize: 10,
            sortBy: 'id',
            sortDir: 'asc',
            debounceDelay: 300,
            renderRow: null,
            ...options
        };
        
        this.currentPage = 1;
        this.currentSearch = '';
        this.currentSort = {
            column: this.options.sortBy,
            direction: this.options.sortDir
        };
        
        this.debounceTimer = null;
        this.isLoading = false;
        
        this.init();
    }
    
    init() {
        this.bindEvents();
        this.loadData();
    }
    
    bindEvents() {
        const id = this.options.id;
        
        // Search input
        $(`#${id}-search`).on('input', (e) => {
            this.debounceSearch(e.target.value);
        });
        
        // Search button
        $(`#${id}-search-btn`).on('click', () => {
            const searchValue = $(`#${id}-search`).val();
            this.performSearch(searchValue);
        });
        
        // Clear button
        $(`#${id}-clear-btn`).on('click', () => {
            $(`#${id}-search`).val('');
            this.performSearch('');
        });
        
        // Page size change
        $(`#${id}-page-size`).on('change', (e) => {
            this.options.pageSize = parseInt(e.target.value);
            this.currentPage = 1;
            this.loadData();
        });
        
        // Enter key on search
        $(`#${id}-search`).on('keypress', (e) => {
            if (e.which === 13) {
                const searchValue = e.target.value;
                this.performSearch(searchValue);
            }
        });
        
        // Sort headers (if sortable)
        $(`#${id}-table`).on('click', 'th[data-sortable="true"]', (e) => {
            const column = $(e.target).data('column');
            this.sortBy(column);
        });
    }
    
    debounceSearch(searchValue) {
        clearTimeout(this.debounceTimer);
        this.debounceTimer = setTimeout(() => {
            this.performSearch(searchValue);
        }, this.options.debounceDelay);
    }
    
    performSearch(searchValue) {
        this.currentSearch = searchValue;
        this.currentPage = 1;
        this.loadData();
    }
    
    sortBy(column) {
        if (this.currentSort.column === column) {
            this.currentSort.direction = this.currentSort.direction === 'asc' ? 'desc' : 'asc';
        } else {
            this.currentSort.column = column;
            this.currentSort.direction = 'asc';
        }
        this.currentPage = 1;
        this.loadData();
        this.updateSortHeaders();
    }
    
    updateSortHeaders() {
        const id = this.options.id;
        $(`#${id}-table th[data-sortable="true"]`).removeClass('sort-asc sort-desc');
        $(`#${id}-table th[data-column="${this.currentSort.column}"]`)
            .addClass(`sort-${this.currentSort.direction}`);
    }
    
    goToPage(page) {
        this.currentPage = page;
        this.loadData();
    }
    
    async loadData() {
        if (this.isLoading) return;
        
        this.isLoading = true;
        this.showLoading();
        
        try {
            const params = new URLSearchParams({
                page: this.currentPage,
                size: this.options.pageSize,
                sortBy: this.currentSort.column,
                sortDir: this.currentSort.direction,
                search: this.currentSearch
            });
            
            const response = await fetch(`${this.options.apiUrl}?${params}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            this.renderData(data);
            this.renderPagination(data);
            this.updateInfo(data);
            
        } catch (error) {
            console.error('Error loading data:', error);
            this.showError('Failed to load data. Please try again.');
        } finally {
            this.isLoading = false;
            this.hideLoading();
        }
    }
    
    renderData(pageResponse) {
        const id = this.options.id;
        const tbody = $(`#${id}-tbody`);
        tbody.empty();
        
        if (pageResponse.content.length === 0) {
            tbody.append(`
                <tr>
                    <td colspan="100%" class="text-center text-muted py-4">
                        ${this.currentSearch ? 'No results found for your search.' : 'No data available.'}
                    </td>
                </tr>
            `);
            return;
        }
        
        // This method should be overridden or provided as a callback
        if (this.options.renderRow) {
            pageResponse.content.forEach(item => {
                const row = this.options.renderRow(item);
                tbody.append(row);
            });
        } else {
            // Default rendering - you should customize this
            pageResponse.content.forEach(item => {
                const row = this.renderDefaultRow(item);
                tbody.append(row);
            });
        }
    }
    
    renderDefaultRow(item) {
        // Default row renderer - should be customized per table
        return `<tr><td colspan="100%">Customize renderRow method for proper display</td></tr>`;
    }
    
    renderPagination(pageResponse) {
        const id = this.options.id;
        const pagination = $(`#${id}-pagination`);
        pagination.empty();
        
        if (pageResponse.totalPages <= 1) {
            return;
        }
        
        const currentPage = pageResponse.page;
        const totalPages = pageResponse.totalPages;
        
        // Previous button
        const prevDisabled = currentPage === 1 ? 'disabled' : '';
        pagination.append(`
            <li class="page-item ${prevDisabled}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">
                    <i class="fas fa-chevron-left"></i>
                </a>
            </li>
        `);
        
        // Page numbers
        const startPage = Math.max(1, currentPage - 2);
        const endPage = Math.min(totalPages, currentPage + 2);
        
        if (startPage > 1) {
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="1">1</a></li>`);
            if (startPage > 2) {
                pagination.append(`<li class="page-item disabled"><span class="page-link">...</span></li>`);
            }
        }
        
        for (let i = startPage; i <= endPage; i++) {
            const active = i === currentPage ? 'active' : '';
            pagination.append(`
                <li class="page-item ${active}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>
            `);
        }
        
        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                pagination.append(`<li class="page-item disabled"><span class="page-link">...</span></li>`);
            }
            pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${totalPages}">${totalPages}</a></li>`);
        }
        
        // Next button
        const nextDisabled = currentPage === totalPages ? 'disabled' : '';
        pagination.append(`
            <li class="page-item ${nextDisabled}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">
                    <i class="fas fa-chevron-right"></i>
                </a>
            </li>
        `);
        
        // Bind pagination click events
        pagination.find('a[data-page]').on('click', (e) => {
            e.preventDefault();
            const page = parseInt($(e.target).closest('a').data('page'));
            if (page && page !== currentPage) {
                this.goToPage(page);
            }
        });
    }
    
    updateInfo(pageResponse) {
        const id = this.options.id;
        const start = (pageResponse.page - 1) * pageResponse.size + 1;
        const end = Math.min(start + pageResponse.size - 1, pageResponse.totalElements);
        
        $(`#${id}-info`).text(`Showing ${start} to ${end} of ${pageResponse.totalElements} entries`);
        $(`#${id}-showing`).text(`Showing ${start} to ${end} of ${pageResponse.totalElements} entries`);
    }
    
    showLoading() {
        const id = this.options.id;
        $(`#${id}-loading`).show();
        $(`#${id}-table`).css('opacity', '0.5');
    }
    
    hideLoading() {
        const id = this.options.id;
        $(`#${id}-loading`).hide();
        $(`#${id}-table`).css('opacity', '1');
    }
    
    showError(message) {
        const id = this.options.id;
        const alertHtml = `
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${message}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        `;
        $(`#${id}-messages`).html(alertHtml);
    }
    
    showSuccess(message) {
        const id = this.options.id;
        const alertHtml = `
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${message}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        `;
        $(`#${id}-messages`).html(alertHtml);
    }
    
    refresh() {
        this.loadData();
    }
    
    reset() {
        this.currentPage = 1;
        this.currentSearch = '';
        this.currentSort = {
            column: this.options.sortBy,
            direction: this.options.sortDir
        };
        $(`#${this.options.id}-search`).val('');
        this.loadData();
    }
}

// Export for global use
window.SearchTable = SearchTable;
