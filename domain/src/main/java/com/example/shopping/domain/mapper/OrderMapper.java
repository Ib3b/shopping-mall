package com.example.shopping.domain.mapper;

import com.example.shopping.common.dto.OrderResponse;
import com.example.shopping.facade.dto.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "statusDescription", target = "statusDesc")
    OrderDTO toDTO(OrderResponse response);
}
