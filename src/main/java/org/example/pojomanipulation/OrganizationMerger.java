package org.example.pojomanipulation;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrganizationMerger {
  OrganizationMerger INSTANCE = Mappers.getMapper(OrganizationMerger.class);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void merge(@MappingTarget Object target, Object patch);
}
