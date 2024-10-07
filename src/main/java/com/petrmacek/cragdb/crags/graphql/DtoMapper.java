package com.petrmacek.cragdb.crags.graphql;

import com.petrmacek.cragdb.crags.SiteAggregate;
import com.petrmacek.cragdb.generated.types.Site;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(componentModel = "spring",
        injectionStrategy = CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface DtoMapper {

    @Mapping(target = "id", source = "siteId")
    Site mapSite(SiteAggregate site);

}