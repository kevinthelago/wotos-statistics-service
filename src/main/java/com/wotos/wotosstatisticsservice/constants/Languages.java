package com.wotos.wotosstatisticsservice.constants;

public enum Languages {
    ENGLISH("English", "en"),
    RUSSIAN("Русский", "ru"),
    POLISH("Polski", "pl"),
    GERMAN("Deutsch", "de"),
    FRENCH("Français", "fr"),
    SPANISH("Español", "es"),
    CHINESE_SIMPLIFIED_CHINA("简体中文", "zh-cn"),
    CHINESE_TRADITIONAL_TAIWAN("繁體中文", "zh-tw"),
    TURKISH("Türkçe", "tr"),
    CZECH("Čeština", "cs"),
    THAI("ไทย", "th"),
    VIETNAMESE("Tiếng Việt", "vi"),
    KOREAN("한국어", "ko");

    private final String name;
    private final String code;

    Languages(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
