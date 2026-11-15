# FoodDyes

Red 40, Yellow 5 and Blue 1 as edible dyes for Paper 26.3. Craft a powder or a soda, get the dye's effect, and pay one heart of max health.

| Dye | Effect |
|---|---|
| Red 40 | Strength |
| Yellow 5 | Saturation |
| Blue 1 | Speed |

Powders give the effect at level I and sodas at level II. Both last 30 seconds by default.

## Recipes

Both recipes are shapeless:

- 2x powder = dye + sugar
- 1x soda = glass bottle + sugar + dye

## The heart price

Eating a powder or drinking a soda costs 1 heart of max health. Dying gives the hearts back. A player can lose at most 9 hearts this way, so eating cannot kill them. Lost hearts survive a relog, and `/fooddyes hearts` shows the current total.

## Commands

- `/fooddyes give <player> <red40|yellow5|blue1> [powder|soda] [amount]`: give items. Needs `fooddyes.give`.
- `/fooddyes hearts [player]`: show lost hearts.
- `/fooddyes reload`: reload `config.yml` and register the recipes again. Needs `fooddyes.admin`.
- `/fooddyes debug`: show version, config, recipes and lost hearts. Needs `fooddyes.admin`.

The alias is `/fd`.

## Install

1. Remove the old `Red40_26.3.zip` datapack from the world's `datapacks/` folder, and the `FoodDyes-Advancements_26.3.zip` companion pack if you installed it. The plugin replaces both, and keeping them would register the recipes and the advancement twice.
2. Stop the server, put `FoodDyes-1.0.0.jar` in `plugins/`, and start it. A new jar needs a restart, not `/reload`.

## Notes

The dyes are dye items underneath, so a powder works in any vanilla recipe that takes that dye, such as wool, terracotta or concrete powder.

Items from the old datapack, Red 40 with `custom_data`, get no effect and cost no hearts. Craft new ones.

The "Artificially Flavoured" advancement ships inside the jar as a bundled datapack and is awarded when a player consumes any of the six items. It gives 10 XP. The id is unchanged (`red40:eat_red40`), so progress carries over.

## Build

`mvn package` builds the 26.3 jar against Paper API `26.3.build.244-stable` with Java 25. `mvn -Ptest1218 package` builds the same source against Paper 1.21.8.
