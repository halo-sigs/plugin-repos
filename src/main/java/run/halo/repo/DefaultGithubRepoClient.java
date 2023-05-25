package run.halo.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * <p>A default implementation of {@link GithubRepoClient}.</p>
 *
 * @author guqing
 * @since 1.0.0
 */
public class DefaultGithubRepoClient implements GithubRepoClient {
    private final WebClient webClient = WebClient.builder().build();
    private static final String GITHUB_API_URL = "https://api.github.com";

    @Override
    public Flux<ObjectNode> listRepos(GithubRepoListRequest repoRequest) {
        return webClient.get()
            .uri(getRepoUrl(repoRequest.getOwner(), repoRequest.getType()))
            .headers(headers -> {
                if (StringUtils.isNotBlank(repoRequest.getToken())) {
                    headers.add(HttpHeaders.AUTHORIZATION,
                        "Bearer " + repoRequest.getToken());
                }
                headers.add(HttpHeaders.ACCEPT, "Accept: application/vnd.github+json");
                headers.add("X-GitHub-Api-Version", "2022-11-28");
            })
            .retrieve()
            .bodyToFlux(ObjectNode.class);
    }

    private String getRepoUrl(String owner, RepoOwnerType type) {
        return switch (type) {
            case USER -> GITHUB_API_URL + "/users/" + owner + "/repos";
            case ORG -> GITHUB_API_URL + "/orgs/" + owner + "/repos";
        };
    }
}
