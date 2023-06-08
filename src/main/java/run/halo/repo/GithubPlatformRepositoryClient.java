package run.halo.repo;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Instant;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.Secret;

@Component
@RequiredArgsConstructor
public class GithubPlatformRepositoryClient implements PlatformRepositoryClient {
    private final GithubRepoClient repoClient = new DefaultGithubRepoClient();

    private final ReactiveExtensionClient client;

    @Override
    public Flux<Repository> listRepos(RepositoryRegistry registry) {
        return Mono.fromSupplier(() -> GithubRepoListRequest.builder()
                .type(RepoOwnerType.convertFrom(registry.getSpec().getType()))
                .owner(registry.getSpec().getOwner())
            )
            .flatMap(builder -> {
                if (registry.getSpec().getTokenSecretRef() == null) {
                    return Mono.just(builder);
                }
                return client.fetch(Secret.class,
                        registry.getSpec().getTokenSecretRef().getSecretName())
                    .filter(secret -> secret.getStringData() != null)
                    .map(secret -> {
                        String value = secret.getStringData()
                            .get(registry.getSpec().getTokenSecretRef().getSecretKey());
                        return builder.token(value);
                    })
                    .defaultIfEmpty(builder);
            })
            .flatMapMany(builder -> repoClient.listRepos(builder.build())
                .map(this::mapToRepository)
            );
    }

    private Repository mapToRepository(@NonNull ObjectNode githubRepo) {
        Repository repository = new Repository();
        repository.setMetadata(new Metadata());
        repository.getMetadata().setGenerateName("repo-");
        Repository.RepoSpec spec = new Repository.RepoSpec();
        repository.setSpec(spec);
        spec.setId(githubRepo.get("id").asText());
        spec.setName(githubRepo.get("name").textValue());
        spec.setFullName(githubRepo.get("full_name").textValue());
        spec.setUrl(githubRepo.get("html_url").textValue());
        spec.setDescription(githubRepo.get("description").textValue());
        spec.setFork(githubRepo.get("fork").asBoolean());
        spec.setForksCount(githubRepo.get("forks_count").asInt());
        spec.setStargazersCount(githubRepo.get("stargazers_count").asInt());
        spec.setWatchersCount(githubRepo.get("watchers_count").asInt());
        spec.setLanguage(githubRepo.get("language").textValue());
        spec.setPlatformName("github");
        spec.setVisibility(isFalse(githubRepo.get("private").asBoolean()));
        spec.setUpdatedAt(Instant.parse(githubRepo.get("updated_at").textValue()));
        spec.setOpenIssuesCount(githubRepo.get("open_issues_count").asInt());

        Repository.Owner owner = new Repository.Owner();
        spec.setOwner(owner);
        owner.setLogin(githubRepo.get("owner").get("login").textValue());
        owner.setAvatarUrl(githubRepo.get("owner").get("avatar_url").textValue());
        owner.setHtmlUrl(githubRepo.get("owner").get("html_url").textValue());
        owner.setType(githubRepo.get("owner").get("type").textValue());
        return repository;
    }
}
