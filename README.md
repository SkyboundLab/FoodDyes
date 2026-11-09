# FoodDyes 1.0.0 — Paper 26.3 (also builds for 1.21.8 for testing)

Red 40, Yellow 5 and Blue 1 as edible dyes. Each dye has a signature effect:
Red 40 gives **Strength**, Yellow 5 gives **Saturation**, Blue 1 gives **Speed**.
The powder gives the effect at level I, the soda at level II (30 seconds each).

## Recipes (shapeless, discovered automatically)

| Result | Recipe |
|---|---|
| 2x Red 40 / Yellow 5 / Blue 1 powder | dye + sugar |
| 1x Red 40 / Yellow 5 / Blue 1 Soda | glass bottle + sugar + dye |

## The heart price

Eating a **powder** or drinking a **soda** costs 1 heart of max health.
The hearts come back when you die. A player can lose at most 9 hearts this
way (down to 1 heart of max health) — eating or drinking cannot kill. Lost
hearts survive relogs and are shown with `/fooddyes hearts`.

## Commands

- `/fooddyes give <player> <red40|yellow5|blue1> [powder|soda] [amount]`
- `/fooddyes hearts [player]`
- `/fooddyes reload` — reloads config.yml (and re-registers recipes)
- `/fooddyes debug` — version, config, recipes, online players' lost hearts
- Alias: `/fd`

## Install

1. **Remove the old Red 40 datapack** (`Red40_26.3.zip`) from the world's
   `datapacks/` folder — the plugin replaces it, and keeping both would
   register the Red 40 recipes twice with different effects. If you installed
   the `FoodDyes-Advancements_26.3.zip` companion datapack, remove that too —
   the plugin now includes the advancement itself, and having both would
   define the same advancement twice.
2. Stop the server, drop `FoodDyes-1.0.0.jar` in `plugins/`, start the server.
   Never `/reload` for a new jar.

## Notes

- The dyes are dye items underneath, so powders work in every vanilla recipe
  that takes that dye (wool, terracotta, concrete powder...). The same
  type-only matching means the soda recipes also accept plain dye.
- Items from the old datapack (Red 40 with `custom_data`) do **not** get the
  new effects or the heart price — they lack the plugin's marker. Craft fresh
  ones; the old ones are still just red dye.
- The "Artificially Flavoured" advancement is built into the plugin: the jar
  carries a datapack (`FoodDyes/advancements`, registered through Paper's
  Datapack API) and the plugin awards it when any food dye is eaten or
  drunk — any powder or soda of Red 40, Yellow 5 or Blue 1 (+10 XP). It is
  a real vanilla advancement — same toast, chat announcement and screen as
  the datapack version — so no extra library or companion datapack is
  needed. The id is unchanged (`red40:eat_red40`), so earned progress
  carries over. (The criterion uses the `impossible` trigger and is granted
  by the plugin: a datapack `consume_item` trigger cannot express "any of
  the six items" — its `custom_data` predicate matches the whole compound
  exactly, and multi-criterion OR requirements do not complete on one
  criterion.)

## Building

`mvn package` builds the 26.3 jar (Paper API `26.3.build.244-stable`, Java 25).
`mvn -Ptest1218 package` builds the same source against Paper 1.21.8 for
bot testing.
