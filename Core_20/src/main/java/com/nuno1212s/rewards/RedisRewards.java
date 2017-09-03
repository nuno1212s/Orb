package com.nuno1212s.rewards;

import com.nuno1212s.main.MainData;
import com.nuno1212s.rediscommunication.Message;
import com.nuno1212s.rediscommunication.RedisReceiver;
import org.json.simple.JSONObject;

/**
 * Handles redis reward communication
 */
public class RedisRewards implements RedisReceiver {

    @Override
    public String channel() {
        return "REWARDS";
    }

    @Override
    public void onReceived(Message message) {
        if (message.getChannel().equalsIgnoreCase(channel())) {
            if (message.getReason().equalsIgnoreCase("NEW_REWARD")) {
                JSONObject data = message.getData();
                int rewardID = ((Long) data.get("RewardID")).intValue();
                boolean isDefault = (Boolean) data.get("IsDefault");
                Reward.RewardType type = Reward.RewardType.valueOf((String) data.get("RewardType"));
                String serverType = (String) data.get("ServerType");
                String reward = (String) data.get("Reward");


                Reward r = new Reward(rewardID, type, isDefault, serverType, reward);
                MainData.getIns().getRewardManager().redis_addReward(r);
            } else if (message.getReason().equalsIgnoreCase("REWARD_TO_CLAIM")) {
                JSONObject data = message.getData();

                int rewardID = ((Long) data.get("RewardID")).intValue();

                MainData.getIns().getRewardManager().redis_addRewardToClaim(rewardID);
            }
        }
    }

    /**
     * Announce a new reward to be added
     *
     * @param r The reward
     */
    public void publishNewReward(Reward r) {
        JSONObject reward = new JSONObject();
        reward.put("RewardID", r.getId());
        reward.put("IsDefault", r.isDefault());
        reward.put("RewardType", r.getType().name());
        reward.put("ServerType", r.getServerType());
        reward.put("Reward", r.rewardToString());

        Message message = new Message(channel(), "NEW_REWARD", reward);

        MainData.getIns().getRedisHandler().sendMessage(message.toByteArray());
    }

    /**
     * Add reward to be claimed
     *
     * @param reward
     */
    public void addRewardToClaim(int reward) {
        JSONObject data = new JSONObject();
        data.put("RewardID", reward);

        Message message = new Message(channel(), "REWARD_TO_CLAIM", data);

        MainData.getIns().getRedisHandler().sendMessage(message.toByteArray());
    }

}
