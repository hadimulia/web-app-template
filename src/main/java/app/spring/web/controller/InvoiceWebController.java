package app.spring.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/invoices")
public class InvoiceWebController {
    
    @GetMapping
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("invoices/list");
        mav.addObject("pageTitle", "Invoice Management");
        return mav;
    }
    
    @GetMapping("/{id}")
    public ModelAndView view(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("invoices/view");
        mav.addObject("pageTitle", "View Invoice");
        mav.addObject("invoiceId", id);
        return mav;
    }
    
    @GetMapping("/{id}/edit")
    public ModelAndView edit(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("invoices/form");
        mav.addObject("pageTitle", "Edit Invoice");
        mav.addObject("invoiceId", id);
        mav.addObject("mode", "edit");
        return mav;
    }
    
    @GetMapping("/create")
    public ModelAndView create() {
        ModelAndView mav = new ModelAndView("invoices/form");
        mav.addObject("pageTitle", "Create Invoice");
        mav.addObject("mode", "create");
        return mav;
    }
}
