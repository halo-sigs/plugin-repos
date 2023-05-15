package run.halo.repo;

import reactor.core.publisher.Flux;

public interface PlatformRepositoryClient {

    Flux<Repository> listRepos();
}
