package client.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class I18n {
    public enum Lang {
        PT_BR("Português", "pt-BR"),
        EN_US("English", "en-US"),
        ES_ES("Español", "es-ES"),
        FR_FR("Français", "fr-FR"),
        DE_DE("Deutsch", "de-DE");

        private final String label;
        private final Locale locale;

        Lang(String label, String tag) {
            this.label = label;
            this.locale = Locale.forLanguageTag(tag);
        }

        public Locale locale() {
            return locale;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static final String BUNDLE_BASE = "client.i18n.messages";
    private static Lang current = Lang.PT_BR;
    private static ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE, current.locale());

    private I18n() {}

    public static void setLanguage(Lang lang) {
        current = (lang == null) ? Lang.PT_BR : lang;
        bundle = ResourceBundle.getBundle(BUNDLE_BASE, current.locale());
    }

    public static Lang getLanguage() {
        return current;
    }

    public static String get(String key, Object... args) {
        String pattern;
        try {
            pattern = bundle.getString(key);
        } catch (MissingResourceException e) {
            pattern = key;
        }
        if (args != null && args.length > 0) {
            return String.format(pattern, args);
        }
        return pattern;
    }
}
