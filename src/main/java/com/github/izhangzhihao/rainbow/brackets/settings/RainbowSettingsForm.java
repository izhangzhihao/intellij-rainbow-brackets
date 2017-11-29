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
    private JCheckBox enableRainbowBracketsForAnyLanguages;

    private final RainbowSettings settings;

    public RainbowSettingsForm() {
        rainbowHTML.setSelected(true);
        enableRainbow.setSelected(true);
        enableRainbowBracketsForAnyLanguages.setSelected(false);
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

    public boolean getEnableRainbowBracketsForAnyLanguages() {
        return enableRainbowBracketsForAnyLanguages.isSelected();
    }

    public boolean isModified() {
        return rainbowHTML.isSelected() != settings.isRainbowEnabled()
                || enableRainbow.isSelected() != settings.isRainbowHTMLEnabled()
                || enableRainbowBracketsForAnyLanguages.isSelected() != settings.isEnableRainbowBracketsForAnyLanguages();
    }

    public void reset() {
        enableRainbow.setSelected(settings.isRainbowEnabled());
        rainbowHTML.setSelected(settings.isRainbowHTMLEnabled());
        enableRainbowBracketsForAnyLanguages.setSelected(settings.isEnableRainbowBracketsForAnyLanguages());
    }
}
