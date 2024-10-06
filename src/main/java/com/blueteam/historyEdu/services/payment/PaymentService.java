package com.blueteam.historyEdu.services.payment;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Service
public class PaymentService {
    private final PayOS payOS;

    public PaymentService(PayOS payOS) {
        this.payOS = payOS;
    }

    public CheckoutResponseData createPayment(String productName, int price, String description, String returnUrl, String cancelUrl) throws Exception {
        // Gen order code
        String currentTimeString = String.valueOf(new Date().getTime());
        long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

        ItemData item = ItemData.builder().name(productName).price(price).quantity(1).build();

        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .description(description)
                .amount(price)
                .item(item)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .build();

        return payOS.createPaymentLink(paymentData);
    }
}
