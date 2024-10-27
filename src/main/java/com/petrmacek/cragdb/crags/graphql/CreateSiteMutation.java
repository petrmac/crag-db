package com.petrmacek.cragdb.crags.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.petrmacek.cragdb.crags.SiteAggregate;
import com.petrmacek.cragdb.crags.api.command.CreateSiteCommand;
import com.petrmacek.cragdb.crags.api.query.GetSiteQuery;
import com.petrmacek.cragdb.generated.types.CreateSiteInput;
import com.petrmacek.cragdb.generated.types.Site;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@DgsComponent
public class CreateSiteMutation {
    private final ReactorCommandGateway commandGateway;
    private final ReactorQueryGateway queryGateway;
    private final DtoMapper dtoMapper;

    @DgsMutation
    public Mono<Site> createSite(@InputArgument CreateSiteInput createSiteInput) {

        log.info("Received request to create new site: '{}'", createSiteInput.getName());

        UUID siteId = UUID.randomUUID();
        CreateSiteCommand createSiteCommand = new CreateSiteCommand(siteId, createSiteInput.getName(), Set.copyOf(createSiteInput.getSectors()));

        return commandGateway.send(createSiteCommand)
                .doOnSubscribe(sub -> log.info("Sending CreateSiteCommand"))
                .doOnSuccess(success -> log.info("Command sent successfully"))
                .then(queryGateway.query(new GetSiteQuery(siteId), SiteAggregate.class)
                        .doOnSubscribe(sub -> log.info("Querying for SiteAggregate"))
                        .map(dtoMapper::mapSite)
                        .retryWhen(Retry.backoff(10, Duration.ofMillis(100)) // Retry up to 10 times with exponential backoff
                                .filter(ex -> ex instanceof OptimisticLockingFailureException)
                                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure()))
                        .doOnError(e -> log.error("Error while fetching site", e))
                        .doOnSuccess(site -> log.info("Site fetched successfully: '{}'", site.getName()))
                );

    }
}
