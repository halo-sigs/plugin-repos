package run.halo.repo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "repo.halo.run", version = "v1alpha1",
    kind = "Repo", singular = "repo", plural = "repos")
public class Repository extends AbstractExtension {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private RepoSpec spec;

    @Data
    public static class RepoSpec {
        private String id;
        private String name;
        private String fullName;
        private Owner owner;
        private String url;
        private String description;
        private Boolean fork;
        private Integer forksCount;
        private Integer stargazersCount;
        private Integer watchersCount;
        private Integer openIssuesCount;
        private String language;
        private String platformName;
        private Boolean visibility;
        private Instant updatedAt;
    }

    @Data
    public static class Owner {
        private String login;
        private String avatarUrl;
        private String htmlUrl;
        private String type;
    }
}
