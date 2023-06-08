package run.halo.repo;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.controller.Controller;
import run.halo.app.extension.controller.ControllerBuilder;
import run.halo.app.extension.controller.Reconciler;
import run.halo.app.infra.Condition;
import run.halo.app.infra.ConditionList;
import run.halo.app.infra.ConditionStatus;

@Component
@RequiredArgsConstructor
public class RepositoryRegistryReconciler implements Reconciler<Reconciler.Request> {

    private final ExtensionClient client;
    private final PlatformRepositoryClientFactory platformRepositoryClientFactory;

    @Override
    public Result reconcile(Request request) {
        client.fetch(RepositoryRegistry.class, request.name())
            .ifPresent(registry -> {
                String platform = registry.getSpec().getPlatform();
                PlatformEnum platformEnum = PlatformEnum.convertFrom(platform);
                if (platformEnum == null) {
                    return;
                }
                RepositoryRegistry.RegistryStatus status =
                    RepositoryRegistry.nullSafeStatus(registry);
                status.setPhase(RepositoryRegistry.RegistryPhase.PENDING);
                nullSafeConditions(registry).
                    addAndEvictFIFO(Condition.builder()
                        .type("Pending")
                        .reason("Pending")
                        .status(ConditionStatus.TRUE)
                        .message("Pending to sync repositories from platform")
                        .build());
                updateStatus(registry);

                try {
                    PlatformRepositoryClient repoClient =
                        platformRepositoryClientFactory.getClient(platformEnum);
                    List<Repository> repositories = repoClient.listRepos(registry)
                        .filter(repo -> isTrue(repo.getSpec().getVisibility()))
                        .collectList()
                        .block();
                    if (repositories != null) {
                        repositories.stream()
                            .filter(repo -> fetchRepository(repo.getSpec().getPlatformName(),
                                repo.getSpec().getFullName()).isEmpty()
                            )
                            .forEach(client::create);
                    }
                } catch (Exception e) {
                    nullSafeConditions(registry).addAndEvictFIFO(Condition.builder()
                        .type("Failed")
                        .reason("Failed")
                        .status(ConditionStatus.FALSE)
                        .message("Failed to sync repositories from platform")
                        .build());
                    updateStatus(registry);
                    return;
                }

                nullSafeConditions(registry).addAndEvictFIFO(Condition.builder()
                    .type("Success")
                    .reason("Success")
                    .status(ConditionStatus.TRUE)
                    .message("Sync repositories successfully")
                    .build());
                updateStatus(registry);
            });
        return new Result(true, Duration.ofDays(10));
    }

    private static ConditionList nullSafeConditions(RepositoryRegistry registry) {
        RepositoryRegistry.RegistryStatus status =
            RepositoryRegistry.nullSafeStatus(registry);
        ConditionList conditions = status.getConditions();
        if (conditions == null) {
            conditions = new ConditionList();
        }
        return conditions;
    }

    private void updateStatus(RepositoryRegistry oldRegistry) {
        client.fetch(RepositoryRegistry.class, oldRegistry.getMetadata().getName())
            .ifPresent(registry -> {
                RepositoryRegistry.RegistryStatus oldStatus = oldRegistry.getStatus();
                registry.setStatus(oldRegistry.getStatus());
                if (oldStatus != registry.getStatus()) {
                    client.update(registry);
                }
            });
    }

    Optional<Repository> fetchRepository(String platform, String fullName) {
        return client.list(Repository.class,
                repo -> repo.getSpec().getPlatformName().equalsIgnoreCase(platform)
                    && repo.getSpec().getFullName().equals(fullName), null)
            .stream()
            .findFirst();
    }

    @Override
    public Controller setupWith(ControllerBuilder builder) {
        return builder
            .extension(new RepositoryRegistry())
            .build();
    }
}
