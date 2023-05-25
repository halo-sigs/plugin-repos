package run.halo.repo;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import run.halo.app.extension.ReactiveExtensionClient;

@Configuration
@RequiredArgsConstructor
public class RepoPluginConfig {
    private final ReactiveExtensionClient client;

    @Bean
    RouterFunction<ServerResponse> repoTemplateRouter() {
        return RouterFunctions.route()
            .GET("/repositories", request -> client.list(Repository.class, null, null)
                .collectList()
                .flatMap(repositories -> {
                    Map<String, Object> model = new HashMap<>();
                    model.put("repositories", repositories);
                    return ServerResponse.ok().render("repositories", model);
                })
            )
            .build();
    }
}
