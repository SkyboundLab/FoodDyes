package com.fooddyes;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

// The three food dyes: Red 40 -> Strength, Yellow 5 -> Saturation, Blue 1 ->
// Speed. Powder gives the effect at level I, soda at level II.
public enum FoodDye {

    RED40("red40", "Red 40", Material.RED_DYE, NamedTextColor.RED,
            PotionEffectType.STRENGTH, "Strength", Color.fromRGB(0xFF0000)),
    YELLOW5("yellow5", "Yellow 5", Material.YELLOW_DYE, NamedTextColor.YELLOW,
            PotionEffectType.SATURATION, "Saturation", Color.fromRGB(0xFFFF00)),
    BLUE1("blue1", "Blue 1", Material.BLUE_DYE, NamedTextColor.BLUE,
            PotionEffectType.SPEED, "Speed", Color.fromRGB(0x0000FF));

    private final String id;
    private final String displayName;
    private final Material material;
    private final NamedTextColor textColor;
    private final PotionEffectType effectType;
    private final String effectName;
    private final Color sodaColor;

    FoodDye(String id, String displayName, Material material, NamedTextColor textColor,
            PotionEffectType effectType, String effectName, Color sodaColor) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.textColor = textColor;
        this.effectType = effectType;
        this.effectName = effectName;
        this.sodaColor = sodaColor;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public Material material() {
        return material;
    }

    public NamedTextColor textColor() {
        return textColor;
    }

    public PotionEffectType effectType() {
        return effectType;
    }

    public String effectName() {
        return effectName;
    }

    public Color sodaColor() {
        return sodaColor;
    }

    /** Soda display name, e.g. "Red 40 Soda". */
    public String sodaName() {
        return displayName + " Soda";
    }

    public static FoodDye byId(String id) {
        if (id == null) {
            return null;
        }
        for (FoodDye dye : values()) {
            if (dye.id.equalsIgnoreCase(id)) {
                return dye;
            }
        }
        return null;
    }
}
