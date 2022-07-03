package Bot.Service;

import Bot.Utils.Constants;
import Bot.Utils.Utils;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

//Singleton class with lazy loading
public final class Translator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Translator.class);

    private static Translator instance = null;

    private Translate translate = null;

    private static final Set<String> validLanguages = new HashSet<>(Set.of(
            "af", "sq", "am", "ar", "hy", "az", "eu", "be", "bn", "bs", "bg", "ca", "ceb", "zh-CN", "zh",
            "zh-TW", "co", "hr", "cs", "da", "nl", "en", "eo", "et", "fi", "fr", "fy", "gl", "ka", "de",
            "el", "gu", "ht", "ha", "haw", "he", "iw", "hi", "hmn", "hu", "is", "ig", "id", "ga", "it", "ja",
            "jw", "kn", "kk", "km", "ko", "ku", "ky", "lo", "la", "lv", "lt", "lb", "mk", "mg", "ms", "ml",
            "mt", "mi", "mr", "mn", "my", "ne", "no", "ny", "ps", "fa", "pl", "pt", "pa", "ro", "ru", "sm",
            "gd", "sr", "st", "sn", "sd", "si", "sk", "sl", "so", "es", "su", "sw", "sv", "tl", "tg", "ta",
            "te", "th", "tr", "uk", "ur", "uz", "vi", "cy", "xh", "yi", "yo", "zu"
    ));

    public static Translator getInstance() {
        if (instance == null) {
            instance = new Translator();
        }

        return instance;
    }

    private Translator() {

        String translateLoc = readTranslationAPIKey();

        try {
            translate = TranslateOptions
                    .newBuilder()
                    .setCredentials(
                            ServiceAccountCredentials
                                    .fromStream(new FileInputStream(translateLoc)))
                    .build().getService();
        } catch (IOException e) {
            LOGGER.error("Most likely no suitable API key could be found!");
            e.printStackTrace();
        }
    }

    private static String readTranslationAPIKey() {
        return Utils.getPropertiesFromResourceFile("config/ConfigurationKeys.properties").getProperty("TranslatorKeyFile");
    }

    public String translate(String from, String to, String message) {

        if (from.equalsIgnoreCase(to))
            return message;

        if (translate == null) {
            return "No translation API key was provided.";
        }

        String result;

        if (isValidLanguage(from)) {
            if (isValidLanguage(to)) {
                Translation translation = translate.translate(
                        message,
                        Translate.TranslateOption.sourceLanguage(from.toLowerCase()),
                        Translate.TranslateOption.targetLanguage(to.toLowerCase()),
                        Translate.TranslateOption.format("text"));

                result = translation.getTranslatedText();
            } else {
                result = "The 2nd language is invalid " + Constants.PENSIVE + "\nList of supported languages: https://cloud.google.com/translate/docs/languages";
            }
        } else {
            result = "The 1st language is invalid " + Constants.PENSIVE + "\nList of supported languages: https://cloud.google.com/translate/docs/languages";
        }

        return result;
    }

    public static boolean isValidLanguage(String language) {
        return validLanguages.contains(language.toLowerCase());
    }
}
