package com.petrmacek.cragdb.crags.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.petrmacek.cragdb.crags.api.model.grade.French;
import com.petrmacek.cragdb.crags.api.model.grade.UIAA;
import com.petrmacek.cragdb.crags.api.model.grade.YDS;
import com.petrmacek.cragdb.generated.types.GradeSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Comparator;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class GradeDataFetcher {

    @DgsQuery()
    public Flux<GradeSystem> gradingSystems() {
        return Flux.just(GradeSystem.values());
    }

    @DgsQuery()
    public Flux<String> grades(@InputArgument GradeSystem system) {
        switch (system) {
            case French -> {
                return Flux.fromStream(Arrays.stream(French.values())
                        .sorted(Comparator.comparing(French::getOrder))
                        .map(French::getValue));
            }
            case YDS -> {
                return Flux.fromStream(Arrays.stream(YDS.values())
                        .sorted(Comparator.comparing(YDS::getOrder))
                        .map(YDS::getValue));
            }
            case UIAA -> {
                return Flux.fromStream(Arrays.stream(UIAA.values())
                        .sorted(Comparator.comparing(UIAA::getOrder))
                        .map(UIAA::getValue));
            }
            default -> {
                log.error("Unknown grade system: {}", system);
                return Flux.empty();
            }
        }
    }
}
