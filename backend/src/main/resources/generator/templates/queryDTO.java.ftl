package ${basePackage}.${moduleName}.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ${table.comment!entity}查询条件
 *
 * @author ${author}
 */
@Schema(description = "${table.comment!entity}查询条件")
public record ${entity}QueryDTO(
        @Schema(description = "关键字") String keyword,
        @Schema(description = "状态") String status,
        @Schema(description = "页码", defaultValue = "1") Long current,
        @Schema(description = "每页数量", defaultValue = "20") Long size
) {
    public long safeCurrent() { return current == null || current < 1 ? 1 : current; }
    public long safeSize() { return size == null || size < 1 ? 20 : Math.min(size, 500); }
}
