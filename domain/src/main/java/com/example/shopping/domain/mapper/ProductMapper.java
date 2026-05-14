package com.example.shopping.domain.mapper;

import com.example.shopping.common.dto.ProductResponse;
import com.example.shopping.facade.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDTO(ProductResponse response);
}
