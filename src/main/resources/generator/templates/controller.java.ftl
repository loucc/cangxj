package ${basePackage}.${moduleName}.controller;

import com.cxj.common.response.PageResult;
import com.cxj.common.response.R;
import ${basePackage}.${moduleName}.controller.dto.${entity}CreateDTO;
import ${basePackage}.${moduleName}.controller.dto.${entity}QueryDTO;
import ${basePackage}.${moduleName}.controller.dto.${entity}UpdateDTO;
import ${basePackage}.${moduleName}.controller.vo.${entity}VO;
import ${basePackage}.${moduleName}.service.${entity}Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

/**
 * ${table.comment!entity} Controller
 *
 * @author ${author}
 */
@Tag(name = "${table.comment!entity}管理", description = "${table.comment!entity} CRUD 接口")
@RestController
@RequestMapping("/${entity?uncap_first}s")
@RequiredArgsConstructor
public class ${entity}Controller {

    private final ${entity}Service ${entity?uncap_first}Service;

    @Operation(summary = "创建${table.comment!entity}")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public R<${entity}VO> create(@Valid @RequestBody ${entity}CreateDTO dto) {
        return R.ok(${entity?uncap_first}Service.create(dto));
    }

    @Operation(summary = "更新${table.comment!entity}")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public R<${entity}VO> update(@PathVariable Long id, @Valid @RequestBody ${entity}UpdateDTO dto) {
        return R.ok(${entity?uncap_first}Service.update(id, dto));
    }

    @Operation(summary = "获取${table.comment!entity}详情")
    @GetMapping("/{id}")
    public R<${entity}VO> get(@PathVariable Long id) {
        return R.ok(${entity?uncap_first}Service.getVO(id));
    }

    @Operation(summary = "分页查询${table.comment!entity}")
    @GetMapping
    public R<PageResult<${entity}VO>> page(@ParameterObject ${entity}QueryDTO query) {
        return R.ok(${entity?uncap_first}Service.page(query));
    }

    @Operation(summary = "删除${table.comment!entity}")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public R<Void> delete(@PathVariable Long id) {
        ${entity?uncap_first}Service.delete(id);
        return R.ok();
    }
}
