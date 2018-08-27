package com.nuno1212s.events.war.inventories;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.events.EventMain;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.SerializableItem;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class SelectPlayersInventory extends InventoryData<InventoryItem> implements Listener {

    @Getter
    private Map<UUID, List<UUID>> selected;

    private ItemStack selectedItem, selectItem;

    public SelectPlayersInventory(File jsonFile) {
        super(jsonFile, InventoryItem.class, true);

        this.selected = new HashMap<>();

        try (Reader r = new FileReader(jsonFile)) {

            JSONObject json = (JSONObject) new JSONParser().parse(r);

            this.selectedItem = new SerializableItem((JSONObject) json.getOrDefault("SelectedItem", new JSONObject()));
            this.selectItem = new SerializableItem((JSONObject) json.getOrDefault("SelectItem", new JSONObject()));

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Inventory buildInventory(Player p) {

        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

        if (!(playerData instanceof ClanPlayer)) {
            return null;
        }

        Clan clan = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) playerData).getClan());

        List<UUID> onlineMembers = clan.getOnlineMembers();

        Inventory inventory = super.buildInventory(p);

        int slot = 0;

        for (UUID onlineMember : onlineMembers) {
            Player onlinePlayer = Bukkit.getPlayer(onlineMember);

            ItemStack item = (this.selected.containsKey(p.getUniqueId()) ?
                    (this.selected.get(p.getUniqueId()).contains(onlineMember) ? this.selectedItem : this.selectItem)
                    : this.selectItem);

            item = item.clone();

            NBTCompound nbt = new NBTCompound(item);

            nbt.add("UUID", onlineMember.toString());

            Map<String, String> placeHolders = new HashMap<>();

            placeHolders.put("%playerName%", onlinePlayer.getName());

            ItemStack writtenItem = nbt.write(item);

            inventory.setItem(slot++, ItemUtils.formatItem(writtenItem, placeHolders));

        }

        return inventory;
    }

    @Override
    public void handleClick(InventoryClickEvent e) {

        e.setResult(Event.Result.DENY);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        InventoryItem item = getItem(e.getSlot());

        if (item == null) {

            NBTCompound nbt = new NBTCompound(e.getCurrentItem());

            if (nbt.getValues().containsKey("UUID")) {

                UUID playerID = UUID.fromString((String) nbt.getValues().get("UUID"));

                if (this.selected.containsKey(e.getWhoClicked().getUniqueId())) {

                    List<UUID> uuids = this.selected.get(e.getWhoClicked().getUniqueId());

                    if (uuids.contains(playerID)) {
                        uuids.remove(playerID);
                    } else {
                        if (uuids.size() >= 10) {

                            MainData.getIns().getMessageManager().getMessage("YOU_CANNOT_SELECT_ANYMORE")
                                    .sendTo(e.getWhoClicked());

                            return;

                        }

                        uuids.add(playerID);
                    }

                    e.getClickedInventory().setContents(buildInventory((Player) e.getWhoClicked()).getContents());
                } else {

                    List<UUID> uuids = new ArrayList<>();

                    uuids.add(playerID);

                    this.selected.put(e.getWhoClicked().getUniqueId(), uuids);
                }

            }

        } else {

            if (item.hasItemFlag("ACCEPT")) {

                PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

                if (playerData instanceof ClanPlayer) {
                    EventMain.getIns().getWarEvent().registerClan(((ClanPlayer) playerData).getClan(), this.selected.get(e.getWhoClicked().getUniqueId()));

                    e.getWhoClicked().closeInventory();
                }

            } else if (item.hasItemFlag("CANCEL")) {
                e.getWhoClicked().closeInventory();
            }

        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (this.equals(e.getInventory())) {
            this.selected.remove(e.getPlayer().getUniqueId());
        }
    }
}
