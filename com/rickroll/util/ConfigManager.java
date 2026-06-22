// com/rickroll/util/ConfigManager.java
package com.rickroll.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rickroll.RickRollDLC;
import com.rickroll.module.Module;
import com.rickroll.setting.BooleanSetting;
import com.rickroll.setting.DoubleSetting;
import com.rickroll.setting.EnumSetting;
import com.rickroll.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File configDir;
    private final File configFile;

    public ConfigManager() {
        configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), "rickroll");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        configFile = new File(configDir, "config.json");
    }

    public void save() {
        JsonObject root = new JsonObject();

        for (Module module : RickRollDLC.modules) {
            JsonObject moduleObj = new JsonObject();
            moduleObj.addProperty("enabled", module.isEnabled());

            for (Setting<?> setting : module.getSettings()) {
                if (setting instanceof BooleanSetting) {
                    moduleObj.addProperty(setting.getName(), ((BooleanSetting) setting).getValue());
                } else if (setting instanceof DoubleSetting) {
                    moduleObj.addProperty(setting.getName(), ((DoubleSetting) setting).getValue());
                } else if (setting instanceof EnumSetting) {
                    moduleObj.addProperty(setting.getName(), ((EnumSetting) setting).getValue());
                }
            }

            root.add(module.getName(), moduleObj);
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            RickRollDLC.LOGGER.error("Failed to save config", e);
        }
    }

    public void load() {
        if (!configFile.exists()) return;

        try (FileReader reader = new FileReader(configFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            for (Module module : RickRollDLC.modules) {
                if (!root.has(module.getName())) continue;
                JsonObject moduleObj = root.getAsJsonObject(module.getName());

                if (moduleObj.has("enabled") && moduleObj.get("enabled").getAsBoolean()) {
                    module.setEnabled(true);
                }

                for (Setting<?> setting : module.getSettings()) {
                    if (!moduleObj.has(setting.getName())) continue;

                    if (setting instanceof BooleanSetting) {
                        ((BooleanSetting) setting).setValue(moduleObj.get(setting.getName()).getAsBoolean());
                    } else if (setting instanceof DoubleSetting) {
                        ((DoubleSetting) setting).setValue(moduleObj.get(setting.getName()).getAsDouble());
                    } else if (setting instanceof EnumSetting) {
                        ((EnumSetting) setting).setValue(moduleObj.get(setting.getName()).getAsString());
                    }
                }
            }
        } catch (Exception e) {
            RickRollDLC.LOGGER.error("Failed to load config", e);
        }
    }
}
