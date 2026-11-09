package com.fooddyes;

import io.papermc.paper.datapack.DatapackRegistrar;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Paper bootstrapper. Two jobs:
 *
 * <ul>
 *   <li>The descriptor uses the correct {@code bootstrapper:} key (Paper
 *   silently ignores the wrong {@code bootstrap:} key).</li>
 *   <li>The plugin's own advancement datapack is discovered here, so the
 *   plugin ships its advancement itself — no companion datapack to install.
 *   The datapack lives in the jar at {@code /pack} (pack.mcmeta +
 *   data/red40/advancement/eat_red40.json). It is discovered on every
 *   datapack-discovery pass and auto-enabled on server start, so
 *   "Artificially Flavoured" is a real vanilla advancement: same toast,
 *   chat announcement, parent and XP reward as the datapack version,
 *   visible in the normal advancement screen and manageable with
 *   /advancement and /datapack commands. No extra library needed —
 *   UltimateAdvancementAPI and friends build fake packet-based
 *   advancements in a custom tab, which is heavier and different from
 *   what this needs.</li>
 * </ul>
 */
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
