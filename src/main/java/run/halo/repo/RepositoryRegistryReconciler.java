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
            });
        return new Result(true, Duration.ofDays(10));
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
