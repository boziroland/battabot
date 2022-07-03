package Bot.ActualCommands.TextCommands;

import Bot.CommandManagement.ICommand;
import Bot.Service.Translator;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TranslateCommand implements ICommand {
    @Override
    public String command() {
        return "translate";
    }

    @Override
    public String help() {
        return "Usage: `!translate <source language> <destination language> <text>`\n: https://cloud.google.com/translate/docs/languages";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {

        String content = event.getMessage().getContentRaw();

        if (args.length >= 3) {

            String translateFrom = args[1];
            String translateTo = args[2];
            String textToTranslate = content.substring(content.indexOf(" ", content.indexOf(" ", content.indexOf(" ") + 1) + 1) + 1);

            String translatedText = Translator.getInstance().translate(translateFrom, translateTo, textToTranslate);
            event.getChannel().sendMessage(translatedText).queue();
        }
    }
}
