package run.halo.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Flux;

public interface GithubRepoClient {

    Flux<ObjectNode> listRepos(GithubRepoListRequest repoRequest);
}
