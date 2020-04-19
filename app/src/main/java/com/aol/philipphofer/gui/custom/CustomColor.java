package com.aol.philipphofer.gui.custom;

public enum CustomColor {

    YELLOW("#ffcc00"), GREEN("#70ad47"), BLUE("#5b9bd5"), ORANGE("#ed7d31");

    String hex;

    CustomColor(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }
}
