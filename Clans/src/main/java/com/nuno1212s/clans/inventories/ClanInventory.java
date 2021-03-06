package com.nuno1212s.clans.inventories;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClanInventory extends InventoryData<ClanItem> {

    public ClanInventory(File jsonFile) {
        super(jsonFile, ClanItem.class, true);

        setOpenFuction((inv) -> {

            if (inv.getValue() instanceof ClanInventory) {

                PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(inv.getKey().getUniqueId());

                inv.getKey().openInventory(((ClanInventory) inv.getValue()).buildInventory(playerData));

            } else {
                inv.getKey().openInventory(inv.getValue().buildInventory((Player) inv.getKey()));
            }

        });
    }

    @Override
    public Inventory buildInventory(Player p) {
        return buildInventory(MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId()));
    }

    private Inventory buildClanInventory(PlayerData player) {

        Inventory i = Bukkit.getServer().createInventory(null, this.inventorySize, this.inventoryName);

        if (player instanceof ClanPlayer) {

            for (ClanItem item : this.items) {
                i.setItem(item.getSlot(), item.getItem(player));
            }

        }

        return i;
    }

    public Inventory buildInventory(PlayerData player) {

        if (player instanceof ClanPlayer) {

            if (((ClanPlayer) player).hasClan()) {

                return buildClanInventory(player);

            } else {

                return buildClanlessInventory(player);

            }

        } else {

            return super.buildInventory();

        }

    }

    private Inventory buildClanlessInventory(PlayerData player) {

        Inventory i = Bukkit.getServer().createInventory(null, this.inventorySize, this.inventoryName);

        for (ClanItem item : this.items) {
            i.setItem(item.getSlot(), item.getItem(player));
        }

        return i;
    }

    @Override
    public void handleClick(InventoryClickEvent e) {

        e.setResult(Event.Result.DENY);

        ClanItem item = getItem(e.getSlot());

        if (item == null) {
            return;
        }

        if (item.hasItemFlag("CREATE")) {

            e.getWhoClicked().closeInventory();

            ClanMain.getIns().getClanManager().createClan((Player) e.getWhoClicked());

        } else if (item.hasItemFlag("INVITE_PLAYERS")) {

            PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

            if (playerData instanceof ClanPlayer && ((ClanPlayer) playerData).hasClan()) {

                Clan c = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) playerData).getClan());

                if (c.getRank(e.getWhoClicked().getUniqueId()).ordinal() < Clan.Rank.MOD.ordinal()) {

                    MainData.getIns().getMessageManager().getMessage("NO_PERMISSION_TO_INVITE").sendTo(e.getWhoClicked());

                    return;
                }

                e.getWhoClicked().closeInventory();

                ClanMain.getIns().getChatRequests().requestChatInformation((Player) e.getWhoClicked(), "SELECT_INVITED_PLAYER", (response) -> {

                    if (response.equalsIgnoreCase("cancelar")) {

                        e.getWhoClicked().openInventory(buildInventory(playerData));

                        return;
                    }

                    Player player = Bukkit.getServer().getPlayer(response);

                    if (player == null || !player.isOnline()) {

                        MainData.getIns().getMessageManager().getMessage("PLAYER_NOT_ONLINE").sendTo(e.getWhoClicked());

                        return;
                    }

                    PlayerData pD = MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId());

                    if (pD instanceof ClanPlayer) {
                        if (((ClanPlayer) pD).hasClan()) {

                            MainData.getIns().getMessageManager().getMessage("PLAYER_ALREADY_HAS_CLAN").sendTo(e.getWhoClicked());

                            return;
                        }

                        ((ClanPlayer) pD).addInvite(c.getClanID());

                        MainData.getIns().getMessageManager().getMessage("RECEIVED_INVITE")
                                .format("%clan%", c.getClanName())
                                .sendTo(pD);

                        MainData.getIns().getMessageManager().getMessage("INVITED_PLAYER")
                                .format("%playerName%", pD.getPlayerName())
                                .sendTo(e.getWhoClicked());

                        e.getWhoClicked().openInventory(buildClanInventory(playerData));
                    }

                });

            }

        } else if (item.hasItemFlag("CLAN_RANK")) {

            e.getWhoClicked().closeInventory();

            PlayerData player = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

            ClanMain.getIns().getClanManager().sendClanRanking(player);

        } else if (item.hasItemFlag("MEMBERS")) {

            PlayerData player = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

            if (player instanceof ClanPlayer) {
                if (((ClanPlayer) player).hasClan()) {

                    Clan clan = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) player).getClan());

                    ClanMain.getIns().getInventoryManager().getMemberInventory().buildMemberInventory(clan)
                            .thenAccept((inventory) -> {
                                e.getWhoClicked().closeInventory();
                                e.getWhoClicked().openInventory(inventory);
                            });

                }
            }
        } else if (item.hasItemFlag("LEAVE") && e.getClick() == ClickType.SHIFT_LEFT) {

            PlayerData player = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

            if (player instanceof ClanPlayer) {

                Clan c = ClanMain.getIns().getClanManager().getClan(((ClanPlayer) player).getClan());

                if (c.getRank(player.getPlayerID()) == Clan.Rank.OWNER) {

                    e.getWhoClicked().closeInventory();

                    ClanMain.getIns().getChatRequests().requestChatInformation((Player) e.getWhoClicked(), "CONFIRM_DELETE_CLAN",
                            (response) -> {
                                if (response.equalsIgnoreCase("SIM")) {

                                    ClanMain.getIns().getClanManager().deleteClan(c);

                                    MainData.getIns().getMessageManager().getMessage("DELETED_CLAN")
                                            .sendTo(e.getWhoClicked());

                                } else {

                                    e.getWhoClicked().openInventory(buildInventory(player));

                                }
                            });

                } else {

                    e.getWhoClicked().closeInventory();

                    ClanMain.getIns().getChatRequests().requestChatInformation((Player) e.getWhoClicked(), "CONFIRM_LEAVE_CLAN",
                            (response) -> {
                                if (response.equalsIgnoreCase("SIM")) {

                                    ((ClanPlayer) player).setClan(null);
                                    c.removeMember(player.getPlayerID());

                                    MainData.getIns().getMessageManager().getMessage("LEFT_CLAN")
                                            .sendTo(e.getWhoClicked());

                                } else {

                                    e.getWhoClicked().openInventory(buildInventory(player));

                                }
                            });

                }

            }

        }

    }
}

class ClanItem extends InventoryItem {

    public ClanItem(JSONObject data) {
        super(data);
    }

    public ItemStack getItem(PlayerData player) {

        ItemStack item = this.item.clone();

        if (item.getType() == Material.SKULL_ITEM && hasItemFlag("STATS")) {

            SkullMeta itemMeta = (SkullMeta) item.getItemMeta();

            itemMeta.setOwner(player.getPlayerName());

            item.setItemMeta(itemMeta);
        }

        Map<String, String> formats = new HashMap<>();

        formats.put("%playerName%", player.getPlayerName());

        if (player instanceof ClanPlayer) {

            ClanPlayer cP = (ClanPlayer) player;

            formats.put("%kills%", String.valueOf(cP.getKills()));
            formats.put("%deaths%", String.valueOf(cP.getDeaths()));
            formats.put("%KDR%", String.format(".%2f", ((float) cP.getKills()) / cP.getDeaths()));
            formats.put("%KDD%", String.valueOf(cP.getKills() - cP.getDeaths()));

            if (cP.hasClan()) {
                Clan c = ClanMain.getIns().getClanManager().getClan(cP.getClan());

                formats.put("%clan%", c.getClanName());
                formats.put("%clanMembers%", String.valueOf(c.getMembers().size()));
                formats.put("%clanMaxMembers%", String.valueOf(Clan.MAX_CLAN_MEMBERS));
                formats.put("%clanMembersOn%", String.valueOf(c.getOnlineMembers().size()));
                formats.put("%clanKills%", String.valueOf(c.getKills()));
                formats.put("%clanDeaths%", String.valueOf(c.getDeaths()));
                formats.put("%clanKDR%", String.format(".%2f", ((float) c.getKills()) / c.getDeaths()));
                formats.put("%clanKDD%", String.valueOf(c.getKills() - c.getDeaths()));
                formats.put("%clanRank%", c.getRank(player.getPlayerID()).getName());

                if (this.hasItemFlag("MEMBERS")) {

                    item.setAmount(c.getMembers().size());

                }

            } else {
                if (this.hasItemFlag("INVITES")) {
                    item.setAmount(cP.getInvites().size());
                }
            }
        }

        return ItemUtils.formatItem(item, formats);
    }


}
