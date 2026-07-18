package ${basePackage}.${moduleName}.entity;

<#list table.importPackages as pkg>
import ${pkg};
</#list>
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * ${table.comment!entity} 实体
 *
 * @author ${author}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("${table.name}")
public class ${entity} implements Serializable {

<#list table.fields as field>
<#if field.keyFlag>
    @TableId(type = IdType.ASSIGN_ID)
<#elseif field.name == "deleted">
    @TableLogic
<#elseif field.name == "version">
    @Version
<#elseif field.name == "created_at" || field.name == "created_by">
    @TableField(fill = FieldFill.INSERT)
<#elseif field.name == "updated_at" || field.name == "updated_by">
    @TableField(fill = FieldFill.INSERT_UPDATE)
</#if>
    private ${field.propertyType} ${field.propertyName};

</#list>
}
