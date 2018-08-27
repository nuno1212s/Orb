package com.nuno1212s.events.war;

import com.nuno1212s.clans.ClanMain;
import com.nuno1212s.clans.clanmanager.Clan;
import com.nuno1212s.events.war.inventories.SelectPlayersInventory;
import com.nuno1212s.main.BukkitMain;
import com.nuno1212s.main.MainData;
import com.nuno1212s.modulemanager.Module;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WarEventScheduler {

    private Map<String, List<UUID>> signedUpClans;

    public long startDate;

    @Getter
    private SelectPlayersInventory selectPlayersInventory;

    public WarEventScheduler(Module m) {

        this.signedUpClans = new HashMap<>();

        File jsonFile = new File(m.getDataFolder(), "selectPlayersInventory.json");

        if (!jsonFile.exists()) {
            m.saveResource(jsonFile, "selectPlayersInventory.json");
        }

        this.selectPlayersInventory = new SelectPlayersInventory(jsonFile);
    }

    public void reset(long newStartDate) {
        this.signedUpClans = new HashMap<>();

        this.startDate = newStartDate;
    }

    /**
     * Only allow for clans to register in the 30 minutes before the war event starts
     * @return
     */
    public boolean canRegisterClan() {
        return this.startDate - System.currentTimeMillis() <= TimeUnit.MINUTES.toMillis(30);
    }

    /**
     * Registers a given clan into the war event
     * @param c
     */
    public void registerClan(Clan c, Player register) {

        if (!canRegisterClan()) {
            return;
        }

        List<UUID> onlineMembers = c.getOnlineMembers();
        if (onlineMembers.size() < 5) {
            MainData.getIns().getMessageManager().getMessage("NOT_ENOUGH_MEMBERS_ONLINE").sendTo(register);

        } else if (onlineMembers.size() > 10) {

            MainData.getIns().getMessageManager().getMessage("TOO_MANY_MEMBERS_ONLINE").sendTo(register);

            //Open inventory
            register.openInventory(this.selectPlayersInventory.buildInventory(register));
        } else {

            MainData.getIns().getMessageManager().getMessage("CLAN_REGISTERED").sendTo(register);

            registerClan(c.getClanID(), onlineMembers);

        }

    }

    public void registerClan(String clanID, List<UUID> membersAssigned) {

        if (!canRegisterClan()) {
            return;
        }

        this.signedUpClans.put(clanID, membersAssigned);

        for (UUID onlineMember : membersAssigned) {
            Player player = Bukkit.getPlayer(onlineMember);

            // TODO: 26/08/2018 teleport to some location idk
//                player.teleport()
        }
    }

    public void registerPlayer(String clanID, UUID player) {

        if (!canRegisterClan()) {
            return;
        }

        if (!this.signedUpClans.containsKey(clanID)) {
            return;
        }

        List<UUID> memberSignedUp = this.signedUpClans.get(clanID);

        memberSignedUp.add(player);

        // TODO: 27-08-2018 Send message saying the player joined

        this.signedUpClans.put(clanID, memberSignedUp);

        Player p = Bukkit.getPlayer(player);

        // TODO: 27-08-2018 Teleport to the location

    }

    /**
     * Get all the players registered in this war event
     * @return
     */
    public List<UUID> getPlayersRegistered() {

        List<UUID> players = new ArrayList<>();

        this.signedUpClans.values().forEach(players::addAll);

        return players;
    }

    public List<UUID> getPlayersRegistered(String clanID) {
        return this.signedUpClans.getOrDefault(clanID, new ArrayList<>());
    }

    /**
     * Unregister a player from the war event
     * @param playerID
     */
    public void removePlayer(UUID playerID) {

        for (Map.Entry<String, List<UUID>> players : this.signedUpClans.entrySet()) {

            if (players.getValue().contains(playerID)) {
                Clan c = ClanMain.getIns().getClanManager().getClan(players.getKey());

                if (c == null) {
                    break;
                }

                // TODO: 27-08-2018 Send message to inform that the player left

                players.getValue().remove(playerID);

                if (players.getValue().size() < 5) {
                    disqualify(c);

                    break;
                }

                List<UUID> onlineMembers = c.getOnlineMembers();


                //There are still some more online members
                if (onlineMembers.size() > players.getValue().size()) {

                    for (UUID onlineMember : onlineMembers) {
                        if (c.getRank(onlineMember).ordinal() >= Clan.Rank.ADMIN.ordinal()) {

                            Player player = Bukkit.getPlayer(onlineMember);

                            this.selectPlayersInventory.getSelected().put(player.getUniqueId(), players.getValue());

                            player.openInventory(this.selectPlayersInventory.buildInventory(player, c));

                            break;
                        }
                    }

                }
            }

        }

    }

    /**
     * Disqualifies a clan from the war event
     * @param c
     */
    private void disqualify(Clan c) {

        this.signedUpClans.get(c.getClanID()).forEach((player) -> {

            Player p = Bukkit.getServer().getPlayer(player);

            p.teleport(p.getWorld().getSpawnLocation());

        });

        this.signedUpClans.remove(c.getClanID());

    }

    /**
     * Get the clans that are registered for the next war event
     *
     * @return
     */
    public Set<String> getRegisteredClans() {
        return this.signedUpClans.keySet();
    }

    /**
     * Is this clan registered in the war event
     * @param clanID
     * @return
     */
    public boolean isClanRegistered(String clanID) {
        return this.signedUpClans.containsKey(clanID);
    }

    public void start() {

        new WarEvent(this.signedUpClans);

        this.reset(this.startDate + TimeUnit.DAYS.toMillis(7));
    }

}
