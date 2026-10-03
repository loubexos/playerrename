package de.loubexos.playerrename;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Ändert den Namen im GameProfile jedes Tab-Listen-Eintrags.
 * Der Client nutzt diesen Namen für Nametag über dem Kopf und Tab-Liste.
 */
public final class PlayerInfoListener extends PacketListenerAbstract {

    private final PlayerRenamePlugin plugin;

    public PlayerInfoListener(PlayerRenamePlugin plugin) {
        super(PacketListenerPriority.NORMAL);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.PLAYER_INFO_UPDATE) return;

        WrapperPlayServerPlayerInfoUpdate wrapper = new WrapperPlayServerPlayerInfoUpdate(event);
        // Das GameProfile (inkl. Name) wird nur bei ADD_PLAYER gesendet
        if (!wrapper.getActions().contains(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER)) return;

        UUID viewerId = event.getUser().getUUID();
        Player viewer = viewerId == null ? null : Bukkit.getPlayer(viewerId);
        NameMasker masker = plugin.masker();

        boolean changed = false;
        for (WrapperPlayServerPlayerInfoUpdate.PlayerInfo info : wrapper.getEntries()) {
            UserProfile profile = info.getGameProfile();
            if (profile == null) continue;

            Player target = Bukkit.getPlayer(profile.getUUID());
            if (masker.shouldMask(viewer, target)) {
                profile.setName(masker.fakeName());
                changed = true;
            }
        }

        if (changed) {
            event.markForReEncode(true);
        }
    }
}
