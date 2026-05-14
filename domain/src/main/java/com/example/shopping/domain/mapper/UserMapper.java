package com.example.shopping.domain.mapper;

import com.example.shopping.common.dto.UserResponse;
import com.example.shopping.facade.dto.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(UserResponse response);
}
