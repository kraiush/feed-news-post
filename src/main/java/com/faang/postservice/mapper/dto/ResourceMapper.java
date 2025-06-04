package com.faang.postservice.mapper.dto;

import com.faang.postservice.dto.resource.ResourceDto;
import com.faang.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    ResourceDto toDto(Resource resource);

}
