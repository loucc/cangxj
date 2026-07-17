package ${basePackage}.${moduleName}.converter;

import ${basePackage}.${moduleName}.controller.dto.${entity}CreateDTO;
import ${basePackage}.${moduleName}.controller.dto.${entity}UpdateDTO;
import ${basePackage}.${moduleName}.controller.vo.${entity}VO;
import ${basePackage}.${moduleName}.entity.${entity};
import org.mapstruct.*;

import java.util.List;

/**
 * ${table.comment!entity}对象转换器
 *
 * @author ${author}
 */
@Mapper(componentModel = "spring")
public interface ${entity}Converter {

    ${entity}VO toVO(${entity} entity);

    List<${entity}VO> toVOList(List<${entity}> entities);

    @Mapping(target = "id", ignore = true)
<#list table.fields as field>
<#if field.propertyName == "password"
    || field.propertyName == "createdAt" || field.propertyName == "updatedAt"
    || field.propertyName == "createdBy" || field.propertyName == "updatedBy"
    || field.propertyName == "version" || field.propertyName == "deleted">
    @Mapping(target = "${field.propertyName}", ignore = true)
</#if>
</#list>
    @Mapping(target = "status", constant = "ACTIVE")
    ${entity} fromCreateDTO(${entity}CreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
<#list table.fields as field>
<#if field.propertyName == "password"
    || field.propertyName == "createdAt" || field.propertyName == "updatedAt"
    || field.propertyName == "createdBy" || field.propertyName == "updatedBy"
    || field.propertyName == "version" || field.propertyName == "deleted">
    @Mapping(target = "${field.propertyName}", ignore = true)
</#if>
</#list>
    void updateFromDTO(${entity}UpdateDTO dto, @MappingTarget ${entity} entity);
}
