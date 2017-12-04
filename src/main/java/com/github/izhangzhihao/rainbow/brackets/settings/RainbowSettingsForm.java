package com.github.izhangzhihao.rainbow.brackets.settings;

import javax.swing.*;

public class RainbowSettingsForm {
    private JPanel panel;
    private JPanel appearancePanel;
    private JCheckBox enableRainbow;
    private JCheckBox enableRainbowRoundBrackets;
    private JCheckBox enableRainbowSquigglyBrackets;
    private JCheckBox enableRainbowSquareBrackets;
    private JCheckBox enableRainbowAngleBrackets;

    private final RainbowSettings settings;

    public RainbowSettingsForm() {
        settings = RainbowSettings.Companion.getInstance();
        enableRainbow.setSelected(settings.isRainbowEnabled());
        enableRainbowRoundBrackets.setSelected(settings.isEnableRainbowRoundBrackets());
        enableRainbowSquigglyBrackets.setSelected(settings.isEnableRainbowSquigglyBrackets());
        enableRainbowSquareBrackets.setSelected(settings.isEnableRainbowSquareBrackets());
        enableRainbowAngleBrackets.setSelected(settings.isEnableRainbowAngleBrackets());
    }

    public JComponent getComponent() {
        return panel;
    }

    public boolean isRainbowEnabled() {
        return enableRainbow.isSelected();
    }

    public boolean isRainbowRoundBracketsEnabled() {
        return enableRainbowRoundBrackets.isSelected();
    }

    public boolean isRainbowSquigglyBracketsEnabled() {
        return enableRainbowSquigglyBrackets.isSelected();
    }

    public boolean isRainbowSquareBracketsEnabled() {
        return enableRainbowSquareBrackets.isSelected();
    }

    public boolean isRainbowAngleBracketsEnabled() {
        return enableRainbowAngleBrackets.isSelected();
    }

    public boolean isModified() {
        return isRainbowEnabled() != settings.isRainbowEnabled()
                || isRainbowAngleBracketsEnabled() != settings.isEnableRainbowAngleBrackets()
                || isRainbowRoundBracketsEnabled() != settings.isEnableRainbowRoundBrackets()
                || isRainbowSquigglyBracketsEnabled() != settings.isEnableRainbowSquigglyBrackets()
                || isRainbowSquareBracketsEnabled() != settings.isEnableRainbowSquareBrackets();
    }

    public void reset() {
        enableRainbow.setSelected(settings.isRainbowEnabled());
        enableRainbowRoundBrackets.setSelected(settings.isEnableRainbowRoundBrackets());
        enableRainbowAngleBrackets.setSelected(settings.isEnableRainbowAngleBrackets());
        enableRainbowSquigglyBrackets.setSelected(settings.isEnableRainbowSquigglyBrackets());
        enableRainbowSquareBrackets.setSelected(settings.isEnableRainbowSquareBrackets());
    }
}
