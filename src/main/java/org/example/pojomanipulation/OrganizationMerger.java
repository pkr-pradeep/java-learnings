package org.example.pojomanipulation;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrganizationMerger {
  OrganizationMerger INSTANCE = Mappers.getMapper(OrganizationMerger.class);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void merge(@MappingTarget Object target, Object patch);
}
