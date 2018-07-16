package com.nuno1212s.monsters.abilities;

import lombok.Getter;
import org.bukkit.Location;

public abstract class Ability {

    @Getter
    int id;

    public abstract void doAbility(Location l);


}
