package com.blueteam.historyEdu.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.node.ObjectNode;
import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

@RestController
@RequestMapping("${api.prefix}/payments")

public class PaymentController {
  private final PayOS payOS;

  public PaymentController(PayOS payOS) {
    this.payOS = payOS;
  }

  @PostMapping(path = "/payos_transfer_handler")
  public ObjectNode handleWebhook(@RequestBody ObjectNode body) {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode response = objectMapper.createObjectNode();

    try {
      Webhook webhook = objectMapper.treeToValue(body, Webhook.class);
      WebhookData data = payOS.verifyPaymentWebhookData(webhook);

      response.put("error", 0);
      response.put("message", "Webhook processed");
      response.set("data", objectMapper.valueToTree(data));
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      response.put("error", -1);
      response.put("message", e.getMessage());
      return response;
    }
  }
}
