package com.github.izhangzhihao.rainbow.brackets.settings;

import javax.swing.*;

/**
 * @author izhangzhihao
 */
public class RainbowSettingsForm {
    private JPanel panel;
    private JPanel appearancePanel;
    private JCheckBox enableRainbow;
    private JCheckBox rainbowHTML;

    private final RainbowSettings settings;

    public RainbowSettingsForm() {
        rainbowHTML.setSelected(true);
        enableRainbow.setSelected(true);
        settings = RainbowSettings.Companion.getInstance();
    }

    public JComponent getComponent() {
        return panel;
    }

    public boolean isRainbowHTML() {
        return rainbowHTML.isSelected();
    }

    public boolean isRainbowEnabled() {
        return enableRainbow.isSelected();
    }

    public boolean isModified() {
        return rainbowHTML.isSelected() != settings.isRainbowEnabled()
                || enableRainbow.isSelected() != settings.isRainbowHTMLEnabled();
    }

    public void reset() {
        enableRainbow.setSelected(settings.isRainbowEnabled());
        rainbowHTML.setSelected(settings.isRainbowHTMLEnabled());
    }
}
