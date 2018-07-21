package com.nuno1212s.clans.inventories;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.main.MainData;
import com.nuno1212s.messagemanager.Message;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ChangeRankInventory extends InventoryData<RankItem> {

    public ChangeRankInventory(File file) {
        super(file, RankItem.class, true);
    }

    public Inventory buildInventory(PlayerData playerData) {

        Inventory i = Bukkit.getServer().createInventory(null, this.inventorySize, this.inventoryName);

        int slot = 0;

        Clan.Rank rank = Clan.Rank.MEMBER;

        if (playerData instanceof ClanPlayer) {

            Clan c = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) playerData).getClan());

            if (c != null) {
                rank = c.getRank(playerData.getPlayerID());
            }

        }

        for (RankItem item : this.items) {
            i.setItem(slot++, item.getItem(playerData, rank));
        }

        return i;
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        e.setResult(Event.Result.DENY);

        RankItem item = getItem(e.getSlot());

        if (item == null) {
            return;
        }

        NBTCompound nbt = new NBTCompound(e.getCurrentItem());

        PlayerData player = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

        if (player instanceof ClanPlayer) {

            Clan c = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) player).getClan());

            Clan.Rank rank = c.getRank(player.getPlayerID());

            if (rank.ordinal() < Clan.Rank.ADMIN.ordinal()) {
                MainData.getIns().getMessageManager().getMessage("NO_CLAN_RANK").sendTo(e.getWhoClicked());

                return;
            } else if (item.getRank().ordinal() > rank.ordinal()) {
                MainData.getIns().getMessageManager().getMessage("NO_CLAN_RANK").sendTo(e.getWhoClicked());

                return;
            }

            UUID playerID = UUID.fromString((String) nbt.getValues().get("PlayerID"));

            if (c.getMembers().containsKey(playerID)) {

                c.setClanRank(playerID, rank);

                PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(playerID);

                if (playerData != null && playerData.isPlayerOnServer()) {
                    MainData.getIns().getMessageManager().getMessage("RANK_UPDATED")
                            .format("%rank%", item.getRank().name())
                            .format("%settingPlayer%", e.getWhoClicked().getName())
                            .sendTo(playerData);
                }

            } else {

                MainData.getIns().getMessageManager().getMessage("PLAYER_NOT_CLAN").sendTo(e.getWhoClicked());

            }

        }

    }
}

class RankItem extends InventoryItem {

    @Getter
    Clan.Rank rank;

    public RankItem(JSONObject data) {
        super(data);

        rank = Clan.Rank.valueOf((String) data.get("Rank"));
    }

    public ItemStack getItem(PlayerData player, Clan.Rank playerRank) {
        ItemStack item = getItem().clone();

        if (rank == playerRank) {

            ItemMeta meta = item.getItemMeta();

            List<String> lore = meta.getLore();

            if (lore == null) lore = new ArrayList<>();

            Message rank_selected = MainData.getIns().getMessageManager().getMessage("RANK_SELECTED");

            List<String> rank_selecteds = Arrays.asList(rank_selected.toString().split("\n"));

            lore.addAll(rank_selecteds);

            meta.setLore(lore);

            item.setItemMeta(meta);

        }

        NBTCompound nbt = new NBTCompound(item);

        nbt.add("PlayerID", player.getPlayerID().toString());

        return nbt.write(item);
    }

}