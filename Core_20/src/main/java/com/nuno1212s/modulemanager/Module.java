package com.nuno1212s.modulemanager;

import com.nuno1212s.main.MainData;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Module class
 */
@Getter
public abstract class Module {

    @Setter
    String moduleName;

    @Setter
    String version;

    @Setter
    String[] dependencies;

    @Setter
    boolean enabled;

    @Setter
    @Getter
    private ModuleLoader initLoader;

    public File getDataFolder() {
        File moduleFolder = MainData.getIns().getDataFolder();
        File dataFolder = new File(moduleFolder + File.separator + this.moduleName + File.separator);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        return dataFolder;
    }

    public List<Module> getDependencies() {
        ArrayList<Module> modules = new ArrayList<>();
        ModuleManager ins = MainData.getIns().getModuleManager();
        for (String dependency : this.dependencies) {
            Module module = ins.getModule(dependency);
            if (module == null) {
                continue;
            }
            modules.add(module);
        }
        return modules;
    }

    protected void registerCommand(String[] names, Object commandExecutor) {
        MainData.getIns().getCommandRegister().registerCommand(names, commandExecutor);
    }

    public void disable() {
        this.initLoader.shutdown();
    }

    public abstract void onEnable();

    public abstract void onDisable();

}
