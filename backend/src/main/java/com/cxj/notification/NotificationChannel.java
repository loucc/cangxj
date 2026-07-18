package com.cxj.notification;

/**
 * 通知渠道类型 —— 使用 sealed + record 便于 Switch 模式匹配
 */
public sealed interface NotificationChannel {

    record Email(String subject, String body) implements NotificationChannel {}

    record Sms(String template, String... args) implements NotificationChannel {}

    record Webhook(String url, String payload) implements NotificationChannel {}
}
