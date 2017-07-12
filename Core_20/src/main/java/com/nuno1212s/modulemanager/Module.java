package com.nuno1212s.modulemanager;

import com.nuno1212s.main.MainData;
import lombok.Getter;
import lombok.Setter;

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

    public File getDataFolder() {
        File moduleFolder = MainData.getIns().getDataFolder();
        File dataFolder = new File(moduleFolder + File.separator + this.moduleName + File.separator);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        return dataFolder;
    }

    public List<Module> getDependencies(ModuleManager ins) {
        ArrayList<Module> modules = new ArrayList<>();
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

    protected void saveResource(File target, String path) {
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

    public File getFile(String fileName, boolean isResource) {
        File file = new File(getDataFolder(), fileName);

        if (!file.exists()) {
            if (isResource) {
                /*if (fileName.contains(File.separator)) {
                    String[] split = fileName.split(File.separator);
                    saveResource(file, split[split.length - 1]);
                } else {*/
                    saveResource(file, fileName);
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

    private InputStream getResourceAsStream(String path) {
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

    public void disable() {
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
