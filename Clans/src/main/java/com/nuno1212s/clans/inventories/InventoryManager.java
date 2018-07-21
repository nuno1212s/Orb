package com.nuno1212s.clans.inventories;

import com.nuno1212s.clans.clanplayer.ClanPlayer;
import com.nuno1212s.modulemanager.Module;
import com.nuno1212s.playermanager.PlayerData;
import com.nuno1212s.util.SerializableItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class InventoryManager {

    private File configFile;

    @Getter
    private ItemStack inviteItem, memberItem;

    @Getter
    private ChangeRankInventory changeRankInventory;

    @Getter
    private MemberInventory memberInventory;

    @Getter
    private ClanInventory landingInventoryClan, landingInventoryNoClan, inviteInventory;


    public InventoryManager(Module module) {

        this.configFile = new File(module.getDataFolder(), "config.json");

        if (!this.configFile.exists()) {
            module.saveResource(this.configFile, "config.json");
        }

        JSONObject obj;

        try (FileReader fr = new FileReader(this.configFile)) {

            obj = (JSONObject) new JSONParser().parse(fr);

            inviteItem = new SerializableItem((JSONObject) obj.get("InviteItem"));

            memberItem = new SerializableItem((JSONObject) obj.get("MemberItem"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        File changeRank = new File(module.getDataFolder(), "changeRankInventory.json");

        if (!changeRank.exists()) {
            module.saveResource(changeRank, "changeRankInventory.json");
        }

        this.changeRankInventory = new ChangeRankInventory(changeRank);

        File memberInventory = new File(module.getDataFolder(), "clanMemberInventory.json");

        if (!memberInventory.exists()) {
            module.saveResource(memberInventory, "clanMemberInventory.json");
        }

        this.memberInventory = new MemberInventory(memberInventory);

        File landingInventory = new File(module.getDataFolder(), "landingClanInventory.json"),
                landingInventory2 = new File(module.getDataFolder(), "landingNoClanInventory.json"),
                inviteInventory = new File(module.getDataFolder(), "inviteInventory.json");

        if (!landingInventory.exists()) {
            module.saveResource(landingInventory, "landingClanInventory.json");
        }

        if (!landingInventory2.exists()) {
            module.saveResource(landingInventory2, "landingNoClanInventory.json");
        }

        if (!inviteInventory.exists()) {
            module.saveResource(inviteInventory, "inviteInventory.json");
        }

        this.landingInventoryClan = new ClanInventory(landingInventory);
        this.landingInventoryNoClan = new ClanInventory(landingInventory2);
        this.inviteInventory = new ClanInventory(inviteInventory);

    }

    public ClanInventory getMainInventory(PlayerData playerData) {

        if (playerData instanceof ClanPlayer) {
            if (((ClanPlayer) playerData).hasClan()) {
                return landingInventoryClan;
            }
        }

        return landingInventoryNoClan;
    }


}
