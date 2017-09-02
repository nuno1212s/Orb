package com.nuno1212s.npcinbox.chat;

import com.nuno1212s.rewards.Reward;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles building messages
 */
public class MessageBuilder {

    private Reward unfinishedReward;

    @Getter
    private List<String> messages;

    public MessageBuilder(Reward unfinishedReward) {
        this.messages = new ArrayList<>();
        this.unfinishedReward = unfinishedReward;
    }

    public MessageBuilder append(String unFormattedMessage) {
        this.messages.add(ChatColor.translateAlternateColorCodes('&', unFormattedMessage));
        return this;
    }

    public MessageBuilder deleteLastMessage() {
        if (!this.messages.isEmpty())
            this.messages.remove(this.messages.size() - 1);
        return this;
    }

    public MessageBuilder clearMessages() {
        this.messages.clear();
        return this;
    }

    /**
     * Returns the reward with the finished messages as the reward
     * @return
     */
    public Reward buildReward() {
        this.unfinishedReward.setReward(this.messages);

        return unfinishedReward;
    }
}

