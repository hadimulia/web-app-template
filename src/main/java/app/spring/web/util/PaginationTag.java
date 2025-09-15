package app.spring.web.util;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class PaginationTag extends TagSupport {
    
    private String containerId;

    @Override
    public int doStartTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            
            // This tag is used internally by SearchTableTag
            // It generates the pagination structure that will be populated by JavaScript
            out.println("<div id=\"" + containerId + "-pagination-container\">");
            out.println("  <nav>");
            out.println("    <ul class=\"pagination justify-content-center\" id=\"" + containerId + "-pagination\">");
            out.println("    </ul>");
            out.println("  </nav>");
            out.println("</div>");
            
        } catch (IOException e) {
            throw new JspException("Error in PaginationTag", e);
        }
        
        return SKIP_BODY;
    }

    public String getContainerId() { 
        return containerId; 
    }
    
    public void setContainerId(String containerId) { 
        this.containerId = containerId; 
    }
}
