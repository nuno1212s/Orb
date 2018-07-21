package com.nuno1212s.mercado.util;

import com.nuno1212s.modulemanager.Module;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TranslatableComponents {

    private File translationFile;

    private Map<String, String> translations;

    public TranslatableComponents(Module m) {
        translationFile = new File(m.getDataFolder(), "translations.json");

        if (!translationFile.exists()) {
            m.saveResource(translationFile, "translations.json");
        }

        translations = new HashMap<>();

        JSONObject obj;

        try (FileReader r = new FileReader(this.translationFile)) {

            obj = (JSONObject) new JSONParser().parse(r);

            obj.forEach((key, value) -> {
                translations.put((String) key, (String) value);
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public String getTranslation(Material m) {
        return translations.getOrDefault(m.name(), StringUtils.capitalize(m.name().replace("_", " ")));
    }

}
