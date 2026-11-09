package com.fooddyes;

import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;

/** Parsed config.yml. Values are clamped so a bad edit cannot break a server. */
public class FoodDyesConfig {

    public int powderEffectSeconds = 30;
    public int drinkEffectSeconds = 30;
    public boolean heartCostEnabled = true;
    /** Most hearts a player can lose to food dyes (9 = down to 1 heart of max health). */
    public int maxHeartsLost = 9;

    private static int clampInt(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    public static FoodDyesConfig load(FileConfiguration cfg, Logger log) {
        FoodDyesConfig c = new FoodDyesConfig();
        c.powderEffectSeconds = clampInt(cfg.getInt("powder-effect-seconds", 30), 1, 600);
        c.drinkEffectSeconds = clampInt(cfg.getInt("drink-effect-seconds", 30), 1, 600);
        c.heartCostEnabled = cfg.getBoolean("heart-cost-enabled", true);
        c.maxHeartsLost = clampInt(cfg.getInt("max-hearts-lost", 9), 0, 9);
        if (c.maxHeartsLost == 0 && c.heartCostEnabled) {
            log.info("[FoodDyes] max-hearts-lost is 0: powders cost no hearts.");
        }
        return c;
    }
}
