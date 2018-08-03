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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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

        setOpenFuction((inv) -> {
            HumanEntity player = inv.getKey();
            if (inv.getValue() instanceof ClanInventory) {

                player.openInventory(((ClanInventory) inv.getValue()).buildInventory(MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId())));

            } else if (inv.getValue() instanceof MemberInventory) {

                PlayerData pD = MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId());

                if (pD instanceof ClanPlayer && ((ClanPlayer) pD).hasClan()) {

                    Clan c = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) pD).getClan());

                    if (c != null) {
                        ((MemberInventory) inv.getValue()).buildMemberInventory(c).thenAccept(player::openInventory);

                        return;
                    }
                }

                inv.getKey().openInventory(inv.getValue().buildInventory());

            } else {
                ((HumanEntity) player).openInventory(inv.getValue().buildInventory((Player) player));
            }
        });
    }

    public Inventory buildInventory(PlayerData playerData) {

        Clan.Rank rank = Clan.Rank.MEMBER;

        if (playerData instanceof ClanPlayer) {

            Clan c = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) playerData).getClan());

            if (c != null) {
                rank = c.getRank(playerData.getPlayerID());
            }

        }

        return buildInventory(playerData.getPlayerID(), rank);
    }

    public Inventory buildInventory(UUID playerID, Clan.Rank playerRank) {
        Inventory i = Bukkit.getServer().createInventory(null, this.inventorySize, this.inventoryName);

        for (RankItem item : this.items) {
            i.setItem(item.getSlot(), item.getItem(playerID, playerRank));
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

                c.setClanRank(playerID, item.getRank());

                e.getClickedInventory().setContents(buildInventory(playerID, item.getRank()).getContents());

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

        if (data.containsKey("Rank")) {
            rank = Clan.Rank.valueOf((String) data.getOrDefault("Rank", null));
        }
    }

    public ItemStack getItem(UUID player, Clan.Rank playerRank) {
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

        nbt.add("PlayerID", player.toString());

        return nbt.write(item);
    }

}