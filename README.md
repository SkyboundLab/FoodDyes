# FoodDyes

Red 40, Yellow 5 and Blue 1 as edible dyes. Craft a powder or a soda, get the dye's effect, and pay one heart of max health. Paper 26.3.

| Dye | Effect |
|---|---|
| Red 40 | Strength |
| Yellow 5 | Saturation |
| Blue 1 | Speed |

Powders give the effect at level I, sodas at level II. Both last 30 seconds by default.

The recipes are shapeless. A dye and sugar make 2 powders. A glass bottle, sugar and a dye make 1 soda.

Each item costs 1 heart of max health, up to 9 hearts, so eating cannot kill. Dying gives the hearts back, and lost hearts survive a relog. `/fooddyes hearts` shows the current total.

## Commands

- `/fd give <player> <red40|yellow5|blue1> [powder|soda] [amount]` needs `fooddyes.give`
- `/fd hearts [player]` shows the lost hearts
- `/fd reload` and `/fd debug` need `fooddyes.admin`

The dyes are dye items underneath, so a powder works in any vanilla recipe that takes that dye. Eating any of the six items awards the bundled "Artificially Flavoured" advancement and 10 XP.
