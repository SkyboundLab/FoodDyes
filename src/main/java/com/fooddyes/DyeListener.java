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

// Eating a powder or drinking a soda costs 1 heart and earns the
// "Artificially Flavoured" advancement (the effects themselves come from the
// item's components). Dying resets the heart count; joining re-applies max
// health and discovers the recipes.
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
                    "-1 \u2764 . You'll get it back when you die.", NamedTextColor.RED));
        }
    }

    // Grants the advancement's single criterion. The datapack trigger can't
    // express "any of the six items" (its custom_data predicate matches the
    // whole compound exactly, and multi-criterion OR doesn't complete on one
    // criterion here), so the plugin awards it directly.
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
