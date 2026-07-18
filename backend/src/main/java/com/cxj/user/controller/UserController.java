package com.cxj.user.controller;

import com.cxj.common.response.PageResult;
import com.cxj.common.response.R;
import com.cxj.user.controller.dto.UserCreateDTO;
import com.cxj.user.controller.dto.UserQueryDTO;
import com.cxj.user.controller.dto.UserUpdateDTO;
import com.cxj.user.controller.vo.UserVO;
import com.cxj.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理", description = "用户 CRUD 接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "创建用户")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public R<UserVO> create(@Valid @RequestBody UserCreateDTO dto) {
        return R.ok(userService.create(dto));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public R<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        return R.ok(userService.update(id, dto));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public R<UserVO> get(@PathVariable Long id) {
        return R.ok(userService.getVO(id));
    }

    @Operation(summary = "分页查询用户")
    @GetMapping
    public R<PageResult<UserVO>> page(@ParameterObject UserQueryDTO query) {
        return R.ok(userService.page(query));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public R<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return R.ok();
    }
}
