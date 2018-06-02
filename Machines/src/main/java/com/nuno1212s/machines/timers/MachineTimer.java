package com.nuno1212s.machines.timers;

import com.nuno1212s.machines.machinemanager.Machine;
import com.nuno1212s.machines.main.Main;

public class MachineTimer implements Runnable {

    @Override
    public void run() {

        synchronized (Main.getIns().getMachineManager().getMachines()) {
            Main.getIns().getMachineManager().getMachines().forEach(Machine::checkTick);
        }

    }
}
