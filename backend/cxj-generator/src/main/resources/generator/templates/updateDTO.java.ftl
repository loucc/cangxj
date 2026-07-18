package ${basePackage}.${moduleName}.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ${table.comment!entity}更新入参
 *
 * @author ${author}
 */
@Schema(description = "${table.comment!entity}更新入参")
public record ${entity}UpdateDTO(
<#assign fieldList = []>
<#list table.fields as field>
<#if !field.keyFlag
    && field.propertyName != "createdAt" && field.propertyName != "updatedAt"
    && field.propertyName != "createdBy" && field.propertyName != "updatedBy"
    && field.propertyName != "version" && field.propertyName != "deleted"
    && field.propertyName != "password"
    && field.propertyName != "username">
    <#assign fieldList = fieldList + [field]>
</#if>
</#list>
<#list fieldList as field>
        @Schema(description = "${field.comment!field.propertyName}")
        ${field.propertyType} ${field.propertyName}<#if field_has_next>,</#if>

</#list>
) {
}
