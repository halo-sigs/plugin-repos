package run.halo.repo;

import org.springframework.lang.NonNull;

public enum RepoOwnerType {
    USER,
    ORG;

    @NonNull
    public static RepoOwnerType convertFrom(String value) {
        for (RepoOwnerType repoOwnerType : values()) {
            if (repoOwnerType.name().equalsIgnoreCase(value)) {
                return repoOwnerType;
            }
        }
        throw new IllegalArgumentException("Unknown repo owner type: " + value);
    }
}
