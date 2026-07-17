package ${basePackage}.${moduleName}.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
<#list table.fields as field>
<#if field.propertyType == "LocalDateTime">
import java.time.LocalDateTime;
<#break>
</#if>
</#list>

/**
 * ${table.comment!entity}视图对象
 *
 * @author ${author}
 */
@Schema(description = "${table.comment!entity}视图对象")
public record ${entity}VO(
<#assign fieldList = []>
<#list table.fields as field>
<#if field.propertyName != "password"
    && field.propertyName != "createdBy" && field.propertyName != "updatedBy"
    && field.propertyName != "version" && field.propertyName != "deleted">
    <#assign fieldList = fieldList + [field]>
</#if>
</#list>
<#list fieldList as field>
        ${field.propertyType} ${field.propertyName}<#if field_has_next>,</#if>

</#list>
) {
}
