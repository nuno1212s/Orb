package com.nuno1212s.clans.inventories;

import com.google.common.collect.ImmutableMap;
import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.clans.events.ClanPlayerKickEvent;
import com.nuno1212s.events.PlayerInformationUpdateEvent;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MemberInventory extends InventoryData<MemberItem> {

    public MemberInventory(File jsonFile) {
        super(jsonFile, MemberItem.class, true);

        setOpenFuction((pair) -> {

            HumanEntity player = pair.getKey();

            if (pair.getValue() instanceof ClanInventory) {

                PlayerData player1 = MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId());
                player.openInventory(((ClanInventory) pair.getValue()).buildInventory(player1));

            } else {

                player.openInventory(pair.getValue().buildInventory());

            }

        });
    }

    public CompletableFuture<Inventory> buildMemberInventory(Clan c) {

        return CompletableFuture.supplyAsync(() -> {

            Inventory i = Bukkit.getServer().createInventory(null, this.inventorySize, this.inventoryName);

            int slot = 0;

            ImmutableMap<UUID, Clan.Rank> members = c.getMembersSorted();

            for (UUID uuid : members.keySet()) {
                Pair<PlayerData, Boolean> orLoadPlayer = MainData.getIns().getPlayerManager().getOrLoadPlayer(uuid);

                if (orLoadPlayer.getValue()) {
                    orLoadPlayer.setKey(MainData.getIns().getPlayerManager().requestAditionalServerData(orLoadPlayer.getKey()));
                }

                PlayerData player = orLoadPlayer.getKey();

                Map<String, String> formats = new LinkedHashMap<>();

                formats.put("%playerName%", player.getPlayerName());

                if (player instanceof ClanPlayer) {

                    ClanPlayer cP = (ClanPlayer) player;

                    formats.put("%kills%", String.valueOf(cP.getKills()));
                    formats.put("%deaths%", String.valueOf(cP.getDeaths()));
                    formats.put("%clanRank%", members.get(uuid).getName());
                    formats.put("%KDR%", String.format(".%2f", ((float) cP.getKills()) / cP.getDeaths()));
                    formats.put("%KDD%", String.valueOf(cP.getKills() - cP.getDeaths()));

                }

                ItemStack memberItem = ClanMain.getIns().getInventoryManager().getMemberItem().clone();

                if (memberItem.getType() == Material.SKULL_ITEM) {
                    SkullMeta meta = (SkullMeta) memberItem.getItemMeta();

                    meta.setOwner(player.getPlayerName());

                    memberItem.setItemMeta(meta);
                }

                NBTCompound nbt = new NBTCompound(memberItem);

                nbt.add("PlayerID", uuid.toString());

                i.setItem(slot++, ItemUtils.formatItem(nbt.write(memberItem), formats));
            }

            for (MemberItem item : items) {
                i.setItem(item.getSlot(), item.getItem());
            }

            return i;
        }, MainData.getIns().getAsyncExecutor());

    }

    @Override
    public void handleClick(InventoryClickEvent e) {

        e.setResult(Event.Result.DENY);

        ItemStack item = e.getCurrentItem();

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        NBTCompound nbt = new NBTCompound(item);

        Map<String, Object> values = nbt.getValues();

        if (!values.containsKey("PlayerID"))
            return;

        UUID playerID = UUID.fromString((String) values.get("PlayerID"));

        if (e.getClick() == ClickType.SHIFT_LEFT) {

            if (playerID.equals(e.getWhoClicked().getUniqueId())) {

                MainData.getIns().getMessageManager().getMessage("CANNOT_CHANGE_OWN_RANK").sendTo(e.getWhoClicked());

                return;
            }

            PlayerData player1 = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

            if (player1 instanceof ClanPlayer) {
                Clan c = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) player1).getClan());

                if (c.getRank(player1.getPlayerID()).ordinal() < Clan.Rank.ADMIN.ordinal()) {
                    MainData.getIns().getMessageManager().getMessage("NO_PERMISSION_TO_CHANGE_RANKS").sendTo(e.getWhoClicked());

                    return;
                }

                Pair<PlayerData, Boolean> player = MainData.getIns().getPlayerManager().getOrLoadPlayer(playerID);

                if (player.getValue()) {
                    player.setKey(MainData.getIns().getPlayerManager().requestAditionalServerData(player.getKey()));
                }

                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(ClanMain.getIns().getInventoryManager().getChangeRankInventory().buildInventory(player.getKey()));

            }
        } else if (e.getClick() == ClickType.SHIFT_RIGHT) {

            if (playerID.equals(e.getWhoClicked().getUniqueId())) {

                MainData.getIns().getMessageManager().getMessage("CANNOT_KICK_SELF").sendTo(e.getWhoClicked());

                return;
            }

            PlayerData player1 = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

            if (player1 instanceof ClanPlayer) {
                Clan c = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) player1).getClan());

                if (c.getRank(player1.getPlayerID()).ordinal() < Clan.Rank.MOD.ordinal()) {
                    MainData.getIns().getMessageManager().getMessage("NO_PERMISSION_TO_KICK").sendTo(e.getWhoClicked());

                    return;
                } else if (c.getRank(player1.getPlayerID()).ordinal() < c.getRank(playerID).ordinal()) {
                    MainData.getIns().getMessageManager().getMessage("CANT_KICK_SUPERIOR_RANK").sendTo(e.getWhoClicked());

                    return;
                }

                Pair<PlayerData, Boolean> player = MainData.getIns().getPlayerManager().getOrLoadPlayer(playerID);

                if (player.getValue()) {
                    player.setKey(MainData.getIns().getPlayerManager().requestAditionalServerData(player.getKey()));
                }

                Bukkit.getServer().getPluginManager().callEvent(new ClanPlayerKickEvent(c, playerID));

                c.removeMember(playerID);

                if (player.getKey() instanceof ClanPlayer) {
                    ((ClanPlayer) player.getKey()).setClan(null);

                    if (player.getValue()) {
                        player.getKey().save((o) -> { });
                    } else {
                        MainData.getIns().getEventCaller().callUpdateInformationEvent(player.getKey(), PlayerInformationUpdateEvent.Reason.OTHER);
                    }
                }

                buildMemberInventory(c).thenAccept((inv) -> e.getClickedInventory().setContents(inv.getContents()));
            }

        }
    }

}

class MemberItem extends InventoryItem {

    public MemberItem(JSONObject data) {
        super(data);
    }


}