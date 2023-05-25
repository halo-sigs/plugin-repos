package run.halo.repo;

import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;

/**
 * Repo plugin to fetch and manage repositories from popular code hosting services.
 *
 * @author guqing
 * @since 1.0.0
 */
@Component
public class RepoPlugin extends BasePlugin {

    private final SchemeManager schemeManager;

    public RepoPlugin(PluginWrapper wrapper, SchemeManager schemeManager) {
        super(wrapper);
        this.schemeManager = schemeManager;
    }

    @Override
    public void start() {
        schemeManager.register(Repository.class);
        schemeManager.register(RepositoryRegistry.class);
    }

    @Override
    public void stop() {
        schemeManager.unregister(schemeManager.get(Repository.class));
        schemeManager.unregister(schemeManager.get(RepositoryRegistry.class));
    }
}
