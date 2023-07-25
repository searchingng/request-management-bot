package uz.everbest.requestmanagement.domain.enums;

import java.util.Locale;

public enum LanguageCode {

    EN("en"),
    RU("ru");

    private final String code;
    private final Locale locale;

    LanguageCode(String code) {
        this.code = code;
        this.locale = new Locale(code);
    }

    public String code() {
        return code;
    }

    public Locale locale() {
        return locale;
    }

    public static LanguageCode getByCode(String code) {
        if (RU.code().equalsIgnoreCase(code))
            return RU;
        else
            return EN;
    }

}
