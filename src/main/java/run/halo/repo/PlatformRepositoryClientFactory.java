package run.halo.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Platform repository client factory.
 *
 * @author guqing
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class PlatformRepositoryClientFactory {
    private final ApplicationContext applicationContext;

    public PlatformRepositoryClient getClient(PlatformEnum platform) {
        return switch (platform) {
            case GITHUB -> applicationContext.getBean(GithubPlatformRepositoryClient.class);
            // TODO: Add more platform support
        };
    }
}
