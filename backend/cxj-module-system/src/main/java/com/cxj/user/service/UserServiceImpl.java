package com.cxj.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.cxj.common.enums.ResultCode;
import com.cxj.common.exception.BusinessException;
import com.cxj.common.response.PageResult;
import com.cxj.user.controller.dto.UserCreateDTO;
import com.cxj.user.controller.dto.UserQueryDTO;
import com.cxj.user.controller.dto.UserUpdateDTO;
import com.cxj.user.controller.vo.UserVO;
import com.cxj.user.converter.UserConverter;
import com.cxj.user.entity.User;
import com.cxj.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO create(UserCreateDTO dto) {
        if (lambdaQuery().eq(User::getUsername, dto.username()).exists()) {
            throw new BusinessException(ResultCode.CONFLICT, "用户名已存在");
        }
        User user = userConverter.fromCreateDTO(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        save(user);
        return userConverter.toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "user", key = "#id")
    public UserVO update(Long id, UserUpdateDTO dto) {
        User user = requireById(id);
        userConverter.updateFromDTO(dto, user);
        updateById(user);
        return userConverter.toVO(user);
    }

    @Override
    @Cacheable(cacheNames = "user", key = "#id", unless = "#result == null")
    public UserVO getVO(Long id) {
        return userConverter.toVO(getById(id));
    }

    @Override
    public PageResult<UserVO> page(UserQueryDTO query) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .and(StringUtils.hasText(query.keyword()), w -> w
                        .like(User::getUsername, query.keyword())
                        .or().like(User::getNickname, query.keyword())
                        .or().like(User::getEmail, query.keyword()))
                .eq(StringUtils.hasText(query.status()), User::getStatus, query.status())
                .orderByDesc(User::getCreatedAt);
        Page<User> page = page(Page.of(query.safeCurrent(), query.safeSize()), wrapper);
        return PageResult.of(page, userConverter::toVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "user", key = "#id")
    public void delete(Long id) {
        if (!removeById(id)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
    }

    @Override
    public User loadByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    private User requireById(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }
}
