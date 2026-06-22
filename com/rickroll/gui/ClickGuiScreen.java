// com/rickroll/gui/ClickGuiScreen.java
package com.rickroll.gui;

import com.rickroll.module.Module;
import com.rickroll.setting.BooleanSetting;
import com.rickroll.setting.DoubleSetting;
import com.rickroll.setting.EnumSetting;
import com.rickroll.setting.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickGuiScreen extends Screen {
    private final List<Module> modules;
    private final List<CategoryPanel> panels = new ArrayList<>();
    private int dragOffsetX, dragOffsetY;
    private CategoryPanel draggingPanel = null;
    private SettingSlider draggingSlider = null;

    private int hudHue = 0;

    private static final int PANEL_WIDTH = 180;
    private static final int PANEL_HEADER_HEIGHT = 24;
    private static final int MODULE_HEIGHT = 22;
    private static final int SETTING_HEIGHT = 20;
    private static final int PANEL_SPACING = 190;
    private static final int PADDING = 4;

    public ClickGuiScreen(List<Module> modules) {
        super(Text.literal("RickRollDLC ClickGUI"));
        this.modules = modules;
    }

    @Override
    protected void init() {
        panels.clear();

        Map<Module.Category, List<Module>> byCategory = new HashMap<>();
        for (Module module : modules) {
            byCategory.computeIfAbsent(module.getCategory(), k -> new ArrayList<>()).add(module);
        }

        Module.Category[] categories = Module.Category.values();
        int startX = 40;
        int startY = 30;

        for (int i = 0; i < categories.length; i++) {
            Module.Category cat = categories[i];
            List<Module> catModules = byCategory.getOrDefault(cat, new ArrayList<>());
            CategoryPanel panel = new CategoryPanel(cat, startX + i * PANEL_SPACING, startY, catModules);
            panels.add(panel);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        hudHue = (hudHue + 1) % 360;

        for (CategoryPanel panel : panels) {
            panel.render(context, mouseX, mouseY, hudHue);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (CategoryPanel panel : panels) {
            if (panel.isHoveredHeader(mouseX, mouseY)) {
                if (button == 0) {
                    draggingPanel = panel;
                    dragOffsetX = (int) mouseX - panel.x;
                    dragOffsetY = (int) mouseY - panel.y;
                }
                return true;
            }

            if (panel.mouseClicked(mouseX, mouseY, button)) {
                if (panel.draggingSlider != null) {
                    draggingSlider = panel.draggingSlider;
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingPanel != null && button == 0) {
            draggingPanel.x = (int) mouseX - dragOffsetX;
            draggingPanel.y = Math.max(0, (int) mouseY - dragOffsetY);
            return true;
        }

        if (draggingSlider != null && button == 0) {
            draggingSlider.handleDrag(mouseX);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingPanel = null;
        draggingSlider = null;
        for (CategoryPanel panel : panels) {
            panel.draggingSlider = null;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private int getRainbowColor(int hueOffset, float saturation, float brightness) {
        return net.minecraft.util.math.MathHelper.hsvToRgb(
            ((hudHue + hueOffset) % 360) / 360f, saturation, brightness
        );
    }

    // --- Inner classes ---

    public class CategoryPanel {
        public Module.Category category;
        public int x, y;
        public List<Module> modules;
        public boolean[] expanded;
        public SettingSlider draggingSlider = null;

        public CategoryPanel(Module.Category category, int x, int y, List<Module> modules) {
            this.category = category;
            this.x = x;
            this.y = y;
            this.modules = modules;
            this.expanded = new boolean[modules.size()];
        }

        public int getHeight() {
            int h = PANEL_HEADER_HEIGHT;
            for (int i = 0; i < modules.size(); i++) {
                h += MODULE_HEIGHT;
                if (expanded[i]) {
                    Module m = modules.get(i);
                    h += m.getSettings().size() * SETTING_HEIGHT;
                }
            }
            return h;
        }

        public boolean isHoveredHeader(double mouseX, double mouseY) {
            return mouseX >= x && mouseX <= x + PANEL_WIDTH && mouseY >= y && mouseY <= y + PANEL_HEADER_HEIGHT;
        }

        public void render(DrawContext context, int mouseX, int mouseY, int hudHue) {
            int panelHeight = getHeight();

            context.fill(x - 1, y - 1, x + PANEL_WIDTH + 1, y + panelHeight + 1, 0x80000000);
            context.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEADER_HEIGHT, category.getColor() | 0xCC000000);

            int titleColor = getRainbowColor((int)(x * 0.5), 0.8f, 1.0f);
            context.drawTextWithShadow(textRenderer, category.getDisplayName(),
                x + 8, y + (PANEL_HEADER_HEIGHT - textRenderer.fontHeight) / 2, titleColor);

            int r = (category.getColor() >> 16) & 0xFF;
            int g = (category.getColor() >> 8) & 0xFF;
            int b = category.getColor() & 0xFF;
            context.fill(x, y + PANEL_HEADER_HEIGHT, x + PANEL_WIDTH, y + PANEL_HEADER_HEIGHT + 1, category.getColor());

            int currentY = y + PANEL_HEADER_HEIGHT;

            for (int i = 0; i < modules.size(); i++) {
                Module module = modules.get(i);
                boolean isHovered = mouseX >= x && mouseX <= x + PANEL_WIDTH &&
                                    mouseY >= currentY && mouseY <= currentY + MODULE_HEIGHT;

                int moduleBg = module.isEnabled() ? (0x40FF69B4 | 0x80000000) : (isHovered ? 0x30FFFFFF : 0x20000000);
                context.fill(x, currentY, x + PANEL_WIDTH, currentY + MODULE_HEIGHT, moduleBg);

                int modTextColor = module.isEnabled() ? 0xFFFF69B4 : 0xFFCCCCCC;
                context.drawTextWithShadow(textRenderer, module.getName(),
                    x + 10, currentY + (MODULE_HEIGHT - textRenderer.fontHeight) / 2, modTextColor);

                if (module.getSettings().size() > 0) {
                    String arrow = expanded[i] ? "v" : ">";
                    context.drawTextWithShadow(textRenderer, arrow,
                        x + PANEL_WIDTH - 14, currentY + (MODULE_HEIGHT - textRenderer.fontHeight) / 2, 0xFF888888);
                }

                currentY += MODULE_HEIGHT;

                if (expanded[i]) {
                    for (Setting<?> setting : module.getSettings()) {
                        int settingBg = 0x15000000;
                        context.fill(x, currentY, x + PANEL_WIDTH, currentY + SETTING_HEIGHT, settingBg);

                        if (setting instanceof BooleanSetting) {
                            renderBooleanSetting(context, (BooleanSetting) setting, currentY, mouseX, mouseY);
                        } else if (setting instanceof DoubleSetting) {
                            renderDoubleSetting(context, (DoubleSetting) setting, currentY, mouseX, mouseY);
                        } else if (setting instanceof EnumSetting) {
                            renderEnumSetting(context, (EnumSetting) setting, currentY, mouseX, mouseY);
                        }

                        currentY += SETTING_HEIGHT;
                    }
                }
            }
        }

        private void renderBooleanSetting(DrawContext context, BooleanSetting setting, int yPos, double mouseX, double mouseY) {
            boolean val = setting.getValue();
            String text = setting.getName() + ": " + (val ? "ON" : "OFF");

            int color = val ? 0xFF7CFC00 : 0xFF888888;
            context.drawTextWithShadow(textRenderer, text, x + 16, yPos + (SETTING_HEIGHT - textRenderer.fontHeight) / 2, color);

            int boxX = x + PANEL_WIDTH - 20;
            int boxY = yPos + (SETTING_HEIGHT - 8) / 2;
            context.fill(boxX, boxY, boxX + 12, boxY + 8, val ? 0xFF7CFC00 : 0xFF444444);
        }

        private void renderDoubleSetting(DrawContext context, DoubleSetting setting, int yPos, double mouseX, double mouseY) {
            String text = setting.getName() + ": " + String.format("%.1f", setting.getValue());
            context.drawTextWithShadow(textRenderer, text, x + 16, yPos + 6, 0xFFDDDDDD);

            int sliderX = x + 16;
            int sliderY = yPos + 14;
            int sliderWidth = PANEL_WIDTH - 32;
            int sliderHeight = 4;

            context.fill(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 0xFF333333);

            double pct = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
            int filledWidth = (int) (sliderWidth * pct);
            int fillColor = getRainbowColor((int)(yPos * 2), 0.9f, 1.0f);
            context.fill(sliderX, sliderY, sliderX + filledWidth, sliderY + sliderHeight, fillColor);

            int handleX = sliderX + filledWidth - 2;
            context.fill(handleX, sliderY - 2, handleX + 4, sliderY + sliderHeight + 2, 0xFFFFFFFF);
        }

        private void renderEnumSetting(DrawContext context, EnumSetting setting, int yPos, double mouseX, double mouseY) {
            String text = setting.getName() + ": " + setting.getValue();
            context.drawTextWithShadow(textRenderer, text, x + 16, yPos + (SETTING_HEIGHT - textRenderer.fontHeight) / 2, 0xFFFFAA00);

            String arrow = "[...]";
            int arrowWidth = textRenderer.getWidth(arrow);
            context.drawTextWithShadow(textRenderer, arrow,
                x + PANEL_WIDTH - arrowWidth - 8, yPos + (SETTING_HEIGHT - textRenderer.fontHeight) / 2, 0xFF888888);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0) return false;
            if (mouseX < x || mouseX > x + PANEL_WIDTH) return false;

            int currentY = y + PANEL_HEADER_HEIGHT;

            for (int i = 0; i < modules.size(); i++) {
                Module module = modules.get(i);

                if (mouseY >= currentY && mouseY <= currentY + MODULE_HEIGHT) {
                    int settingArrowX = x + PANEL_WIDTH - 20;
                    if (mouseX >= settingArrowX && module.getSettings().size() > 0) {
                        expanded[i] = !expanded[i];
                    } else {
                        module.toggle();
                    }
                    return true;
                }
                currentY += MODULE_HEIGHT;

                if (expanded[i]) {
                    for (Setting<?> setting : module.getSettings()) {
                        if (mouseY >= currentY && mouseY <= currentY + SETTING_HEIGHT) {
                            handleSettingClick(module, setting, currentY, mouseX, mouseY);
                            return true;
                        }
                        currentY += SETTING_HEIGHT;
                    }
                }
            }
            return false;
        }

        private void handleSettingClick(Module module, Setting<?> setting, int yPos, double mouseX, double mouseY) {
            if (setting instanceof BooleanSetting) {
                BooleanSetting boolSet = (BooleanSetting) setting;
                boolSet.setValue(!boolSet.getValue());
            } else if (setting instanceof DoubleSetting) {
                DoubleSetting doubleSet = (DoubleSetting) setting;
                int sliderX = x + 16;
                int sliderWidth = PANEL_WIDTH - 32;
                double pct = MathHelper.clamp((mouseX - sliderX) / sliderWidth, 0, 1);
                double newVal = doubleSet.getMin() + pct * (doubleSet.getMax() - doubleSet.getMin());
                doubleSet.setValue(newVal);

                draggingSlider = new SettingSlider(doubleSet, x + 16, PANEL_WIDTH - 32);
            } else if (setting instanceof EnumSetting) {
                EnumSetting enumSet = (EnumSetting) setting;
                List<String> options = enumSet.getOptions();
                int currentIdx = options.indexOf(enumSet.getValue());
                int nextIdx = (currentIdx + 1) % options.size();
                enumSet.setValue(options.get(nextIdx));
            }
        }
    }

    public class SettingSlider {
        public DoubleSetting setting;
        public int sliderX;
        public int sliderWidth;

        public SettingSlider(DoubleSetting setting, int sliderX, int sliderWidth) {
            this.setting = setting;
            this.sliderX = sliderX;
            this.sliderWidth = sliderWidth;
        }

        public void handleDrag(double mouseX) {
            double pct = MathHelper.clamp((mouseX - sliderX) / sliderWidth, 0, 1);
            double newVal = setting.getMin() + pct * (setting.getMax() - setting.getMin());
            setting.setValue(newVal);
        }
    }
}
