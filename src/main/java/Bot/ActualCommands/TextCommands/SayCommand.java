package Bot.ActualCommands.TextCommands;

import Bot.CommandManagement.ICommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SayCommand implements ICommand {
    @Override
    public String command() {
        return "say";
    }

    @Override
    public String help() {
        return "Usage !say <your message>\nRemoves your message and sends it as Battabot.";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++)
            sb.append(args[i]).append(" ");

        event.getMessage().delete().queue();
        event.getChannel().sendMessage(sb.toString()).queue();
    }
}
