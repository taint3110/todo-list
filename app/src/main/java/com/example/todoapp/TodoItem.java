package com.example.todoapp;

public class TodoItem {
    private String text;
    private boolean isSelected;
    private String contactName;
    private String contactPhone;

    public TodoItem(String text) {
        this.text = text;
        this.isSelected = false;
        this.contactName = null;
        this.contactPhone = null;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public boolean hasContact() {
        return contactName != null && !contactName.isEmpty();
    }
}
