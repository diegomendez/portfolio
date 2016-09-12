package com.wideo.metrics.events;

public enum WideoEventTypesEnum {
    WIDEO_VIEW("WIDEO_VIEW"), WIDEO_PLAY("WIDEO_PLAY"), WIDEO_LIKE("WIDEO_LIKE"), FACEBOOK_LIKE(
            "FACEBOOK_LIKE"), WIDEO_REUSE("WIDEO_REUSE"), WIDEO_SHARE(
            "WIDEO_SHARE"), WIDEO_PLAYING("WIDEO_PLAYING"), WIDEO_INTERACTION_FORM("FORM_COMPLETED"), INTERACTION_SHOWN(
            "INTERACTION_SHOWN"), EMBED_VIEW("EMBED_VIEW"), WIDEO_CLONE(
            "WIDEO_CLONE"), WIDEO_DOWNLOAD("DOWNLOAD"), YOUTUBE_SHARE(
            "YOUTUBE_SHARE"), FACEBOOK_SHARE("FACEBOOK_SHARE"), YOUTUBE_VIEWS(
            "YOUTUBE_VIEWS"), SECONDS_WATCHED("SECONDS_WATCHED"), OBJECT_CLICK("OBJECT_CLICK"), FORM_SENT("FORM_SENT");

    private String name;

    WideoEventTypesEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
