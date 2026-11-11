package com.fooddyes;

import io.papermc.paper.datapack.Datapack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

// Red 40, Yellow 5 and Blue 1 as edible dyes. Powders (dye + sugar) give the
// effect at level I, sodas (bottle + sugar + dye) at level II. Eating one
// costs 1 heart of max health; dying gives the hearts back.
public class FoodDyesPlugin extends JavaPlugin {

    private FoodDyesConfig dyeConfig;
    private DyeItems items;
    private HeartManager hearts;
    private final List<NamespacedKey> recipeKeys = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadDyeConfig();
        items = new DyeItems(this);
        hearts = new HeartManager(this);
        registerRecipes();
        getServer().getPluginManager().registerEvents(new DyeListener(this), this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(
                        new FoodDyesCommand(this).buildRoot(),
                        "FoodDyes: Red 40, Yellow 5 and Blue 1",
                        List.of("fd")));
        for (Player player : Bukkit.getOnlinePlayers()) {
            hearts.apply(player);
            recipeKeys.forEach(player::discoverRecipe);
        }
        Datapack advPack = getServer().getDatapackManager().getPack("FoodDyes/advancements");
        if (advPack != null && advPack.isEnabled()) {
            getLogger().info("Advancement datapack loaded: FoodDyes/advancements enabled.");
        } else {
            getLogger().warning("Advancement datapack FoodDyes/advancements is not enabled; "
                    + "the 'Artificially Flavoured' advancement will not be available.");
        }
        getLogger().info("FoodDyes " + getPluginMeta().getVersion() + " enabled: "
                + recipeKeys.size() + " recipes, powder " + dyeConfig.powderEffectSeconds
                + "s, drink " + dyeConfig.drinkEffectSeconds + "s, heart cost "
                + (dyeConfig.heartCostEnabled ? "on" : "off") + ".");
    }

    /** Reload config.yml and re-register the recipes (durations are baked in). */
    public void reload() {
        reloadConfig();
        loadDyeConfig();
        for (NamespacedKey key : recipeKeys) {
            Bukkit.removeRecipe(key);
        }
        recipeKeys.clear();
        registerRecipes();
        for (Player player : Bukkit.getOnlinePlayers()) {
            hearts.apply(player);
            recipeKeys.forEach(player::discoverRecipe);
        }
        getLogger().info("FoodDyes config reloaded: " + recipeKeys.size() + " recipes.");
    }

    private void loadDyeConfig() {
        dyeConfig = FoodDyesConfig.load(getConfig(), getLogger());
    }

    private void registerRecipes() {
        for (FoodDye dye : FoodDye.values()) {
            ItemStack powder = items.powder(dye);
            powder.setAmount(2);
            NamespacedKey powderKey = new NamespacedKey(this, dye.id() + "_powder");
            ShapelessRecipe powderRecipe = new ShapelessRecipe(powderKey, powder);
            powderRecipe.addIngredient(dye.material());
            powderRecipe.addIngredient(Material.SUGAR);
            if (Bukkit.addRecipe(powderRecipe)) {
                recipeKeys.add(powderKey);
            } else {
                getLogger().warning("Could not register recipe " + powderKey + ".");
            }

            NamespacedKey sodaKey = new NamespacedKey(this, dye.id() + "_soda");
            ShapelessRecipe sodaRecipe = new ShapelessRecipe(sodaKey, items.soda(dye));
            sodaRecipe.addIngredient(Material.GLASS_BOTTLE);
            sodaRecipe.addIngredient(Material.SUGAR);
            sodaRecipe.addIngredient(dye.material());
            if (Bukkit.addRecipe(sodaRecipe)) {
                recipeKeys.add(sodaKey);
            } else {
                getLogger().warning("Could not register recipe " + sodaKey + ".");
            }
        }
    }

    public FoodDyesConfig config() {
        return dyeConfig;
    }

    public DyeItems items() {
        return items;
    }

    public HeartManager hearts() {
        return hearts;
    }

    public List<NamespacedKey> recipeKeys() {
        return List.copyOf(recipeKeys);
    }
}
