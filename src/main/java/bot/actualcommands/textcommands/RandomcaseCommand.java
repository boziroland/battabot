package bot.actualcommands.textcommands;

import bot.commandmanagement.ICommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class RandomcaseCommand implements ICommand {

    @Override
    public String command() {
        return "randomcase";
    }

    @Override
    public String help() {
        return "Usage: !randomcase <your message>\nRandomizes the cases of the letters in <your message>";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        StringBuilder message = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            StringBuilder currentWord = new StringBuilder(args[i]);

            if (!(args[i].startsWith(":") || args[i].endsWith(":"))) { //if it's not an emote
                for (int j = 0; j < args[i].length(); j++) {
                    if (Math.random() < 0.5)
                        currentWord.setCharAt(j, Character.toUpperCase(args[i].charAt(j)));
                    else
                        currentWord.setCharAt(j, Character.toLowerCase(args[i].charAt(j)));
                }
            }
            message.append(currentWord.toString()).append(" ");
        }

        event.getChannel().sendMessage(message.toString()).queue();
    }

    @Override
    public List<String> getAliases() {
        return List.of("rc");
    }
}
