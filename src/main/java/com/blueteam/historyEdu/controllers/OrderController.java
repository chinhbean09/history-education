package com.blueteam.historyEdu.controllers;

import com.blueteam.historyEdu.entities.Purchase;
import com.blueteam.historyEdu.entities.ServicePackage;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.enums.PackageStatus;
import com.blueteam.historyEdu.repositories.IPurchaseRepository;
import com.blueteam.historyEdu.repositories.IServicePackageRepository;
import com.blueteam.historyEdu.repositories.IUserRepository;
import com.blueteam.historyEdu.services.user.UserService;
import com.blueteam.historyEdu.type.CreatePaymentLinkRequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final PayOS payOS;
    private final UserService userService;
    private final IUserRepository userRepository;
    private final IServicePackageRepository servicePackageRepository;
    private final IPurchaseRepository purchaseRepository;

    @GetMapping(value = "/success")
    public RedirectView success(@RequestParam Map<String, String> params) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            long orderId = Long.parseLong(params.get("orderCode"));
            long packageId = Long.parseLong(params.get("packageId"));
            long userId = Long.parseLong(params.get("userId"));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            ServicePackage servicePackage = servicePackageRepository.findById(packageId)
                    .orElseThrow(() -> new IllegalArgumentException("Package not found"));

            PaymentLinkData order = payOS.getPaymentLinkInformation(orderId);

            if (!"PAID".equals(order.getStatus())) {
                response.put("error", -1);
                response.put("message", "Payment not completed or failed.");
                response.set("data", null);
                return new RedirectView("http://blueedu.info.vn/fail");
            }

            LocalDate now = LocalDate.now();
            LocalDate expiryDate = now.plusDays(servicePackage.getDuration());

            if ("PAID".equals(order.getStatus())) {
                // Create a Purchase record
                Purchase purchase = Purchase.builder()
                        .servicePackage(servicePackage)
                        .user(user)
                        .price(servicePackage.getPrice())
                        .orderCode(order.getOrderCode())
                        .packageStatus(PackageStatus.PAID)
                        .purchaseDate(now.atStartOfDay())
                        .expiryDate(expiryDate.atStartOfDay())
                        .createDate(now.atStartOfDay())
                        .build();
                purchaseRepository.save(purchase);

                user.setPackageStatus(PackageStatus.ACTIVE);
                userRepository.save(user);
            }

            response.put("error", 0);
            response.put("message", "Package successfully purchased.");
            response.set("data", null);
            return new RedirectView("http://blueedu.info.vn/success");

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return new RedirectView("http://blueedu.info.vn/fail");
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

    @GetMapping(value = "/cancel")
    public RedirectView cancel(@RequestParam Map<String, String> params) throws Exception {
        long packageId = Long.parseLong(params.get("packageId"));
        long userId = Long.parseLong(params.get("userId"));
        long orderId = Long.parseLong(params.get("orderCode"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ServicePackage servicePackage = servicePackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));

        LocalDate now = LocalDate.now();
        LocalDate expiryDate = now.plusDays(servicePackage.getDuration());
        PaymentLinkData order = payOS.getPaymentLinkInformation(orderId);
        if ("CANCELLED".equals(order.getStatus())) {
            Purchase purchase = Purchase.builder()
                    .servicePackage(servicePackage)
                    .user(user)
                    .orderCode(order.getOrderCode())
                    .price(servicePackage.getPrice())
                    .packageStatus(PackageStatus.CANCELLED)
                    .purchaseDate(now.atStartOfDay())
                    .expiryDate(expiryDate.atStartOfDay())
                    .createDate(now.atStartOfDay())
                    .build();
            purchaseRepository.save(purchase);

            user.setPackageStatus(PackageStatus.CANCELLED);

            //cancel in payos
            payOS.cancelPaymentLink(orderId, null);

            userRepository.save(user);
        }
        return new RedirectView("http://blueedu.info.vn/fail");
    }

    @PostMapping(path = "/create")
    public ObjectNode createPaymentLink(@RequestBody CreatePaymentLinkRequestBody requestBody, HttpServletRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        try {
            String baseUrl = getBaseUrl(request);

            final String productName = requestBody.getProductName();
            final String description = requestBody.getDescription();
            String returnUrl = baseUrl + "/api/v1/orders/success?packageId=" + requestBody.getPackageId() + "&userId=" + currentUser.getId();
            String cancelUrl = baseUrl + "/api/v1/orders/cancel?packageId=" + requestBody.getPackageId() + "&userId=" + currentUser.getId();
            final int price = requestBody.getPrice();
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
