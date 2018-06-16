package com.nuno1212s.rankup.events;

import com.gmail.nossr50.listeners.BlockListener;
import com.gmail.nossr50.mcMMO;
import com.nuno1212s.minas.events.PlayerBreakBlockMineEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * FORCES MCMMO BLOCK BREAK EVENTS, BECAUSE THE MINE PLUGIN CANCELS THE EVENT AND MCMMO IGNORES CANCELED BLOCK BREAKS
 */
public class ForceMCMMOBreakEvent implements Listener {

    private BlockListener listener = new BlockListener(mcMMO.p);

    @EventHandler
    public void onBreak(PlayerBreakBlockMineEvent e) {
        listener.onBlockBreak(e.getE());
        listener.onBlockBreakHigher(e.getE());
    }

}
