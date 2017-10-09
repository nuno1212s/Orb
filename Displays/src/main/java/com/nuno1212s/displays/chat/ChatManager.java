package com.nuno1212s.displays.chat;

import com.nuno1212s.displays.DisplayMain;
import com.nuno1212s.displays.player.ChatData;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.playermanager.PlayerData;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Chat manager
 */
@SuppressWarnings("unchecked")
public class ChatManager {

    private File configFile;

    @Getter
    @Setter
    private boolean chatActivated = true;

    @Getter
    private boolean localChatActivated = true;

    @Getter
    private String separator = " » ";

    @Getter
    private long chatTimerGlobal = 15000, chatTimerLocal = 3000, range = (long) Math.pow(25L, 2);

    private String localChatFormatting, globalChatFormatting;

    private List<String> hoverElements;

    public ChatManager(Module m) {
        configFile = m.getFile("chatConfig.json", true);

        loadConfig();
    }

    public void loadConfig() {
        JSONObject config;

        try (FileReader r = new FileReader(configFile)) {
            config = (JSONObject) new JSONParser().parse(r);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        this.chatActivated = (Boolean) config.getOrDefault("ChatActivated", true);
        this.separator = (String) config.getOrDefault("Separator", " » ");
        this.chatTimerGlobal = (Long) config.getOrDefault("ChatCooldownGlobal", 15) * 1000;
        this.chatTimerLocal = (Long) config.getOrDefault("ChatCooldownLocal", 3) * 1000;
        this.localChatActivated = (Boolean) config.getOrDefault("LocalChatActivated", true);
        this.range = (Long) config.getOrDefault("Range", 25);

        this.localChatFormatting = ChatColor.translateAlternateColorCodes('&',
                (String) config.getOrDefault("LocalChatFormatting", "&e[l] %playerName% » %message%"));
        this.globalChatFormatting = ChatColor.translateAlternateColorCodes('&',
                (String) config.getOrDefault("GlobalChatFormatting", "&7[g] %playerName% » %message%"));

        if (!(localChatFormatting.contains("%playerName%") && globalChatFormatting.contains("%playerName%"))) {
            throw new IllegalArgumentException("Chat formatting must contain the player name");
        }

        this.hoverElements = new ArrayList<>();

        List<String> unformattedElements = (JSONArray) config.getOrDefault("HoverElements", new JSONArray());

        for (String hoverElement : unformattedElements) {
            hoverElements.add(ChatColor.translateAlternateColorCodes('&', hoverElement));
        }

        this.range *= this.range;
    }

    private String getHoverText(PlayerData data) {
        List<String> hoverElements = this.hoverElements;

        StringBuilder builder = new StringBuilder();

        int current = 0;

        for (String hoverElement : hoverElements) {
            builder.append(DisplayMain.getIns().getPlaceHolderManager().format(hoverElement, data));
            if (++current < hoverElements.size())
                builder.append("\n");
        }

        return builder.toString();
    }

    /**
     * Send a message to the server
     *
     * @param message  The message
     * @param data     The player who sent it
     * @param original The original location it was sent from
     * @param global   Is it a global message
     * @return True if someone heard the message, false if not
     */
    public boolean sendMessage(String message, PlayerData data, Location original, boolean global) {
        if (global) {
            String[] split = this.globalChatFormatting.split("%playerName%");
            String firstMessagePart = split[0];
            String secondMessagePart = split[1].replace("%message%", message);

            BaseComponent[] messageParts = TextComponent.fromLegacyText(firstMessagePart), messageParts2 = TextComponent.fromLegacyText(secondMessagePart);

            BaseComponent[] baseComponents = new ComponentBuilder(data.getNameWithPrefix())
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(getHoverText(data)))).create();

            List<BaseComponent> components = new ArrayList<>();
            components.addAll(Arrays.asList(messageParts));
            components.addAll(Arrays.asList(baseComponents));
            components.addAll(Arrays.asList(messageParts2));

            BaseComponent[] finalComponents = components.toArray(new BaseComponent[components.size()]);

            for (Player p : Bukkit.getOnlinePlayers()) {

                if (p.getUniqueId().equals(data.getPlayerID())) {
                    p.spigot().sendMessage(finalComponents);
                    continue;
                }

                PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

                if (playerData instanceof ChatData) {
                    if (!((ChatData) playerData).shouldReceive()) {
                        continue;
                    }
                }

                p.spigot().sendMessage(finalComponents);
            }

            return true;
        } else {
            String[] split = this.localChatFormatting.split("%playerName%");
            String firstMessagePart = split[0];
            String secondMessagePart = split[1].replace("%message%", message);

            BaseComponent[] messageParts = TextComponent.fromLegacyText(firstMessagePart), messageParts2 = TextComponent.fromLegacyText(secondMessagePart);

            BaseComponent[] baseComponents = new ComponentBuilder(data.getNameWithPrefix())
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(getHoverText(data)))).create();

            List<BaseComponent> components = new ArrayList<>();
            components.addAll(Arrays.asList(messageParts));
            components.addAll(Arrays.asList(baseComponents));
            components.addAll(Arrays.asList(messageParts2));

            BaseComponent[] finalComponents = components.toArray(new BaseComponent[components.size()]);

            boolean heard = false;

            for (Player p : original.getWorld().getPlayers()) {
                if (p.getUniqueId().equals(data.getPlayerID())) {
                    p.spigot().sendMessage(finalComponents);
                    continue;
                }

                PlayerData player = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

                if (player instanceof ChatData) {
                    if (!((ChatData) player).shouldReceive()) {
                        continue;
                    }
                }

                if (p.getLocation().distanceSquared(original) < this.range) {
                    heard = true;
                    p.spigot().sendMessage(finalComponents);
                }
            }

            return heard;
        }
    }

}
