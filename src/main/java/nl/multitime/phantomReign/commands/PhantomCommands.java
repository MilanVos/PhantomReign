package nl.multitime.phantomReign.commands;

import nl.multitime.phantomReign.PhantomReign;
import nl.multitime.phantomReign.models.PhantomClass;
import nl.multitime.phantomReign.models.PlayerPhantomData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PhantomCommands implements CommandExecutor, TabCompleter {

    private final PhantomReign plugin;
    private final List<String> subCommands = Arrays.asList(
        "status", "attack", "defend", "follow", "stay", "help"
    );

    public PhantomCommands(PhantomReign plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§8[§5Phantom Reign§8] §cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            showHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "status":
                showStatus(player);
                break;
            case "attack":
            case "defend":
            case "follow":
            case "stay":
                plugin.getPhantomManager().commandPhantoms(player, subCommand);
                break;
            case "help":
                showHelp(player);
                break;
            default:
                player.sendMessage("§8[§5Phantom Reign§8] §cUnknown command. Use /phantom help for a list of commands.");
                break;
        }

        return true;
    }

    private void showStatus(Player player) {
        PlayerPhantomData data = plugin.getPhantomManager().getPlayerData(player.getUniqueId());

        player.sendMessage("§8§m-----------------------------------------------------");
        player.sendMessage("§8[§5Phantom Reign§8] §fYour Phantom Status:");
        player.sendMessage("§8§m-----------------------------------------------------");
        player.sendMessage("§8» §fDeath Count: §5" + data.getDeathCount());
        player.sendMessage("§8» §fTotal Phantoms: §5" + data.getPhantoms().size());

        for (PhantomClass phantomClass : PhantomClass.values()) {
            int count = data.getPhantomCountByClass(phantomClass);
            if (count > 0) {
                player.sendMessage("§8» §f" + phantomClass.getColoredName() + "§f: §5" + count);
            }
        }

        player.sendMessage("§8§m-----------------------------------------------------");
    }

    private void showHelp(Player player) {
        player.sendMessage("§8§m-----------------------------------------------------");
        player.sendMessage("§8[§5Phantom Reign§8] §fCommand Help:");
        player.sendMessage("§8§m-----------------------------------------------------");
        player.sendMessage("§8» §5/phantom status §8- §fView your phantom army status");
        player.sendMessage("§8» §5/phantom attack §8- §fCommand your phantoms to attack nearby players");
        player.sendMessage("§8» §5/phantom defend §8- §fCommand your phantoms to defend you");
        player.sendMessage("§8» §5/phantom follow §8- §fCommand your phantoms to follow you");
        player.sendMessage("§8» §5/phantom stay §8- §fCommand your phantoms to stay in place");
        player.sendMessage("§8» §5/phantom help §8- §fShow this help message");
        player.sendMessage("§8§m-----------------------------------------------------");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String partialCommand = args[0].toLowerCase();
            return subCommands.stream()
                .filter(cmd -> cmd.startsWith(partialCommand))
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}