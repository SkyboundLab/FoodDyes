package com.fooddyes;

import io.papermc.paper.datapack.DatapackRegistrar;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

// Paper bootstrapper. Registers the plugin's own advancement datapack (the
// /pack directory in the jar, auto-enabled on server start), so
// "Artificially Flavoured" is a real vanilla advancement with no extra
// library. Note: the descriptor must use the "bootstrapper:" key; Paper
// silently ignores "bootstrap:".
public class FoodDyesBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(
                LifecycleEvents.DATAPACK_DISCOVERY, event -> {
            DatapackRegistrar registrar = event.registrar();
            try {
                final URI uri = Objects.requireNonNull(
                        FoodDyesBootstrap.class.getResource("/pack")
                ).toURI();
                registrar.discoverPack(uri, "advancements",
                        c -> c.autoEnableOnServerStart(true));
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(
                        "FoodDyes could not discover its advancement datapack", e);
            }
        });
    }
}
