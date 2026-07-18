package ${basePackage}.${moduleName}.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.cxj.common.enums.ResultCode;
import com.cxj.common.exception.BusinessException;
import com.cxj.common.response.PageResult;
import ${basePackage}.${moduleName}.controller.dto.${entity}CreateDTO;
import ${basePackage}.${moduleName}.controller.dto.${entity}QueryDTO;
import ${basePackage}.${moduleName}.controller.dto.${entity}UpdateDTO;
import ${basePackage}.${moduleName}.controller.vo.${entity}VO;
import ${basePackage}.${moduleName}.converter.${entity}Converter;
import ${basePackage}.${moduleName}.entity.${entity};
import ${basePackage}.${moduleName}.mapper.${entity}Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ${table.comment!entity} Service 实现
 *
 * @author ${author}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ${entity}ServiceImpl extends ServiceImpl<${entity}Mapper, ${entity}> implements ${entity}Service {

    private final ${entity}Converter ${entity?uncap_first}Converter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ${entity}VO create(${entity}CreateDTO dto) {
        ${entity} entity = ${entity?uncap_first}Converter.fromCreateDTO(dto);
        save(entity);
        return ${entity?uncap_first}Converter.toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "${entity?uncap_first}", key = "#id")
    public ${entity}VO update(Long id, ${entity}UpdateDTO dto) {
        ${entity} entity = requireById(id);
        ${entity?uncap_first}Converter.updateFromDTO(dto, entity);
        updateById(entity);
        return ${entity?uncap_first}Converter.toVO(entity);
    }

    @Override
    @Cacheable(cacheNames = "${entity?uncap_first}", key = "#id", unless = "#result == null")
    public ${entity}VO getVO(Long id) {
        return ${entity?uncap_first}Converter.toVO(getById(id));
    }

    @Override
    public PageResult<${entity}VO> page(${entity}QueryDTO query) {
        LambdaQueryWrapper<${entity}> wrapper = new LambdaQueryWrapper<${entity}>()
                .orderByDesc(${entity}::getCreatedAt);
        Page<${entity}> page = page(Page.of(query.safeCurrent(), query.safeSize()), wrapper);
        return PageResult.of(page, ${entity?uncap_first}Converter::toVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "${entity?uncap_first}", key = "#id")
    public void delete(Long id) {
        if (!removeById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "${table.comment!entity}不存在");
        }
    }

    private ${entity} requireById(Long id) {
        ${entity} entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "${table.comment!entity}不存在");
        }
        return entity;
    }
}
