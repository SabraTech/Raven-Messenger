package com.example.space.chatapp.models;

/**
 * includes profile element consisting of label , value ,icon
 */

public class ProfileItem {

    private String label;
    private String value;
    private int icon;

    public ProfileItem(String label, String value, int icon) {
        this.label = label;
        this.value = value;
        this.icon = icon;
    }

    public String getLabel() {
        return this.label;
    }

    public String getValue() {
        return this.value;
    }

    public int getIcon() {
        return this.icon;
    }
}