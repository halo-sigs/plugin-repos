package run.halo.repo;

import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

/**
 * Repo plugin to fetch and manage repositories from popular code hosting services.
 *
 * @author guqing
 * @since 1.0.0
 */
@Component
public class RepoPlugin extends BasePlugin {

    public RepoPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("插件启动成功！");
    }

    @Override
    public void stop() {
        System.out.println("插件停止！");
    }
}
