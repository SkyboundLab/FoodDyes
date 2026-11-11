package com.fooddyes;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// Brigadier command tree. Paper plugins register commands through the Paper
// registrar, not plugin.yml (see FoodDyesPlugin). Subcommands:
// /fooddyes hearts [player], give <player> <dye> [kind] [amount], reload,
// debug. Dye ids: red40, yellow5, blue1. Kinds: powder, soda.
public class FoodDyesCommand {

    private final FoodDyesPlugin plugin;

    public FoodDyesCommand(FoodDyesPlugin plugin) {
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> buildRoot() {
        return Commands.literal("fooddyes")
                .executes(this::heartsSelf)
                .then(Commands.literal("give")
                        .requires(s -> s.getSender().hasPermission("fooddyes.give"))
                        .then(Commands.argument("player", StringArgumentType.word())
                                .then(Commands.argument("dye", StringArgumentType.word())
                                        .executes(ctx -> give(ctx, DyeItems.KIND_POWDER, 1))
                                        .then(Commands.argument("kind", StringArgumentType.word())
                                                .executes(ctx -> give(ctx,
                                                        ctx.getArgument("kind", String.class), 1))
                                                .then(Commands.argument("amount",
                                                                IntegerArgumentType.integer(1, 64))
                                                        .executes(ctx -> give(ctx,
                                                                ctx.getArgument("kind", String.class),
                                                                ctx.getArgument("amount", Integer.class))))))))
                .then(Commands.literal("hearts")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .executes(this::heartsOther))
                        .executes(this::heartsSelf))
                .then(Commands.literal("reload")
                        .requires(s -> s.getSender().hasPermission("fooddyes.admin"))
                        .executes(this::reload))
                .then(Commands.literal("debug")
                        .requires(s -> s.getSender().hasPermission("fooddyes.admin"))
                        .executes(this::debug))
                .build();
    }

    // ---- helpers ------------------------------------------------------

    private void send(CommandSender sender, String text) {
        sender.sendMessage(Component.text(text));
    }

    private void send(CommandSender sender, String text, NamedTextColor color) {
        sender.sendMessage(Component.text(text, color));
    }

    private Player target(CommandContext<CommandSourceStack> ctx) {
        String arg = ctx.getArgument("player", String.class);
        Player online = Bukkit.getPlayerExact(arg);
        if (online == null) {
            send(ctx.getSource().getSender(), "No online player named " + arg + ".",
                    NamedTextColor.RED);
        }
        return online;
    }

    private int give(CommandContext<CommandSourceStack> ctx, String kind, int amount) {
        CommandSender sender = ctx.getSource().getSender();
        Player player = target(ctx);
        if (player == null) {
            return 0;
        }
        FoodDye dye = FoodDye.byId(ctx.getArgument("dye", String.class));
        if (dye == null) {
            send(sender, "Unknown dye. Use red40, yellow5 or blue1.", NamedTextColor.RED);
            return 0;
        }
        if (!DyeItems.KIND_POWDER.equalsIgnoreCase(kind)
                && !DyeItems.KIND_SODA.equalsIgnoreCase(kind)) {
            send(sender, "Unknown kind. Use powder or soda.", NamedTextColor.RED);
            return 0;
        }
        ItemStack stack = DyeItems.KIND_SODA.equalsIgnoreCase(kind)
                ? plugin.items().soda(dye) : plugin.items().powder(dye);
        stack.setAmount(amount);
        player.getInventory().addItem(stack);
        String what = amount + "x " + dye.displayName()
                + (DyeItems.KIND_SODA.equalsIgnoreCase(kind) ? " Soda" : "");
        send(sender, "Gave " + what + " to " + player.getName() + ".", NamedTextColor.GREEN);
        return 1;
    }

    private int heartsSelf(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player player)) {
            send(sender, "From the console, name a player: /fooddyes hearts <player>.",
                    NamedTextColor.RED);
            return 0;
        }
        return hearts(sender, player);
    }

    private int heartsOther(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!sender.hasPermission("fooddyes.admin") && sender instanceof Player self
                && !self.getName().equalsIgnoreCase(ctx.getArgument("player", String.class))) {
            send(sender, "You can only check your own hearts.", NamedTextColor.RED);
            return 0;
        }
        Player player = target(ctx);
        if (player == null) {
            return 0;
        }
        return hearts(sender, player);
    }

    private int hearts(CommandSender sender, Player player) {
        int lost = plugin.hearts().heartsLost(player);
        double max = plugin.hearts().maxHealth(player);
        send(sender, player.getName() + " has lost " + lost + " heart"
                + (lost == 1 ? "" : "s") + " to food dyes (max health "
                + (int) max + "/20). They come back on death.");
        return 1;
    }

    private int reload(CommandContext<CommandSourceStack> ctx) {
        plugin.reload();
        send(ctx.getSource().getSender(), "FoodDyes config reloaded.", NamedTextColor.GREEN);
        return 1;
    }

    private int debug(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        FoodDyesConfig c = plugin.config();
        send(sender, "FoodDyes " + plugin.getPluginMeta().getVersion()
                + ": powder " + c.powderEffectSeconds + "s, drink " + c.drinkEffectSeconds + "s,"
                + " heart cost " + (c.heartCostEnabled ? "on" : "off")
                + ", max hearts lost " + c.maxHeartsLost
                + ", " + plugin.recipeKeys().size() + " recipes.");
        for (Player p : Bukkit.getOnlinePlayers()) {
            send(sender, "  " + p.getName() + ": lost " + plugin.hearts().heartsLost(p)
                    + " hearts, max health " + (int) plugin.hearts().maxHealth(p) + "/20.");
        }
        return 1;
    }
}
