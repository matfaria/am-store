package br.com.les.amstore.service.serviceImpl;

import br.com.les.amstore.domain.Order;
import br.com.les.amstore.domain.Status;
import br.com.les.amstore.repository.CreditCards;
import br.com.les.amstore.repository.Customers;
import br.com.les.amstore.repository.Orders;
import br.com.les.amstore.repository.Statuses;
import br.com.les.amstore.service.IGenericService;
import br.com.les.amstore.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    Orders orders;

    @Autowired
    Statuses statuses;

    @Autowired
    CreditCards creditCards;

    @Autowired
    Customers customers;

    @Override
    public List<Order> findAll() {
        return orders.findAll();
    }

    @Override
    public Order findById(Long id) {
        return orders.findById(id).get();
    }

    @Override
    public Order saveAndFlush(Order object) {

        fillOrderObject(object);
        orders.saveAndFlush(object);

        object.getCustomer().getCart().getItemList().clear();

        customers.saveAndFlush(object.getCustomer());
        return object;
    }

    public void fillOrderObject(Order order){
        Status status = statuses.findByStatus("EM PROCESSAMENTO");
        order.setItemList(order.getCustomer().getCart().getItemList());
        Double costs = order.getShippingTax() + order.getItemList().stream().mapToDouble(i -> i.getGame().getPrice() * i.getAmount().doubleValue()).sum();

        order.getPaymentMethodList().forEach(p -> p.setCreditCard(creditCards.findById(p.getCreditCard().getId()).get()));
        order.setStatus(status);
        order.setTotal(costs - order.getCustomer().getWallet().getValue());

        if(null != order.getCoupon()){
            order.setTotal(costs - order.getCustomer().getWallet().getValue() - order.getCoupon().getValue());
            order.getCoupon().setAmount(order.getCoupon().getAmount() - 1);
        }

        order.getItemList().forEach(g -> g.getGame().setAmount(g.getGame().getAmount() - g.getAmount()));
    }

    @Override
    public Order updateOrder(Order order){
        return orders.saveAndFlush(order);
    }
}
