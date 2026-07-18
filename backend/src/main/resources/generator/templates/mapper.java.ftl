package ${basePackage}.${moduleName}.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ${basePackage}.${moduleName}.entity.${entity};
import org.apache.ibatis.annotations.Mapper;

/**
 * ${table.comment!entity} Mapper
 *
 * @author ${author}
 */
@Mapper
public interface ${entity}Mapper extends BaseMapper<${entity}> {
}
