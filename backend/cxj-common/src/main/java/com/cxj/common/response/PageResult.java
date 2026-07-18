package com.cxj.common.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * 分页响应
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageResult<T>(
        long total,
        long size,
        long current,
        long pages,
        List<T> records
) implements Serializable {

    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getTotal(), page.getSize(), page.getCurrent(), page.getPages(), page.getRecords());
    }

    public static <E, T> PageResult<T> of(IPage<E> page, Function<E, T> converter) {
        List<T> records = page.getRecords().stream().map(converter).toList();
        return new PageResult<>(page.getTotal(), page.getSize(), page.getCurrent(), page.getPages(), records);
    }
}
