package run.halo.repo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;
import run.halo.app.infra.ConditionList;

@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "repo.halo.run", version = "v1alpha1",
    kind = "RepositoryRegistry", singular = "repositoryregistry", plural = "repositoryregistries")
public class RepositoryRegistry extends AbstractExtension {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private RegistrySpec spec;

    private RegistryStatus status;

    @Data
    public static class RegistrySpec {
        // guqing
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1)
        private String owner;
        // github
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1)
        private String platform;

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1)
        private String type;

        private TokenSecretRef tokenSecretRef;
    }

    public static RegistryStatus nullSafeStatus(RepositoryRegistry registry) {
        RegistryStatus status = registry.getStatus();
        if (status == null) {
            status = new RegistryStatus();
            registry.setStatus(status);
        }
        return status;
    }

    @Data
    public static class RegistryStatus {
        private RegistryPhase phase;
        private ConditionList conditions;
    }

    public enum RegistryPhase {
        PENDING,
        FAILED,
        SUCCEEDED
    }

    @Data
    public static class TokenSecretRef {
        private String secretName;
        private String secretKey;
    }
}
