package com.nuno1212s.events.war;

import com.nuno1212s.clans.clanmanager.Clan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WarEvent {

    private List<String> signedUpClans;

    public long startDate;

    public WarEvent() {

        this.signedUpClans = new ArrayList<>();

    }

    public boolean canRegisterClan() {
        return this.startDate - System.currentTimeMillis() <= TimeUnit.MINUTES.toMillis(30);
    }

    /**
     * Registers a given clan into the war event
     * @param c
     */
    public void registerClan(Clan c) {
        this.signedUpClans.add(c.getClanID());
    }

    /**
     * Get the clans that are registered for the next war event
     *
     * @return
     */
    public List<String> getRegisteredClans() {
        return this.signedUpClans;
    }

}
