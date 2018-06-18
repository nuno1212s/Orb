package com.nuno1212s.machines.inventories;

import com.nuno1212s.inventories.InventoryData;
import com.nuno1212s.inventories.InventoryItem;
import com.nuno1212s.machines.machinemanager.Machine;
import com.nuno1212s.machines.machinemanager.MachineConfiguration;
import com.nuno1212s.machines.main.Main;
import com.nuno1212s.main.MainData;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.ItemUtils;
import lombok.Getter;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MachineInventory extends InventoryData<MachineItem> {

    public MachineInventory(File jsonFile) {
        super(jsonFile, MachineItem.class, true);
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        MachineItem item = getItem(e.getSlot());

        if (item == null) {
            return;
        }

        e.setResult(Event.Result.DENY);

        if (item.getItemFlags().contains("BUY")) {

            MachineConfiguration configuration = MachineConfiguration.fromItem(e.getCurrentItem());

            if (configuration == null) {
                return;
            }

            e.getWhoClicked().closeInventory();
            e.getWhoClicked().openInventory(Main.getIns().getInventoryManager().getConfirmInventory().buildInventory((Player) e.getWhoClicked(), configuration,
                    (ev) -> {
                        ev.getWhoClicked().closeInventory();

                        PlayerData player = MainData.getIns().getPlayerManager().getPlayer(ev.getWhoClicked().getUniqueId());

                        if (configuration.isCash()) {

                            if (player.removeCash(configuration.getPrice())) {

                                ItemStack itemStack = configuration.getItem();

                                e.getWhoClicked().getInventory().addItem(itemStack);

                                MainData.getIns().getMessageManager().getMessage("MACHINE_PURCHASED")
                                        .format("%machineName%", "")
                                        .sendTo(e.getWhoClicked());

                            } else {

                                MainData.getIns().getMessageManager().getMessage("NO_COINS")
                                        .format("%coins%", configuration.getPrice())
                                        .sendTo(e.getWhoClicked());

                            }

                        } else {

                            MainData.getIns().getServerCurrencyHandler()
                                    .removeCurrency(player, configuration.getPrice())
                                    .thenAccept((completed) -> {
                                        if (completed) {

                                            ItemStack itemStack = configuration.getItem();

                                            e.getWhoClicked().getInventory().addItem(itemStack);

                                            MainData.getIns().getMessageManager().getMessage("MACHINE_PURCHASED")
                                                    .format("%machineName%", "")
                                                    .sendTo(e.getWhoClicked());

                                        } else {

                                            MainData.getIns().getMessageManager().getMessage("NO_COINS")
                                                    .format("%coins%", configuration.getPrice())
                                                    .sendTo(e.getWhoClicked());

                                        }
                                    });

                        }
                        ev.getWhoClicked().getInventory().addItem(configuration.getItem());

                        MainData.getIns().getMessageManager().getMessage("BOUGHT_MACHINE").sendTo(ev.getWhoClicked());
                    }, (ev) -> {

                        ev.getWhoClicked().closeInventory();

                    }));

        } else if (item.getItemFlags().contains("UPGRADE")) {

            Machine m = Machine.getMachineFromItem(e.getCurrentItem());

            if (m == null) {
                return;
            }

            e.getWhoClicked().closeInventory();
            e.getWhoClicked().openInventory(Main.getIns().getInventoryManager().getConfirmInventory().buildInventory((Player) e.getWhoClicked(), m,
                    (ev) -> {
                        Machine m1 = Machine.getMachineFromItem(ev.getCurrentItem());

                        if (m1 == null) {
                            return;
                        }

                        long price = m1.getConfiguration().getPrice();
                        boolean cash = m1.getConfiguration().isCash();

                        if (cash) {

                            PlayerData player = MainData.getIns().getPlayerManager().getPlayer(ev.getWhoClicked().getUniqueId());

                            if (player.removeCash(price)) {
                                m1.incrementAmount();

                                MainData.getIns().getMessageManager().getMessage("UPGRADED_MACHINE_CASH")
                                        .format("%cost%", price)
                                        .sendTo(player);
                            } else {
                                MainData.getIns().getMessageManager().getMessage("NO_CASH")
                                        .format("%amount%", price)
                                        .sendTo(player);
                            }
                        } else {

                            PlayerData player = MainData.getIns().getPlayerManager().getPlayer(ev.getWhoClicked().getUniqueId());

                            MainData.getIns().getServerCurrencyHandler().removeCurrency(player, price)
                                    .thenAccept((completed) -> {
                                        if (completed) {

                                            m1.incrementAmount();

                                            MainData.getIns().getMessageManager().getMessage("UPGRADED_MACHINE_COINS")
                                                    .format("%cost%", price)
                                                    .sendTo(player);

                                        } else {

                                            MainData.getIns().getMessageManager().getMessage("NO_COINS")
                                                    .format("%amount%", price)
                                                    .sendTo(player);
                                        }
                                    });
                        }
                    },
                    (ev) -> {
                        ev.getWhoClicked().closeInventory();

                        Machine m1 = Machine.getMachineFromItem(ev.getCurrentItem());

                        if (m1 == null) {
                            return;
                        }

                        ev.getWhoClicked().openInventory(Main.getIns().getInventoryManager().getInventoryForMachine(m1));
                    }));
        }
    }

    public Inventory buildInventory(Machine m) {
        Inventory i = Bukkit.getServer().createInventory(null, this.getInventorySize(), this.getInventoryName());

        Map<String, String> formats = new HashMap<>();

        formats.put("%amount%", String.valueOf(m.getAmount()));
        formats.put("%baseAmount%", String.valueOf(m.getConfiguration().getBaseAmount()));
        formats.put("%currentAmount%", String.valueOf(m.getAmount() * m.getConfiguration().getBaseAmount()));
        formats.put("%timeDiference%", DurationFormatUtils.formatDuration(m.getConfiguration().getSpacing(), "mm:ss"));

        for (MachineItem machineItem : this.getItems()) {
            i.setItem(machineItem.getSlot(), ItemUtils.formatItem(machineItem.getItem(m), formats));
        }

        return i;
    }
}

class MachineItem extends InventoryItem {

    @Getter
    int configuration;

    public MachineItem(JSONObject data) {
        super(data);

        configuration = (Integer) data.getOrDefault("MachineConfiguration", 0);
    }

    public ItemStack getItem(Machine m) {

        if (this.getItemFlags().isEmpty()) {
            return getItem();
        }

        return m.writeToItem(getItem());
    }

    public ItemStack getItem() {

        MachineConfiguration m = Main.getIns().getMachineManager().getConfiguration(getConfiguration());

        return m.getItem();

    }

}
