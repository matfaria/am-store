package br.com.les.amstore.controller;

import br.com.les.amstore.domain.Coupon;
import br.com.les.amstore.domain.Customer;
import br.com.les.amstore.domain.Order;
import br.com.les.amstore.domain.Status;
import br.com.les.amstore.dto.CouponDTO;
import br.com.les.amstore.service.ICustomerTypeService;
import br.com.les.amstore.service.ICustomersService;
import br.com.les.amstore.service.IGenericService;
import br.com.les.amstore.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ICustomersService customers;

    @Autowired
    private ICustomerTypeService customerTypes;

    @Autowired
    private IGenericService<Coupon> couponService;

    @Autowired
    private IGenericService<Order> orderService;

    @Autowired
    private IOrderService orderServiceExclusive;

    @Autowired
    private IGenericService<Status> statusService;

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("/admin/index");
        return mv;
    }

    @DeleteMapping("/customer/list")
    public ModelAndView deleteCustomer(@RequestParam String id, RedirectAttributes attributes) {
        ModelAndView mv = new ModelAndView("redirect:/admin/customer/list");

        Customer customer = customers.findById(Long.parseLong(id));
        System.err.println(customer.getId());
        customer.delete();

        attributes.addFlashAttribute("message", "Usuário excluído com sucesso!");
        customers.saveAndFlush(customer);

        return mv;
    }

    @GetMapping("/customer/list")
    public ModelAndView listCustomer() {
        ModelAndView mv = new ModelAndView("/admin/listCustomer");
        mv.addObject("customers", customers.findAll());
        mv.addObject("customerTypes", customerTypes.findAll());
        return mv;
    }

    @GetMapping("/order_return/list")
    public ModelAndView listOrdersReturned() {
        ModelAndView mv = new ModelAndView("/admin/listReturnedOrders");

        return mv;
    }

    @GetMapping("/order_return/{id}/edit")
    public ModelAndView updateOrderReturned() {
        ModelAndView mv = new ModelAndView("/admin/updateOrderReturned");

        return mv;
    }

    @GetMapping("/orders")
    public ModelAndView listOrders() {
        ModelAndView mv = new ModelAndView("/admin/listOrders");
        mv.addObject("orders", orderService.findAll());
        mv.addObject("statuses", statusService.findAll());

        return mv;
    }

    @GetMapping("/coupons")
    public ModelAndView listCoupons() {
        ModelAndView mv = new ModelAndView("/admin/listCoupons");

        mv.addObject("coupons", couponService.findAll());

        return mv;
    }

    @GetMapping("/coupons/new")
    public ModelAndView newCoupons(Coupon coupon) {
        ModelAndView mv = new ModelAndView("/admin/newCoupom");
        mv.addObject(coupon);

        return mv;
    }

    @PostMapping("/coupons/new")
    public ModelAndView createCoupons(@Valid Coupon coupon, BindingResult result, RedirectAttributes attributes) {
        if(result.hasErrors()){
            return newCoupons(coupon);
        }

        couponService.saveAndFlush(coupon);

        ModelAndView mv = new ModelAndView("/admin/newCoupom");
        mv.addObject(coupon);

        return mv;
    }

    @PostMapping("/orders/changestatusorder")
    public ResponseEntity changeStatusOrder(@RequestParam("statusId") Long statusId, Long orderId) {
        Order order = orderService.findById(orderId);
        Status status = statusService.findById(statusId);

        if(order != null && status != null)
            order.setStatus(status);

        orderServiceExclusive.updateOrder(order);

        return ResponseEntity.ok("alterado com sucesso");
    }
}
