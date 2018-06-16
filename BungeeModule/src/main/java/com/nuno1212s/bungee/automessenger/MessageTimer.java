package com.nuno1212s.bungee.automessenger;

import com.nuno1212s.bungee.main.Main;
import com.nuno1212s.main.MainData;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class MessageTimer implements Runnable {

    private int ticksToMessage;

    private AutoMessageManager manager;

    public MessageTimer(AutoMessageManager manager) {
        this.manager = manager;
        ticksToMessage = manager.getTimeBetweenMessagesInTicks();
        MainData.getIns().getScheduler().runTaskTimerAsync(this, 1, 1);
    }

    @Override
    public void run() {

        if (--ticksToMessage <= 0) {
            List<String> message = manager.getRandomMessage();

            for (String s : message) {
                Main.getPlugin().getProxy().broadcast(TextComponent.fromLegacyText(s));
            }

            ticksToMessage = manager.getTimeBetweenMessagesInTicks();
        }

    }
}
