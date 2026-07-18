package com.cxj.user.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.cxj.common.response.PageResult;
import com.cxj.user.controller.dto.UserCreateDTO;
import com.cxj.user.controller.dto.UserQueryDTO;
import com.cxj.user.controller.dto.UserUpdateDTO;
import com.cxj.user.controller.vo.UserVO;
import com.cxj.user.entity.User;

public interface UserService extends IService<User> {

    UserVO create(UserCreateDTO dto);

    UserVO update(Long id, UserUpdateDTO dto);

    UserVO getVO(Long id);

    PageResult<UserVO> page(UserQueryDTO query);

    void delete(Long id);

    User loadByUsername(String username);
}
