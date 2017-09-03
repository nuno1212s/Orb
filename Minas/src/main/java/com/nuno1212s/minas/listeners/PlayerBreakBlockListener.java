package com.nuno1212s.minas.listeners;

import com.nuno1212s.main.MainData;
import com.nuno1212s.minas.events.PlayerBreakBlockMineEvent;
import com.nuno1212s.minas.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Handles giving items directly to a player's inventory
 */
public class PlayerBreakBlockListener implements Listener {

    Random r = new Random();

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e) {
        Location l = e.getBlock().getLocation();
        if (Main.getIns().getMineManager().isInAMine(l)) {
            e.setCancelled(true);

            ItemStack[] drops = getDrops(e.getPlayer().getItemInHand(), e.getBlock());

            //28
            e.getPlayer().getInventory().addItem(drops);
            Bukkit.getServer().getPluginManager().callEvent(new PlayerBreakBlockMineEvent(e));
            e.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Location l = e.getBlock().getLocation();
        if (Main.getIns().getMineManager().isInAMine(l)) {
            e.setCancelled(true);
            MainData.getIns().getMessageManager().getMessage("CAN_NOT_BUILD_HERE").sendTo(e.getPlayer());
        }
    }

    private ItemStack[] getDrops(ItemStack tool, Block b) {
        if (tool.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
            if (b.getType() == Material.GLOWING_REDSTONE_ORE) {
                return new ItemStack[]{new ItemStack(Material.REDSTONE_ORE)};
            }
            return new ItemStack[]{new ItemStack(b.getType(), 1)};
        }

        Material bT = b.getType();
        if (bT == Material.COAL_ORE
                || bT == Material.DIAMOND_ORE
                || bT == Material.REDSTONE_ORE
                || bT == Material.GLOWING_REDSTONE_ORE
                || bT == Material.LAPIS_ORE
                || bT == Material.EMERALD_ORE && tool.getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {

            List<ItemStack> blockDrops = new ArrayList<>(b.getDrops());

            if (bT == Material.REDSTONE_ORE || bT == Material.GLOWING_REDSTONE_ORE) {
                Material type = b.getDrops().iterator().next().getType();
                blockDrops = Arrays.asList(new ItemStack(type), new ItemStack(type), new ItemStack(type));
            } else if (bT == Material.COAL_ORE) {
                Material type = b.getDrops().iterator().next().getType();
                blockDrops = Arrays.asList(new ItemStack(type), new ItemStack(type));
            }

            ItemStack[] drops = new ItemStack[blockDrops.size()];
            int crrnt = 0;

            for (ItemStack itemStack : blockDrops) {

                ItemStack itemStack1 = itemStack.clone();

                int dropCount = getDropCount(tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), r);

                itemStack1.setAmount(dropCount);

                drops[crrnt++] = itemStack1;

            }

            return drops;
        } else {
            return b.getDrops().toArray(new ItemStack[b.getDrops().size()]);
        }

    }

    private int getDropCount(int i, Random random) {
        int j = random.nextInt(i + 2) - 1;

        if (j < 0) {
            j = 0;
        }

        return (j + 1);
    }
}
