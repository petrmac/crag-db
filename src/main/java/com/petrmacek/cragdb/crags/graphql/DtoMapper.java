package com.petrmacek.cragdb.crags.graphql;

import com.petrmacek.cragdb.crags.RouteAggregate;
import com.petrmacek.cragdb.crags.SiteAggregate;
import com.petrmacek.cragdb.crags.api.model.GradeSystem;
import com.petrmacek.cragdb.crags.api.model.grade.French;
import com.petrmacek.cragdb.crags.api.model.grade.Grade;
import com.petrmacek.cragdb.crags.api.model.grade.UIAA;
import com.petrmacek.cragdb.crags.api.model.grade.YDS;
import com.petrmacek.cragdb.generated.types.Route;
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
    @Mapping(target = "routes", ignore = true)
    Site mapSite(SiteAggregate site);

    @Mapping(target = "grade", source = "grade.value")
    @Mapping(target = "gradeSystem", source = "grade.system")
    Route mapRoute(RouteAggregate route);

    GradeSystem mapGradeSystem(com.petrmacek.cragdb.generated.types.GradeSystem gradeSystem);

}