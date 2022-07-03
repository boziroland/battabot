package Bot.ActualCommands.TextCommands;

import Bot.CommandManagement.ICommand;
import Bot.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DictionaryCommand implements ICommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryCommand.class);

    private final String url = "https://api.dictionaryapi.dev/api/v2/entries/";
    private final String language = "en_US";

    private static class DictionaryResponse {
        private static class Phonetics {
            public String text;
            public String audio;
        }

        private static class Meaning {
            private static class Definition {
                public String definition;
                public List<String> synonyms;
                public String example;
            }

            public String partOfSpeech;
            public List<Definition> definitions;
        }

        public String word;
        public List<Phonetics> phonetics;
        public List<Meaning> meanings;
    }

    public List<DictionaryResponse> response;

    @Override
    public String command() {
        return "dictionary";
    }

    @Override
    public String help() {
        return "Usage: `!dictionary/!dict <en_US/en_GB/ja/es/de> <the word>`\n" +
                "If no language is given, (american) english is assumed\n" +
                "Source: https://dictionaryapi.dev/";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        try {
            if (args.length >= 2) {
                String json = "";
                if (!isSupportedDictionaryLanguage(args[1])) {//we are assuming it's english
                    json = Utils.getJsonFromAPI(url + language + "/" + args[1]);
                } else {
                    if (args.length >= 3)
                        json = Utils.getJsonFromAPI(url + args[1] + "/" + args[2]);
                }
                if (!json.isEmpty()) {
                    Gson g = new Gson();
                    Type dictionaryResponseType = new TypeToken<ArrayList<DictionaryResponse>>() {
                    }.getType();
                    List<DictionaryResponse> response = g.fromJson(json, dictionaryResponseType);
                    event.getChannel().sendMessageEmbeds(createEmbed(response.get(0))).queue();
                }
            }
        } catch (RuntimeException e) {
            event.getChannel().sendMessage("Word not found!").queue();
            LOGGER.info(e.getLocalizedMessage());
        }
    }

    @Override
    public List<String> getAliases() {
        return List.of("dict");
    }

    private boolean isSupportedDictionaryLanguage(String lang) {
        return Set.of("en_US", "hi", "es", "fr", "ja", "ru", "en_GB", "de", "it", "ko", "pt-BR", "ar", "tr").contains(lang);
    }

    private MessageEmbed createEmbed(DictionaryResponse response) {
        EmbedBuilder ret = new EmbedBuilder();
        ret.setTitle("Word: " + response.word);
        for (var m : response.meanings) {
            for (var d : m.definitions) {
                ret.addField("Definition (" + m.partOfSpeech + ")", d.definition, true);
                ret.addField("Example", d.example, true);
                StringBuilder synonyms = new StringBuilder("");
                if (d.synonyms != null)
                    d.synonyms.forEach((syn) -> synonyms.append(syn).append(", "));
                ret.addField("Synonyms", synonyms.toString().isEmpty() ? "-" : synonyms.toString().substring(0, synonyms.length() - 2), true);
            }
        }
        ret.setColor(Color.YELLOW);
        return ret.build();
    }
}
