package ${basePackage}.${moduleName}.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * ${table.comment!entity}创建入参
 *
 * @author ${author}
 */
@Schema(description = "${table.comment!entity}创建入参")
public record ${entity}CreateDTO(
<#assign fieldList = []>
<#list table.fields as field>
<#if !field.keyFlag
    && field.propertyName != "createdAt" && field.propertyName != "updatedAt"
    && field.propertyName != "createdBy" && field.propertyName != "updatedBy"
    && field.propertyName != "version" && field.propertyName != "deleted"
    && field.propertyName != "status">
    <#assign fieldList = fieldList + [field]>
</#if>
</#list>
<#list fieldList as field>
        @Schema(description = "${field.comment!field.propertyName}")
        ${field.propertyType} ${field.propertyName}<#if field_has_next>,</#if>

</#list>
) {
}
