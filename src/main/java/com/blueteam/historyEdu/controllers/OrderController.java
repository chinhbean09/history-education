package com.blueteam.historyEdu.controllers;

import com.blueteam.historyEdu.entities.ServicePackage;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.enums.PackageStatus;
import com.blueteam.historyEdu.repositories.IServicePackageRepository;
import com.blueteam.historyEdu.repositories.IUserRepository;
import com.blueteam.historyEdu.services.user.UserService;
import com.blueteam.historyEdu.type.CreatePaymentLinkRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/orders")

public class OrderController {
    private final PayOS payOS;
    private final UserService userService;
    private final IUserRepository userRepository;
   private final IServicePackageRepository servicePackageRepository;
    @PostMapping(value = "/success")
    public ObjectNode Success(@RequestParam Map<String, String> params) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            long orderId = Long.parseLong(params.get("orderId"));
            long packageId = Long.parseLong(params.get("packageId"));
            long userId = Long.parseLong(params.get("userId"));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            ServicePackage servicePackage = servicePackageRepository.findById(packageId)
                    .orElseThrow(() -> new IllegalArgumentException("Package not found"));

            PaymentLinkData order = payOS.getPaymentLinkInformation(orderId);

            if (!(order.getStatus().equals("PAID"))) {
                response.put("error", -1);
                response.put("message", "Chưa Thanh toán hoặc thanh toán lỗi");
                response.set("data", null);
                return response;
            }

            LocalDate now = LocalDate.now();

            if (order.getStatus().equals("PAID")) {
                user.setServicePackage(servicePackage);
                user.setPackageStartDate(now);
                user.setPackageEndDate(now.plusDays(30));
                user.setStatus(PackageStatus.ACTIVE);
                userRepository.save(user);
            }

            // Cập nhật trạng thái gói dịch vụ cho người dùng
            response.put("error", 0);
            response.put("message", "Thanh toán thành công gói");
            response.set("data", null);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }


    @PostMapping(value = "/cancel")
    public String Cancel(@RequestParam Map<String, String> params) {
        long orderId = Long.parseLong(params.get("orderId"));
        long packageId = Long.parseLong(params.get("packageId"));
        long userId = Long.parseLong(params.get("userId"));
        return "Thanh toán đã bị hủy!";
    }

    @PostMapping(path = "/create")
    public ObjectNode createPaymentLink(@RequestBody CreatePaymentLinkRequestBody RequestBody, HttpServletRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            String baseUrl = getBaseUrl(request);

            final String productName = RequestBody.getProductName();
            final String description = RequestBody.getDescription();
            String returnUrl = baseUrl + "/api/v1/orders/success?packageId=" + RequestBody.getPackageId() + "&userId=" + RequestBody.getUserId();
            String cancelUrl = baseUrl + "/api/v1/orders/cancel?packageId=" + RequestBody.getPackageId() + "&userId=" + RequestBody.getUserId();
            final int price = RequestBody.getPrice();
            String currentTimeString = String.valueOf(String.valueOf(new Date().getTime()));
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

            ItemData item = ItemData.builder().name(productName).price(price).quantity(1).build();

            PaymentData paymentData = PaymentData.builder().orderCode(orderCode).description(description).amount(price)
                    .item(item).returnUrl(returnUrl).cancelUrl(cancelUrl).build();

            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            response.put("error", 0);
            response.put("message", "success");
            response.set("data", objectMapper.valueToTree(data));
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", "fail");
            response.set("data", null);
            return response;

        }
    }

    @GetMapping(path = "/{orderId}")
    public ObjectNode getOrderById(@PathVariable("orderId") long orderId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            PaymentLinkData order = payOS.getPaymentLinkInformation(orderId);

            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }

    }

    @PutMapping(path = "/{orderId}")
    public ObjectNode cancelOrder(@PathVariable("orderId") int orderId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            PaymentLinkData order = payOS.cancelPaymentLink(orderId, null);
            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @PostMapping(path = "/confirm-webhook")
    public ObjectNode confirmWebhook(@RequestBody Map<String, String> requestBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            String str = payOS.confirmWebhook(requestBody.get("webhookUrl"));
            response.set("data", objectMapper.valueToTree(str));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
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
