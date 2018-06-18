package com.nuno1212s.ferreiro.commands;

import com.nuno1212s.ferreiro.inventories.ConfirmInventory;
import com.nuno1212s.ferreiro.main.Main;
import com.nuno1212s.ferreiro.util.RepairCost;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.NBTDataStorage.NBTCompound;
import com.nuno1212s.util.Pair;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Repair command
 */
public class RepairCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;

            ItemStack itemInHand = p.getItemInHand();
            if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                MainData.getIns().getMessageManager().getMessage("NO_ITEM_IN_HAND").sendTo(p);
                return true;
            }

            if (!isTool(itemInHand.getType())) {
                MainData.getIns().getMessageManager().getMessage("TOOLS_ONLY").sendTo(p);
                return true;
            }

            if (itemInHand.getDurability() == 0) {
                MainData.getIns().getMessageManager().getMessage("ITEM_REPAIRED").sendTo(p);
                return true;
            }

            NBTCompound nbt = new NBTCompound(itemInHand);
            int repairTimes;

            if (nbt.getValues().containsKey("RepairTimes")) {
                repairTimes = (int) nbt.getValues().get("RepairTimes");
            } else {
                repairTimes = 0;
            }

            Pair<Integer, Boolean> repairCost = RepairCost.getRepairCost(repairTimes);

            PlayerData d = MainData.getIns().getPlayerManager().getPlayer(p.getUniqueId());

            ConfirmInventory c = new ConfirmInventory(repairCost, itemInHand, (o) -> {
                if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR || !p.getItemInHand().isSimilar(itemInHand)) {
                    MainData.getIns().getMessageManager().getMessage("REPAIR_ERROR").sendTo(p);
                    return;
                }

                if (repairCost.getValue()) {
                    if (d.removeCash(repairCost.getKey())) {

                        repair(itemInHand, p, repairTimes, repairCost);

                    } else {

                        MainData.getIns().getMessageManager().getMessage("NO_CASH")
                                .format("%cash%", repairCost.getKey())
                                .sendTo(d);

                    }

                } else {

                    MainData.getIns().getServerCurrencyHandler().removeCurrency(d, repairCost.getKey())
                            .thenAccept((completed) -> {

                                if (completed) {
                                    repair(itemInHand, p, repairTimes, repairCost);
                                } else {
                                    MainData.getIns().getMessageManager().getMessage("NO_COINS")
                                            .format("%coins%", repairCost.getKey())
                                            .sendTo(d);
                                }

                            });
                }

            });

            Main.getIns().addInventory(p.getUniqueId(), c);
            p.openInventory(c.getInv());

        }
        return true;
    }

    void repair(ItemStack itemInHand, Player p, int repairTimes, Pair<Integer, Boolean> repairCost) {
        itemInHand.setDurability((short) 0);
        repairTimes++;
        NBTCompound nbtCompound = new NBTCompound(itemInHand);
        nbtCompound.add("RepairTimes", repairTimes);
        ItemStack write = nbtCompound.write(itemInHand);
        p.setItemInHand(write);
        MainData.getIns().getMessageManager()
                .getMessage("REPAIRED_ITEM" + (repairCost.getValue() ? "_CASH" : "_COINS"))
                .format("%price%", String.valueOf(repairCost.getKey())).sendTo(p);
    }

    boolean isTool(Material m) {
        return m.name().contains("SWORD") || m.name().contains("PICKAXE") || m.name().contains("AXE") || m.name().contains("HOE")
                || m.name().contains("CHESTPLATE") || m.name().contains("LEGGINGS") || m.name().contains("BOOTS")
                || m.name().contains("HELMET") || m.name().contains("BOW");
    }
}
