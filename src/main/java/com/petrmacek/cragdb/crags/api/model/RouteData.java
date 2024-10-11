package com.petrmacek.cragdb.crags.api.model;

import com.petrmacek.cragdb.crags.api.model.grade.Grade;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RouteData {
    private String name;
    private Grade grade;
    private GradeSystem gradeSystem;

}
