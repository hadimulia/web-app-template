package app.spring.web.util;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

public class SearchTableTag extends BodyTagSupport {
    
    private String id;
    private String apiUrl;
    private String searchUrl;
    private String title = "Data List";
    private String createUrl;
    private String createLabel = "Add New";
    private int pageSize = 10;
    private String sortBy = "id";
    private String sortDir = "asc";

    @Override
    public int doStartTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            
            // Generate the search and table container
            out.println("<div class=\"search-table-container\" id=\"" + id + "-container\">");
            
            // Header with title and create button
            out.println("<div class=\"d-sm-flex align-items-center justify-content-between mb-4\">");
            out.println("  <h1 class=\"h3 mb-0 text-gray-800\">" + title + "</h1>");
            
           
            out.println("</div>");
            
            // Messages container
            out.println("<div id=\"" + id + "-messages\"></div>");
            
            // Search and filters
            out.println("<div class=\"card shadow mb-4\">");
            out.println("  <div class=\"card-header py-3\">");
            out.println("    <div class=\"row\">");
            out.println("      <div class=\"col-md-6\">");
            
             if (createUrl != null && !createUrl.trim().isEmpty()) {
                out.println("  <a href=\"" + createUrl + "\" class=\"d-none d-sm-inline-block btn btn-sm btn-primary shadow-sm\">");
                out.println("    <i class=\"fas fa-plus fa-sm text-white-50\"></i> " + createLabel);
                out.println("  </a>");
            }
            out.println("      </div>");
            out.println("      <div class=\"col-md-6\">");
            out.println("        <div class=\"input-group\">");
            out.println("          <input type=\"text\" class=\"form-control\" id=\"" + id + "-search\" placeholder=\"Search...\">");
            out.println("          <div class=\"input-group-append\">");
            out.println("            <button class=\"btn btn-outline-secondary\" type=\"button\" id=\"" + id + "-search-btn\">");
            out.println("              <i class=\"fas fa-search\"></i>");
            out.println("            </button>");
            out.println("            <button class=\"btn btn-outline-secondary\" type=\"button\" id=\"" + id + "-clear-btn\">");
            out.println("              <i class=\"fas fa-times\"></i>");
            out.println("            </button>");
            out.println("          </div>");
            out.println("        </div>");
            out.println("      </div>");
            out.println("    </div>");
            out.println("  </div>");
            
            // Table container
            out.println("  <div class=\"card-body\">");
            out.println("    <div class=\"row mb-3\">");
            out.println("      <div class=\"col-md-6\">");
            out.println("        <div class=\"form-group\">");
            out.println("          <label for=\"" + id + "-page-size\">Show:</label>");
            out.println("          <select class=\"form-control form-control-sm d-inline-block w-auto\" id=\"" + id + "-page-size\">");
            out.println("            <option value=\"5\">5</option>");
            out.println("            <option value=\"10\" selected>10</option>");
            out.println("            <option value=\"25\">25</option>");
            out.println("            <option value=\"50\">50</option>");
            out.println("            <option value=\"100\">100</option>");
            out.println("          </select>");
            out.println("          <span class=\"ml-2\">entries</span>");
            out.println("        </div>");
            out.println("      </div>");
            out.println("      <div class=\"col-md-6 text-right\">");
            out.println("        <div id=\"" + id + "-info\" class=\"text-muted\"></div>");
            out.println("      </div>");
            out.println("    </div>");
            
            out.println("    <div class=\"table-responsive\">");
            out.println("      <div id=\"" + id + "-loading\" class=\"text-center p-4\" style=\"display: none;\">");
            out.println("        <i class=\"fas fa-spinner fa-spin\"></i> Loading...");
            out.println("      </div>");
            out.println("      <table class=\"table table-bordered\" id=\"" + id + "-table\" width=\"100%\" cellspacing=\"0\">");
            out.println("        <thead>");
            
        } catch (IOException e) {
            throw new JspException("Error in SearchTableTag", e);
        }
        
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            
            // Close table structure
            out.println("        </thead>");
            out.println("        <tbody id=\"" + id + "-tbody\">");
            out.println("        </tbody>");
            out.println("      </table>");
            out.println("    </div>");
            
            // Pagination controls
            out.println("    <div class=\"row mt-3\">");
            out.println("      <div class=\"col-md-6\">");
            out.println("        <div id=\"" + id + "-showing\" class=\"text-muted\"></div>");
            out.println("      </div>");
            out.println("      <div class=\"col-md-6\">");
            out.println("        <nav>");
            out.println("          <ul class=\"pagination justify-content-end\" id=\"" + id + "-pagination\">");
            out.println("          </ul>");
            out.println("        </nav>");
            out.println("      </div>");
            out.println("    </div>");
            
            out.println("  </div>"); // Close card-body
            out.println("</div>"); // Close card
            out.println("</div>"); // Close container
            
            // Generate JavaScript configuration as data attributes instead of inline script
            out.println("<script type=\"application/json\" id=\"" + id + "-config\">");
            out.println("{");
            out.println("  \"id\": \"" + id + "\",");
            out.println("  \"apiUrl\": \"" + apiUrl + "\",");
            if (searchUrl != null) {
                out.println("  \"searchUrl\": \"" + searchUrl + "\",");
            }
            out.println("  \"pageSize\": " + pageSize + ",");
            out.println("  \"sortBy\": \"" + sortBy + "\",");
            out.println("  \"sortDir\": \"" + sortDir + "\"");
            out.println("}");
            out.println("</script>");
            
        } catch (IOException e) {
            throw new JspException("Error in SearchTableTag", e);
        }
        
        return EVAL_PAGE;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
    
    public String getSearchUrl() { return searchUrl; }
    public void setSearchUrl(String searchUrl) { this.searchUrl = searchUrl; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getCreateUrl() { return createUrl; }
    public void setCreateUrl(String createUrl) { this.createUrl = createUrl; }
    
    public String getCreateLabel() { return createLabel; }
    public void setCreateLabel(String createLabel) { this.createLabel = createLabel; }
    
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}
