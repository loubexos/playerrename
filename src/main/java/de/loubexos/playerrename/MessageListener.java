package de.loubexos.playerrename;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/** Maskiert den Namen zusätzlich im Chat sowie in Join-/Quit-Nachrichten. */
public final class MessageListener implements Listener {

    private final PlayerRenamePlugin plugin;

    public MessageListener(PlayerRenamePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        NameMasker masker = plugin.masker();
        if (!masker.maskChat()) return;

        ChatRenderer original = event.renderer();
        event.renderer((source, sourceDisplayName, message, viewer) -> {
            Player viewingPlayer = viewer instanceof Player p ? p : null;
            Component name = masker.shouldMask(viewingPlayer, source)
                    ? Component.text(masker.fakeName())
                    : sourceDisplayName;
            return original.render(source, name, message, viewer);
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        NameMasker masker = plugin.masker();
        if (!masker.maskJoinQuit() || event.joinMessage() == null) return;
        if (masker.shouldMaskGlobally(event.getPlayer())) {
            event.joinMessage(Component.translatable("multiplayer.player.joined",
                    NamedTextColor.YELLOW, Component.text(masker.fakeName())));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        NameMasker masker = plugin.masker();
        if (!masker.maskJoinQuit() || event.quitMessage() == null) return;
        if (masker.shouldMaskGlobally(event.getPlayer())) {
            event.quitMessage(Component.translatable("multiplayer.player.left",
                    NamedTextColor.YELLOW, Component.text(masker.fakeName())));
        }
    }
}
