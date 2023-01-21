package de.morice.infinitybucket.commands;

import de.morice.bukkitutils.TabCompleteUtil;
import de.morice.bukkitutils.gradient.ColorAPI;
import de.morice.infinitybucket.InfinityBucket;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiveBucketCommand implements CommandExecutor, TabCompleter {
    private final InfinityBucket plugin;

    public GiveBucketCommand(@NotNull InfinityBucket plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("infinitybucket.givebucket")) {
            return true;
        }

        switch (args.length) {
            case 0 -> {
                sender.sendMessage(ColorAPI.process("&cYou didn't provide enough arguments!"));
            }
            case 1 -> {
                // only bucket
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(ColorAPI.process("&cYou must be a player if you don't provide " +
                            "a player"));
                    return true;
                }

                this.doBucketAction(player, player, args[0]);
            }
            case 2 -> {
                final String bucket = args[0];
                final String targetName = args[1];
                final Player target = Bukkit.getPlayerExact(targetName);
                if (target == null) {
                    sender.sendMessage(ColorAPI.process("&cThis player doesn't exist!"));
                    return true;
                }
                this.doBucketAction(target, sender, bucket);
            }
            default -> {
                sender.sendMessage(ColorAPI.process("&7/&6givebucket &8(&6bucket&8) &7[&6playerName " +
                        "&8(optional&8)&7]"));
            }
        }
        return true;
    }

    private void doBucketAction(@NotNull Player receiving, @NotNull CommandSender operator, @NotNull String arg) {
        switch (arg.toLowerCase()) {
            case "lava" -> {
                receiving.getInventory().addItem(this.plugin.getInfinityLavaBucket());
                operator.sendMessage(ColorAPI.process("&aThe player has received an Infinite Lava Bucket!"));
            }
            case "water" -> {
                receiving.getInventory().addItem(this.plugin.getInfinityWaterBucket());
                operator.sendMessage(ColorAPI.process("&aThe player has received an Infinite Water Bucket!"));
            }
            case "milk", "cum" -> {
                receiving.getInventory().addItem(this.plugin.getInfinityMilkBucket());
                operator.sendMessage(ColorAPI.process("&aThe player has received an Infinite Milk " +
                        "Bucket!"));
            }
            default -> {
                operator.sendMessage(ColorAPI.process("&cThis bucket does not exist!"));
            }
        };
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("infinitybucket.givebucket")) {
            return Collections.emptyList();
        }
        final List<String> arguments = new ArrayList<>();
        if (args.length == 1) {
            // the bucket
            arguments.add("Lava");
            arguments.add("Water");
            arguments.add("Milk");
            return TabCompleteUtil.isPartial(arguments, args, 0);
        }
        Bukkit.getOnlinePlayers().forEach(player -> arguments.add(player.getName()));
        return TabCompleteUtil.isPartial(arguments, args, args.length - 1);
    }
}
