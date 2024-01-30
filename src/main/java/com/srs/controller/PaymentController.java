package com.srs.controller;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.srs.exception.OrderException;
import com.srs.model.Order;
import com.srs.repository.OrderRepository;
import com.srs.response.ApiResponse;
import com.srs.response.PaymentLinkResponse;
import com.srs.service.OrderService;
import com.srs.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Value("{razorpay.api.key}")
    String apiKey;

    @Value("{razorpay.api.secret}")
    String apiSecret;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/payments/{orderId}")
    public ResponseEntity<PaymentLinkResponse> createPaymentLink(@PathVariable Long orderId,
                                                                 @RequestHeader("Authorization") String jwt) throws OrderException, RazorpayException {
        Order order = orderService.findOrderById(orderId);

        try{
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
            JSONObject paymentLinkRequest = new JSONObject();

            paymentLinkRequest.put("amount", order.getTotalPrice()*100);
            paymentLinkRequest.put("currency", "INR");

            JSONObject customer = new JSONObject();
            customer.put("name", order.getUser().getFirstName());
            customer.put("email", order.getUser().getEmail());
            paymentLinkRequest.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("sms", true);
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);

            paymentLinkRequest.put("callback_url", "http://localhost:3000/payment/" + orderId);
            paymentLinkRequest.put("callback_method", "get");

            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);

            String paymentLinkId = paymentLink.get("id");
            String paymentLinkUrl = paymentLink.get("short_url");

            PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse();

            paymentLinkResponse.setPayment_link_id(paymentLinkId);
            paymentLinkResponse.setPayment_link_url(paymentLinkUrl);

            return new ResponseEntity<PaymentLinkResponse>(paymentLinkResponse, HttpStatus.CREATED);
        }catch (Exception e){
throw new RazorpayException(e.getMessage());
        }

    }

    public ResponseEntity<ApiResponse> redirect(@RequestParam(name = "payment_id") String paymentId,
                                                @RequestParam(name = "order_id") Long orderId) throws OrderException, RazorpayException {
        Order order = orderService.findOrderById(orderId);
        RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
        try {
            Payment payment = razorpayClient.payments.fetch(paymentId);
            if(payment.get("status").equals("captured")){
                order.getPaymentDetails().setPaymentId(paymentId);
                order.getPaymentDetails().setStatus("COMPLETED");
                order.setOrderStatus("PLACED");
                orderRepository.save(order);
            }
            ApiResponse response = new ApiResponse();
            response.setMessage("your order got placed");
            response.setStatus(true);
            return new ResponseEntity<ApiResponse>(HttpStatus.ACCEPTED);
        } catch (Exception e){
            throw  new RazorpayException(e.getMessage());
        }
    }
}
