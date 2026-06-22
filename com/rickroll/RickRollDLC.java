// com/rickroll/RickRollDLC.java
package com.rickroll;

import com.rickroll.gui.ClickGuiScreen;
import com.rickroll.module.*;
import com.rickroll.util.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RickRollDLC implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("RickRollDLC");
    public static final String MOD_ID = "rickroll";
    public static MinecraftClient mc;

    public static final List<Module> modules = new ArrayList<>();
    public static KeyBinding clickGuiKey;
    private static ClickGuiScreen clickGuiScreen;
    private static ConfigManager configManager;

    public static KillAura killAura;
    public static TriggerBot triggerBot;
    public static AutoArmor autoArmor;
    public static AutoTotem autoTotem;
    public static Sprint sprint;
    public static Step step;
    public static Fly fly;
    public static ESP esp;
    public static Tracers tracers;
    public static Nametags nametags;
    public static Scaffold scaffold;
    public static ChestStealer chestStealer;

    @Override
    public void onInitializeClient() {
        mc = MinecraftClient.getInstance();

        killAura = new KillAura();
        triggerBot = new TriggerBot();
        autoArmor = new AutoArmor();
        autoTotem = new AutoTotem();
        sprint = new Sprint();
        step = new Step();
        fly = new Fly();
        esp = new ESP();
        tracers = new Tracers();
        nametags = new Nametags();
        scaffold = new Scaffold();
        chestStealer = new ChestStealer();

        modules.add(killAura);
        modules.add(triggerBot);
        modules.add(autoArmor);
        modules.add(autoTotem);
        modules.add(sprint);
        modules.add(step);
        modules.add(fly);
        modules.add(esp);
        modules.add(tracers);
        modules.add(nametags);
        modules.add(scaffold);
        modules.add(chestStealer);

        configManager = new ConfigManager();
        configManager.load();

        clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.rickroll.clickgui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.rickroll"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Module module : modules) {
                if (module.isEnabled()) {
                    module.onTick();
                }
            }

            while (clickGuiKey.wasPressed()) {
                if (clickGuiScreen == null) {
                    clickGuiScreen = new ClickGuiScreen(modules);
                }
                clickGuiScreen = new ClickGuiScreen(modules);
                client.setScreen(clickGuiScreen);
            }
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            for (Module module : modules) {
                if (module.isEnabled()) {
                    module.onWorldRender(context);
                }
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            for (Module module : modules) {
                if (module.isEnabled()) {
                    module.onHudRender(drawContext);
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            configManager.save();
        }));

        LOGGER.info("RickRollDLC initialized — never gonna give you up.");
    }

    public static Module getModuleByName(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }
}
