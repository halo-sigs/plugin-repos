package run.halo.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Instant;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.halo.app.extension.Metadata;

@Component
public class GithubPlatformRepositoryClient implements PlatformRepositoryClient {
    private final GithubRepoClient repoClient = new DefaultGithubRepoClient();

    @Override
    public Flux<Repository> listRepos() {
        // TODO build query parameters here
        return repoClient.listRepos(null)
            .map(this::mapToRepository);
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
        spec.setVisibility(githubRepo.get("private").asBoolean());
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
