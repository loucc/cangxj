package com.cxj.user.converter;

import com.cxj.user.controller.dto.UserCreateDTO;
import com.cxj.user.controller.dto.UserUpdateDTO;
import com.cxj.user.controller.vo.UserVO;
import com.cxj.user.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * 用户对象转换器（MapStruct 编译期生成实现）
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    // --- Entity → VO ---

    UserVO toVO(User user);

    List<UserVO> toVOList(List<User> users);

    // --- CreateDTO → Entity ---

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    User fromCreateDTO(UserCreateDTO dto);

    // --- UpdateDTO → Entity（部分更新，跳过 null 字段）---

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateFromDTO(UserUpdateDTO dto, @MappingTarget User user);
}
