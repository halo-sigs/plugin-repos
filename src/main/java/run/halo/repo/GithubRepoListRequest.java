package run.halo.repo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GithubRepoListRequest {
    private String owner;
    private RepoOwnerType type;
    private String token;
}
