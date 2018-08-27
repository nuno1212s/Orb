package com.nuno1212s.monsters.monsters;

import com.nuno1212s.monsters.monsters.nmshandlers.CustomEntityZombie;

public enum CustomMobs {

    ZOMBIE("Zombie", CustomEntityZombie.class);

    String entityName;

    Class<? extends CustomizableEntity> mobClass;

    CustomMobs(String entityName, Class<? extends CustomizableEntity> mobClass) {
        this.entityName = entityName;
        this.mobClass = mobClass;
    }

}
