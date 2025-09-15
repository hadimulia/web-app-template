/*!
    * Start Bootstrap - SB Admin v7.0.7 (https://startbootstrap.com/template/sb-admin)
    * Copyright 2013-2023 Start Bootstrap
    * Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-sb-admin/blob/master/LICENSE)
    */
    // 
// Scripts
// 

window.addEventListener('DOMContentLoaded', event => {

    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        // Uncomment Below to persist sidebar toggle between refreshes
        // if (localStorage.getItem('sb|sidebar-toggle') === 'true') {
        //     document.body.classList.toggle('sb-sidenav-toggled');
        // }
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }

    // Enhanced menu functionality
    initializeMenuSystem();

});

function initializeMenuSystem() {
    // Remove collapsed class from parent menus that have active children
    const activeSubMenus = document.querySelectorAll('.sb-sidenav-menu-nested .nav-link.active');
    activeSubMenus.forEach(activeSubMenu => {
        const parentCollapse = activeSubMenu.closest('.collapse');
        if (parentCollapse) {
            const parentMenuLink = document.querySelector(`[data-bs-target="#${parentCollapse.id}"]`);
            if (parentMenuLink) {
                parentMenuLink.classList.remove('collapsed');
                parentMenuLink.classList.add('active');
                parentMenuLink.setAttribute('aria-expanded', 'true');
                parentCollapse.classList.add('show');
            }
        }
    });

    // Add click handlers for menu items to update active states
    const menuLinks = document.querySelectorAll('.sb-sidenav .nav-link[href]:not([data-bs-toggle])');
    menuLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // Remove active class from all menu links
            const allMenuLinks = document.querySelectorAll('.sb-sidenav .nav-link');
            allMenuLinks.forEach(ml => ml.classList.remove('active'));
            
            // Add active class to clicked link
            this.classList.add('active');
            this.setAttribute('aria-current', 'page');
            
            // If this is a sub-menu item, ensure parent is expanded and active
            if (this.closest('.sb-sidenav-menu-nested')) {
                const parentCollapse = this.closest('.collapse');
                if (parentCollapse) {
                    const parentMenuLink = document.querySelector(`[data-bs-target="#${parentCollapse.id}"]`);
                    if (parentMenuLink) {
                        parentMenuLink.classList.add('active');
                        parentMenuLink.classList.remove('collapsed');
                        parentMenuLink.setAttribute('aria-expanded', 'true');
                        parentCollapse.classList.add('show');
                    }
                }
            }
        });
    });

    // Handle collapsible menu toggling
    const collapsibleMenus = document.querySelectorAll('.sb-sidenav [data-bs-toggle="collapse"]');
    collapsibleMenus.forEach(menu => {
        menu.addEventListener('click', function(e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('data-bs-target');
            const targetCollapse = document.querySelector(targetId);
            
            if (targetCollapse) {
                const isExpanded = targetCollapse.classList.contains('show');
                
                if (isExpanded) {
                    // Collapse the menu
                    targetCollapse.classList.remove('show');
                    this.classList.add('collapsed');
                    this.setAttribute('aria-expanded', 'false');
                    
                    // Remove active state from parent if no active children
                    const activeChildren = targetCollapse.querySelectorAll('.nav-link.active');
                    if (activeChildren.length === 0) {
                        this.classList.remove('active');
                    }
                } else {
                    // Expand the menu
                    targetCollapse.classList.add('show');
                    this.classList.remove('collapsed');
                    this.setAttribute('aria-expanded', 'true');
                }
            }
        });
    });

    // Persist menu state in localStorage
    persistMenuState();
}

function persistMenuState() {
    // Save expanded menu states
    const expandedMenus = document.querySelectorAll('.sb-sidenav .collapse.show');
    const expandedIds = Array.from(expandedMenus).map(menu => menu.id);
    localStorage.setItem('sb|expanded-menus', JSON.stringify(expandedIds));
    
    // Restore expanded menu states on load
    const savedExpandedMenus = localStorage.getItem('sb|expanded-menus');
    if (savedExpandedMenus) {
        try {
            const expandedMenuIds = JSON.parse(savedExpandedMenus);
            expandedMenuIds.forEach(menuId => {
                const menu = document.getElementById(menuId);
                const menuToggle = document.querySelector(`[data-bs-target="#${menuId}"]`);
                if (menu && menuToggle && !menu.classList.contains('show')) {
                    // Only expand if not already expanded by active state
                    const hasActiveChild = menu.querySelector('.nav-link.active');
                    if (!hasActiveChild) {
                        menu.classList.add('show');
                        menuToggle.classList.remove('collapsed');
                        menuToggle.setAttribute('aria-expanded', 'true');
                    }
                }
            });
        } catch (e) {
            console.warn('Failed to restore menu state:', e);
        }
    }
}
