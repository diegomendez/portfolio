package com.wideo.metrics.events;

public enum EventNamespaceTypesEnum {

    player("player"), interactivity("interactivity"), editor("editor"), user(
            "user"), social("social"), wideos("wideos");

    private String name;

    EventNamespaceTypesEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name();
    }

}
