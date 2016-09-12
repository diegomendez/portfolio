package com.wideo.metrics.events;

public enum EventPropertyTypesEnum {
    wideo_id("wideo_id"), cloned_wideo_id("cloned_wideo_id"), reused_wideo_id(
            "reused_wideo_id"), browser("browser"), location("location"), user_id(
            "user_id"), interaction_href("href_target"), interaction_type(
            "interaction_type"), interaction_name("interaction_name"), interaction_phone(
            "interaction_phone"), interaction_mail("interaction_mail"), interaction_lastname(
            "interaction_lastname"), interaction_id("interaction_id"), embed_site_url(
            "embed_site_url"), namespace("namespace"), action("action"), date(
            "date"), os("os"), playing_sec("playing_sec"), properties(
            "properties"), device_category("device_category"), count("count");

    private String name;

    EventPropertyTypesEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name();
    }
}
