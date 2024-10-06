package com.blueteam.historyEdu.controllers;

import com.blueteam.historyEdu.services.payment.PaymentService;
import com.blueteam.historyEdu.type.CreatePaymentLinkRequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.util.Date;

@RestController
@RequestMapping("${api.prefix}/checkouts")
public class CheckoutController {

    private final PaymentService paymentService;

    public CheckoutController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/create-payment-link", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void checkout(@RequestBody CreatePaymentLinkRequestBody requestBody, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        try {
            String baseUrl = getBaseUrl(request);

            String productName = requestBody.getProductName();
            String description = requestBody.getDescription();
            int price = requestBody.getPrice();
            String returnUrl = baseUrl + "/success";
            String cancelUrl = baseUrl + "/cancel";

            var data = paymentService.createPayment(productName, price, description, returnUrl, cancelUrl);

            String checkoutUrl = data.getCheckoutUrl();
            httpServletResponse.setHeader("Location", checkoutUrl);
            httpServletResponse.setStatus(302);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        String url = scheme + "://" + serverName;
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            url += ":" + serverPort;
        }
        url += contextPath;
        return url;
    }
}
