package run.halo.repo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GithubRepoListRequest {
    private String owner;
    private RepoType type;
    private String token;

    public enum RepoType {
        USER,
        ORG
    }
}
