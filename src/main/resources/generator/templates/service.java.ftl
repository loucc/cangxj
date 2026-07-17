package ${basePackage}.${moduleName}.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.cxj.common.response.PageResult;
import ${basePackage}.${moduleName}.controller.dto.${entity}CreateDTO;
import ${basePackage}.${moduleName}.controller.dto.${entity}QueryDTO;
import ${basePackage}.${moduleName}.controller.dto.${entity}UpdateDTO;
import ${basePackage}.${moduleName}.controller.vo.${entity}VO;
import ${basePackage}.${moduleName}.entity.${entity};

/**
 * ${table.comment!entity} Service
 *
 * @author ${author}
 */
public interface ${entity}Service extends IService<${entity}> {

    ${entity}VO create(${entity}CreateDTO dto);

    ${entity}VO update(Long id, ${entity}UpdateDTO dto);

    ${entity}VO getVO(Long id);

    PageResult<${entity}VO> page(${entity}QueryDTO query);

    void delete(Long id);
}
