package run.halo.repo;

public enum PlatformEnum {
    GITHUB;

    public static PlatformEnum convertFrom(String value) {
        for (PlatformEnum platformEnum : values()) {
            if (platformEnum.name().equalsIgnoreCase(value)) {
                return platformEnum;
            }
        }
        return null;
    }
}
