package com.cxj.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知分发：借助 JDK 25 Switch Pattern Matching 实现清晰的策略路由。
 * <p>
 * sealed interface 让编译器强制枚举所有实现，避免遗漏。
 */
@Slf4j
@Service
public class NotificationDispatcher {

    public String dispatch(String recipient, NotificationChannel channel) {
        return switch (channel) {
            case NotificationChannel.Email(String subject, String body) ->
                    sendEmail(recipient, subject, body);
            case NotificationChannel.Sms(String template, String[] args) ->
                    sendSms(recipient, template, args);
            case NotificationChannel.Webhook(String url, String payload) ->
                    invokeWebhook(url, payload);
        };
    }

    private String sendEmail(String to, String subject, String body) {
        log.info("[email] to={}, subject={}", to, subject);
        return "email:" + to;
    }

    private String sendSms(String phone, String template, String[] args) {
        log.info("[sms] to={}, template={}, args={}", phone, template, String.join(",", args));
        return "sms:" + phone;
    }

    private String invokeWebhook(String url, String payload) {
        log.info("[webhook] url={}, payloadLen={}", url, payload == null ? 0 : payload.length());
        return "webhook:" + url;
    }
}
