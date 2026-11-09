package com.fooddyes;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

/**
 * The heart price. Each powder eaten or soda drunk costs 1 heart of max
 * health (2.0), tracked per player in their PersistentDataContainer so it
 * survives relogs. Dying gives the hearts back: the count resets on respawn.
 * Max health never drops below 2.0 (one heart), so eating cannot kill.
 */
public class HeartManager {

    public static final double BASE_MAX_HEALTH = 20.0;
    public static final double MIN_MAX_HEALTH = 2.0;

    private final FoodDyesPlugin plugin;
    private final NamespacedKey heartsLostKey;

    public HeartManager(FoodDyesPlugin plugin) {
        this.plugin = plugin;
        this.heartsLostKey = new NamespacedKey(plugin, "hearts_lost");
    }

    public int heartsLost(Player player) {
        Integer v = player.getPersistentDataContainer()
                .get(heartsLostKey, PersistentDataType.INTEGER);
        return v == null ? 0 : v;
    }

    public void setHeartsLost(Player player, int lost) {
        int clamped = Math.max(0, Math.min(lost, plugin.config().maxHeartsLost));
        player.getPersistentDataContainer()
                .set(heartsLostKey, PersistentDataType.INTEGER, clamped);
        apply(player);
    }

    /** Re-apply max health from the stored count (e.g. on join). */
    public void apply(Player player) {
        int lost = Math.max(0, Math.min(heartsLost(player), plugin.config().maxHeartsLost));
        double max = Math.max(MIN_MAX_HEALTH, BASE_MAX_HEALTH - 2.0 * lost);
        AttributeInstance inst = player.getAttribute(Attribute.MAX_HEALTH);
        if (inst != null) {
            inst.setBaseValue(max);
        }
        if (player.getHealth() > max) {
            player.setHealth(max);
        }
    }

    /** Take one heart from the eater. Returns true if a heart was taken. */
    public boolean takeHeart(Player player) {
        if (!plugin.config().heartCostEnabled) {
            return false;
        }
        int lost = heartsLost(player);
        if (lost >= plugin.config().maxHeartsLost) {
            return false;
        }
        setHeartsLost(player, lost + 1);
        return true;
    }

    /** Give all lost hearts back (on respawn). */
    public void reset(Player player) {
        setHeartsLost(player, 0);
    }

    /** Current max health, for /fooddyes hearts. */
    public double maxHealth(Player player) {
        AttributeInstance inst = player.getAttribute(Attribute.MAX_HEALTH);
        return inst == null ? BASE_MAX_HEALTH : inst.getBaseValue();
    }
}
