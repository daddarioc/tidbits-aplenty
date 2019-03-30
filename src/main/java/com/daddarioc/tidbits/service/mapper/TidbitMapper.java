package com.daddarioc.tidbits.service.mapper;

import com.daddarioc.tidbits.domain.Tidbit;
import com.daddarioc.tidbits.service.dto.TidbitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity Tidbit and its DTO TidbitDTO.
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface TidbitMapper extends EntityMapper<TidbitDTO, Tidbit> {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    TidbitDTO toDto(Tidbit tidbit);

    @Mapping(source = "categoryId", target = "category")
    Tidbit toEntity(TidbitDTO tidbitDTO);

    default Tidbit fromId(Long id) {
        if (id == null) {
            return null;
        }
        Tidbit tidbit = new Tidbit();
        tidbit.setId(id);
        return tidbit;
    }
}
