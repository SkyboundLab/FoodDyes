# FoodDyes

Red 40, Yellow 5 and Blue 1 as edible dyes. Craft a powder or a soda, get the effect, and pay one heart of max health. Paper 26.3.

| Dye | Effect |
|---|---|
| Red 40 | Strength |
| Yellow 5 | Saturation |
| Blue 1 | Speed |

Powders give the effect at level I, sodas at level II. Both last 30 seconds by default.

Both recipes are shapeless:

- 2x powder = dye + sugar
- 1x soda = glass bottle + sugar + dye

The heart price is 1 heart of max health per item, up to 9 hearts, so eating cannot kill. Dying gives the hearts back, and lost hearts survive a relog.

## Commands

- `/fd give <player> <red40|yellow5|blue1> [powder|soda] [amount]` needs `fooddyes.give`
- `/fd hearts [player]` shows lost hearts
- `/fd reload` and `/fd debug` need `fooddyes.admin`

The dyes are dye items underneath, so a powder works in any vanilla recipe that takes that dye. Items from the old Red 40 datapack get no effect and cost no hearts. The "Artificially Flavoured" advancement is bundled in the jar and gives 10 XP when a player eats or drinks one of the six items.
