# FoodDyes

Red 40, Yellow 5 and Blue 1 as edible dyes for Paper 26.3. Craft a powder or a soda, get the dye's effect, pay a heart.

| Dye | Effect |
|---|---|
| Red 40 | Strength |
| Yellow 5 | Saturation |
| Blue 1 | Speed |

Powders give the effect at level I, sodas at level II (30 seconds each by default).

## Recipes (shapeless)

- 2x powder = dye + sugar
- 1x soda = glass bottle + sugar + dye

## The heart price

Eating a powder or drinking a soda costs 1 heart of max health. Dying gives the hearts back. A player can lose at most 9 hearts this way (down to 1 heart), so eating cannot kill. Lost hearts survive relogs and show with `/fooddyes hearts`.

## Commands

- `/fooddyes give <player> <red40|yellow5|blue1> [powder|soda] [amount]`
- `/fooddyes hearts [player]`
- `/fooddyes reload`: reload config.yml and re-register recipes
- `/fooddyes debug`: version, config, recipes, lost hearts
- Alias: `/fd`

## Install

1. Remove the old `Red40_26.3.zip` datapack from the world's `datapacks/` folder, and the `FoodDyes-Advancements_26.3.zip` companion pack if you installed it. The plugin replaces both; keeping them would register the recipes and the advancement twice.
2. Stop the server, drop `FoodDyes-1.0.0.jar` in `plugins/`, start it. Never `/reload` to load a new jar.

## Notes

- The dyes are dye items underneath, so powders work in any vanilla recipe that takes that dye (wool, terracotta, concrete powder).
- Items from the old datapack (Red 40 with `custom_data`) get no effect and no heart cost. Craft fresh ones.
- The "Artificially Flavoured" advancement ships inside the jar as a bundled datapack and is awarded when any of the six items is consumed (+10 XP). The id is unchanged (`red40:eat_red40`), so progress carries over.

## Build

`mvn package` builds the 26.3 jar (Paper API `26.3.build.244-stable`, Java 25). `mvn -Ptest1218 package` builds the same source against Paper 1.21.8.
