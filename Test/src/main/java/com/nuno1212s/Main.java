package com.nuno1212s;

import com.nuno1212s.core.modulemanager.Module;
import com.nuno1212s.core.modulemanager.ModuleData;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Main module class
 */
@ModuleData(name = "Test", version = "1.0", dependencies = {})
public class Main extends Module {

    void enable() {
        ArrayList<Modules> modules1 = new ArrayList<>();
        Modules eksde1 = new Modules("eksde1");
        Modules eksde2 = new Modules("eksde2");
        Modules eksde3 = new Modules("eksde3");
        Modules eksde4 = new Modules("eksde4");
        Modules eksde5 = new Modules("eskde5");
        eksde1.addDP(eksde2);
        eksde2.addDP(eksde4);
        eksde3.addDP(eksde5);
        modules1.add(eksde1);
        modules1.add(eksde2);
        modules1.add(eksde3);
        modules1.add(eksde4);
        modules1.add(eksde5);
        topoSort(modules1);
    }

    public void topoSort(List<Modules> modules) {

        while (!modules.isEmpty()) {
            dep_resolve(modules.get(0), new ArrayList<>());
            modules.removeIf(Modules::isEnabled);
        }
    }

    void dep_resolve(Modules a,  List<Modules> unresolved) {
        unresolved.add(a);
        for (Modules m : a.getDependencies()) {
            if (m.isEnabled()) {
                continue;
            }
            if (unresolved.contains(m)) {
                throw new IllegalArgumentException("Circular module dependency " + m.getName());
            }
            if (m.isEnabled()) {
                continue;
            }
            dep_resolve(m, unresolved);
        }
        a.setEnabled(true);
        unresolved.remove(a);
    }

    @Override
    public void onEnable() {
        System.out.println("Eazy eskde.ASDASDASD");
        enable();
    }

    @Override
    public void onDisable() {

    }
}

@Getter
@RequiredArgsConstructor
class Modules {

    @NonNull
    String name;

    List<Modules> dependencies = new ArrayList<>();

    @Setter
    boolean enabled;

    public void addDP(Modules m) {
        dependencies.add(m);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        System.out.println("Module " + name + " has been enabled");
    }

    @Override
    public String toString() {
        return name;
    }
}