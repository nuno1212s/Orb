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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class InviteInventory extends InventoryData<InventoryItem> {

    private int maxInvitesPerPage;

    public InviteInventory(File inviteInventory) {
        super(inviteInventory, InventoryItem.class, true);

        this.maxInvitesPerPage = this.getInventorySize() - 9;

        setOpenFuction((inv) -> {
            inv.getKey().openInventory(inv.getValue().buildInventory((Player) inv.getKey()));
        });
    }

    @Override
    public Inventory buildInventory(Player player) {

        PlayerData player1 = MainData.getIns().getPlayerManager().getPlayer(player.getUniqueId());

        if (player1 instanceof ClanPlayer) {
            return buildInventory((ClanPlayer) player1);
        }

        return super.buildInventory(player);
    }

    public Inventory buildInventory(ClanPlayer player) {

        Inventory i = Bukkit.getServer().createInventory(null, this.inventorySize, this.inventoryName);

        int j = 0;

        for (String invite : player.getInvites()) {
            Clan c = ClanMain.getIns().getClanManager().getClan(invite);

            if (c == null) {
                player.removeInvite(invite);

                continue;
            }

            Map<String, String> formats = new HashMap<>();

            formats.put("%clanName%", c.getClanName());
            formats.put("%clanKills%", String.valueOf(c.getKills()));
            formats.put("%clanDeaths%", String.valueOf(c.getDeaths()));
            formats.put("%clanKDR%", String.format(".2%f", ((float) c.getKills()) / c.getDeaths()));
            formats.put("%clanKDD%", String.valueOf(c.getKills() - c.getDeaths()));

            ItemStack inviteItem = ClanMain.getIns().getInventoryManager().getInviteItem().clone();

            NBTCompound compound = new NBTCompound(inviteItem);

            compound.add("Clan", c.getClanID());
            compound.add("INVITE_ITEM", 1);

            i.setItem(j++, ItemUtils.formatItem(compound.write(inviteItem), formats));

            if (j >= this.maxInvitesPerPage) {
                break;
            }
        }

        for (InventoryItem item : items) {
            i.setItem(item.getSlot(), item.getItem());
        }

        return i;
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        e.setResult(Event.Result.DENY);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        NBTCompound nbt = new NBTCompound(e.getCurrentItem());

        if (!nbt.getValues().containsKey("INVITE_ITEM")) {
            return;
        }

        PlayerData playerData = MainData.getIns().getPlayerManager().getPlayer(e.getWhoClicked().getUniqueId());

        if (playerData instanceof ClanPlayer) {

            String clan = (String) nbt.getValues().get("Clan");

            if (e.getClick() == ClickType.SHIFT_RIGHT) {

                ((ClanPlayer) playerData).removeInvite(clan);

                Clan c = ClanMain.getIns().getClanManager().getClan(clan);

                if (c == null) {
                    return;
                }

                ((ClanPlayer) playerData).setClan(c.getClanID());

                c.addMember(playerData.getPlayerID());

                e.getWhoClicked().closeInventory();
                e.getWhoClicked().openInventory(ClanMain.getIns().getInventoryManager().getMainInventory(playerData).buildInventory(playerData));

            } else if (e.getClick() == ClickType.SHIFT_LEFT) {

                ((ClanPlayer) playerData).removeInvite(clan);

                e.getInventory().setContents(buildInventory((ClanPlayer) playerData).getContents());

            }
        }
    }
}
