package com.cxj.common.response;

import com.cxj.common.enums.ResultCode;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.Instant;

/**
 * 统一响应包装（JDK Record，天然不可变）
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record R<T>(
        int code,
        String message,
        T data,
        Instant timestamp
) implements Serializable {

    public static <T> R<T> ok() {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null, Instant.now());
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, Instant.now());
    }

    public static <T> R<T> ok(T data, String message) {
        return new R<>(ResultCode.SUCCESS.getCode(), message, data, Instant.now());
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return new R<>(resultCode.getCode(), resultCode.getMessage(), null, Instant.now());
    }

    public static <T> R<T> fail(ResultCode resultCode, String message) {
        return new R<>(resultCode.getCode(), message, null, Instant.now());
    }

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null, Instant.now());
    }
}
