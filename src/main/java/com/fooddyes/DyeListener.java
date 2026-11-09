package com.fooddyes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Eating, dying, joining.
 *
 * <ul>
 *   <li>Eating a powder or drinking a soda costs 1 heart of max health
 *   (the effects themselves come from the item's components, no code
 *   needed) and earns the "Artificially Flavoured" advancement.</li>
 *   <li>Dying gives the hearts back: the count resets on respawn.</li>
 *   <li>Joining re-applies max health from the stored count and unlocks
 *   the recipes, so a relog never loses (or keeps) the wrong hearts.</li>
 * </ul>
 */
public class DyeListener implements Listener {

    private final FoodDyesPlugin plugin;

    public DyeListener(FoodDyesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        DyeItems.Identified id = plugin.items().identify(event.getItem());
        if (id == null) {
            return; // Not one of ours.
        }
        // Powders and sodas both cost a heart, and eating or drinking any
        // dye earns "Artificially Flavoured".
        Player player = event.getPlayer();
        awardAdvancement(player);
        if (plugin.hearts().takeHeart(player)) {
            player.sendActionBar(Component.text(
                    "-1 \u2764 — you'll get it back when you die.", NamedTextColor.RED));
        }
    }

    /**
     * Awards the advancement's single criterion. The criterion uses the
     * impossible trigger and is granted here, because the plugin is the
     * authority on what counts as a food dye — the datapack trigger cannot
     * express "any of the six items": its custom_data predicate matches the
     * whole compound exactly (a shared marker tag does not match), and
     * multi-criterion OR requirements do not complete on one criterion in
     * this environment (a fresh player needed 5 of 6 criteria granted).
     */
    private void awardAdvancement(Player player) {
        Advancement adv = Bukkit.getAdvancement(new NamespacedKey("red40", "eat_red40"));
        if (adv == null) {
            return; // Advancement datapack not loaded; nothing to award.
        }
        AdvancementProgress progress = player.getAdvancementProgress(adv);
        if (!progress.isDone()) {
            progress.awardCriteria("ate_red40");
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        // The hearts come back when the player respawns (the player object
        // there is the live one, so the attribute change sticks).
        plugin.hearts().reset(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.hearts().apply(player);
        for (var key : plugin.recipeKeys()) {
            player.discoverRecipe(key);
        }
    }
}
