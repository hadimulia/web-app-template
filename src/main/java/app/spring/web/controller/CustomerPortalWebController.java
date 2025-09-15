package app.spring.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/portal")
public class CustomerPortalWebController {
    
    @GetMapping
    public ModelAndView index() {
        return new ModelAndView("redirect:/portal/login");
    }
    
    @GetMapping("/login")
    public ModelAndView login(HttpSession session) {
        // Redirect to dashboard if already logged in
        if (session.getAttribute("customerId") != null) {
            return new ModelAndView("redirect:/portal/dashboard");
        }
        
        ModelAndView mav = new ModelAndView("portal/login");
        mav.addObject("pageTitle", "Customer Login");
        return mav;
    }
    
    @GetMapping("/forgot-password")
    public ModelAndView forgotPassword() {
        ModelAndView mav = new ModelAndView("portal/forgot-password");
        mav.addObject("pageTitle", "Forgot Password");
        return mav;
    }
    
    @GetMapping("/reset-password")
    public ModelAndView resetPassword() {
        ModelAndView mav = new ModelAndView("portal/reset-password");
        mav.addObject("pageTitle", "Reset Password");
        return mav;
    }
    
    @GetMapping("/dashboard")
    public ModelAndView dashboard(HttpSession session) {
        // Check authentication
        if (session.getAttribute("customerId") == null) {
            return new ModelAndView("redirect:/portal/login");
        }
        
        ModelAndView mav = new ModelAndView("portal/dashboard");
        mav.addObject("pageTitle", "Customer Dashboard");
        mav.addObject("customerName", session.getAttribute("customerName"));
        return mav;
    }
    
    @GetMapping("/invoices")
    public ModelAndView invoices(HttpSession session) {
        // Check authentication
        if (session.getAttribute("customerId") == null) {
            return new ModelAndView("redirect:/portal/login");
        }
        
        ModelAndView mav = new ModelAndView("portal/invoices");
        mav.addObject("pageTitle", "My Invoices");
        mav.addObject("customerName", session.getAttribute("customerName"));
        return mav;
    }
    
    @GetMapping("/invoices/{id}")
    public ModelAndView invoiceDetail(@PathVariable Long id, HttpSession session) {
        // Check authentication
        if (session.getAttribute("customerId") == null) {
            return new ModelAndView("redirect:/portal/login");
        }
        
        ModelAndView mav = new ModelAndView("portal/invoice-detail");
        mav.addObject("pageTitle", "Invoice Details");
        mav.addObject("customerName", session.getAttribute("customerName"));
        mav.addObject("invoiceId", id);
        return mav;
    }
    
    @GetMapping("/profile")
    public ModelAndView profile(HttpSession session) {
        // Check authentication
        if (session.getAttribute("customerId") == null) {
            return new ModelAndView("redirect:/portal/login");
        }
        
        ModelAndView mav = new ModelAndView("portal/profile");
        mav.addObject("pageTitle", "My Profile");
        mav.addObject("customerName", session.getAttribute("customerName"));
        return mav;
    }
    
    @GetMapping("/payments")
    public ModelAndView payments(HttpSession session) {
        // Check authentication
        if (session.getAttribute("customerId") == null) {
            return new ModelAndView("redirect:/portal/login");
        }
        
        ModelAndView mav = new ModelAndView("portal/payments");
        mav.addObject("pageTitle", "Payment History");
        mav.addObject("customerName", session.getAttribute("customerName"));
        return mav;
    }
}
