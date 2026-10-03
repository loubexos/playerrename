package de.loubexos.playerrename;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/** Entscheidet, ob ein Spielername für einen bestimmten Betrachter maskiert wird. */
public final class NameMasker {

    public enum Mode { TARGET, VIEWER, BOTH }

    private final String fakeName;
    private final Mode mode;
    private final boolean maskChat;
    private final boolean maskJoinQuit;

    public NameMasker(FileConfiguration config) {
        String name = config.getString("display-name", "Player");
        if (name.isBlank()) name = "Player";
        // Minecraft-Profilnamen sind maximal 16 Zeichen lang
        this.fakeName = name.length() > 16 ? name.substring(0, 16) : name;

        Mode parsed;
        try {
            parsed = Mode.valueOf(config.getString("bypass-mode", "TARGET").toUpperCase());
        } catch (IllegalArgumentException e) {
            parsed = Mode.TARGET;
        }
        this.mode = parsed;
        this.maskChat = config.getBoolean("mask-chat", true);
        this.maskJoinQuit = config.getBoolean("mask-join-quit", true);
    }

    public String fakeName() { return fakeName; }
    public boolean maskChat() { return maskChat; }
    public boolean maskJoinQuit() { return maskJoinQuit; }

    /** Wird {@code target} für {@code viewer} umbenannt? Unbekannte Spieler (null) werden maskiert. */
    public boolean shouldMask(@Nullable Player viewer, @Nullable Player target) {
        boolean targetExempt = target != null && target.hasPermission(PlayerRenamePlugin.BYPASS_PERMISSION);
        boolean viewerExempt = viewer != null && viewer.hasPermission(PlayerRenamePlugin.BYPASS_PERMISSION);
        return switch (mode) {
            case TARGET -> !targetExempt;
            case VIEWER -> !viewerExempt;
            case BOTH -> !(targetExempt || viewerExempt);
        };
    }

    /** Für Nachrichten ohne bestimmten Betrachter (z. B. Join/Quit). */
    public boolean shouldMaskGlobally(Player target) {
        if (mode == Mode.VIEWER) return false;
        return !target.hasPermission(PlayerRenamePlugin.BYPASS_PERMISSION);
    }
}
