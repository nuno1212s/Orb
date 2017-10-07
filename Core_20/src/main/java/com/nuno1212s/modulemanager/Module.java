package com.nuno1212s.modulemanager;

import com.nuno1212s.main.MainData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandExecutor;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
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

    public final File getDataFolder() {
        File moduleFolder = MainData.getIns().getDataFolder();
        File dataFolder = new File(moduleFolder + File.separator + this.moduleName + File.separator);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        return dataFolder;
    }

    /**
     * Get the dependencies of this module
     *
     * @param ins
     * @return
     */
    public final List<Module> getDependencies(ModuleManager ins) {

        ArrayList<Module> modules = new ArrayList<>();

        for (String dependency : this.dependencies) {
            Module module = ins.getModule(dependency);

            if (module == null) {
                throw new IllegalArgumentException("COULD NOT ENABLE MODULE " + getModuleName() + " LACKING DEPENDENCY " + dependency);
            }

            modules.add(module);
        }

        return modules;
    }

    /**
     * Register a command to the server
     *
     * @param names The aliases of the command
     * @param commandExecutor The executor of the command (CommandExecutor in bukkit, Command in bungee)
     */
    protected final void registerCommand(String[] names, Object commandExecutor) {
        MainData.getIns().getCommandRegister().registerCommand(names, commandExecutor);
    }

    /**
     * Save the resource to the modules data folder
     *
     * @param target
     * @param path
     */
    protected final void saveResource(File target, String path) {

        if (!target.exists()) {
            try {
                target.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (InputStream resourceAsStream = getResourceAsStream(path);
        OutputStream outputStream = new FileOutputStream(target)) {

            if (resourceAsStream == null) {
                System.out.println("Resource " + path + " cannot be found.");
                return;
            }

            byte[] bytes = new byte[1024];

            int length;

            while ((length = resourceAsStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, length);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the file from the modules data folder
     * If the file does not exist, it shall be created (If it is a resource, the file will be copied from the modules jar)
     *
     * @param filePath The path of the file
     * @param isResource Is the file a resource of the module
     * @return
     */
    public final File getFile(String filePath, boolean isResource) {
        File file = new File(getDataFolder(), filePath);

        if (!file.exists()) {
            if (isResource) {
                /*if (fileName.contains(File.separator)) {
                    String[] split = fileName.split(File.separator);
                    saveResource(file, split[split.length - 1]);
                } else {*/
                    saveResource(file, filePath);
                //}
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }

    /**
     * Get a resource from the jar file of the module
     *
     * @param path The path to the resource
     * @return
     */
    private final InputStream getResourceAsStream(String path) {
        URL resource = this.getInitLoader().getResource(path);
        if (resource == null) {
            return null;
        } else {
            try {
                URLConnection urlConnection = resource.openConnection();
                urlConnection.setUseCaches(false);
                return urlConnection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public final void disable() {
        MainData.getIns().getModuleManager().getLoader().removeLoader(this.getInitLoader());
        this.initLoader.shutdown();
    }

    public abstract void onEnable();

    public abstract void onDisable();

    /**
     * Method to handle disabling with players on the server (Without server restart)
     */
    public void activeDisable() {
        onDisable();
    }

    /**
     * Method to handle enabling with players on the server
     */
    public void activeEnable() {
        onEnable();
    }

    public void restart() {
        activeDisable();
        activeEnable();
    }

}
