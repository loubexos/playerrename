package de.loubexos.playerrename;

import com.github.retrooper.packetevents.PacketEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class PlayerRenamePlugin extends JavaPlugin {

    public static final String BYPASS_PERMISSION = "playerrename.bypass";

    private volatile NameMasker masker;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        masker = new NameMasker(getConfig());

        PacketEvents.getAPI().getEventManager().registerListener(new PlayerInfoListener(this));
        getServer().getPluginManager().registerEvents(new MessageListener(this), this);
    }

    public NameMasker masker() {
        return masker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            masker = new NameMasker(getConfig());
            refreshAll();
            sender.sendMessage("§aPlayerRename: Konfiguration neu geladen und Namen aktualisiert.");
            return true;
        }
        sender.sendMessage("§cVerwendung: /" + label + " reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return args.length == 1 ? List.of("reload") : List.of();
    }

    /** Sendet allen Spielern die Tab-Einträge erneut, damit neue Einstellungen/Permissions greifen. */
    private void refreshAll() {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (viewer.equals(target)) continue;
                viewer.hidePlayer(this, target);
                viewer.showPlayer(this, target);
            }
        }
    }
}
