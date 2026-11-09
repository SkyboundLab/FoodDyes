package com.fooddyes;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

/**
 * Builds the dye items and recognises them again later. Every FoodDyes item
 * carries two PersistentDataContainer tags: the dye id ("red40", "yellow5",
 * "blue1") and the kind ("powder" or "soda"). The effects come from the
 * item's own data components, exactly like the datapack did: the powder is
 * a food with a consumable that applies its dye's effect at level I, the
 * soda is a potion whose custom effects apply its dye's effect at level II.
 */
public class DyeItems {

    public static final String KIND_POWDER = "powder";
    public static final String KIND_SODA = "soda";

    private final FoodDyesPlugin plugin;
    private final NamespacedKey dyeKey;
    private final NamespacedKey kindKey;

    public DyeItems(FoodDyesPlugin plugin) {
        this.plugin = plugin;
        this.dyeKey = new NamespacedKey(plugin, "dye");
        this.kindKey = new NamespacedKey(plugin, "kind");
    }

    /** One serving of the powder: edible dye, effect level I. */
    public ItemStack powder(FoodDye dye) {
        ItemStack stack = new ItemStack(dye.material());
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text(dye.displayName(), dye.textColor())
                .decoration(TextDecoration.BOLD, true));
        meta.lore(List.of(
                Component.text("Synthetic food colouring. Tastes like trouble.",
                                NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Costs 1 heart per bite — you get it back when you die.",
                                NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false)));
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(dyeKey, PersistentDataType.STRING, dye.id());
        pdc.set(kindKey, PersistentDataType.STRING, KIND_POWDER);
        stack.setItemMeta(meta);

        int ticks = plugin.config().powderEffectSeconds * 20;
        PotionEffect effect = new PotionEffect(dye.effectType(), ticks, 0);
        stack.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(1).saturation(0.5f).canAlwaysEat(true).build());
        stack.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds(0.8f)
                .animation(ItemUseAnimation.EAT)
                .hasConsumeParticles(true)
                .addEffect(ConsumeEffect.applyStatusEffects(List.of(effect), 1.0f))
                .build());
        return stack;
    }

    /** One bottle of the soda: a potion with the dye's effect at level II. */
    public ItemStack soda(FoodDye dye) {
        ItemStack stack = new ItemStack(Material.POTION);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text(dye.sodaName(), dye.textColor())
                .decoration(TextDecoration.BOLD, true));
        meta.lore(List.of(
                Component.text(dye.effectName() + " II for "
                                + plugin.config().drinkEffectSeconds + " seconds.",
                                NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Costs 1 heart per drink — you get it back when you die.",
                                NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false)));
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(dyeKey, PersistentDataType.STRING, dye.id());
        pdc.set(kindKey, PersistentDataType.STRING, KIND_SODA);
        stack.setItemMeta(meta);

        int ticks = plugin.config().drinkEffectSeconds * 20;
        PotionEffect effect = new PotionEffect(dye.effectType(), ticks, 1);
        stack.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents()
                .customColor(dye.sodaColor())
                .addCustomEffect(effect)
                .build());
        return stack;
    }

    /** What a stack is, if it is one of ours; null otherwise. */
    public Identified identify(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return null;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return null;
        }
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String id = pdc.get(dyeKey, PersistentDataType.STRING);
        String kind = pdc.get(kindKey, PersistentDataType.STRING);
        if (id == null || kind == null) {
            return null;
        }
        FoodDye dye = FoodDye.byId(id);
        if (dye == null) {
            return null;
        }
        if (!KIND_POWDER.equals(kind) && !KIND_SODA.equals(kind)) {
            return null;
        }
        return new Identified(dye, kind);
    }

    public record Identified(FoodDye dye, String kind) {
        public boolean isPowder() {
            return KIND_POWDER.equals(kind);
        }
    }
}
