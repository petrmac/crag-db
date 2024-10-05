package com.petrmacek.cragdb.site.graphql;

import com.petrmacek.cragdb.generated.types.Site;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

@Mapper(componentModel = "spring",
        injectionStrategy = CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface DtoMapper {

    Site mapSite(com.petrmacek.cragdb.site.Site site);

}