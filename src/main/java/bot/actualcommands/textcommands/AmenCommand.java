package bot.actualcommands.textcommands;

import bot.commandmanagement.ICommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AmenCommand implements ICommand {
    @Override
    public String command() {
        return "amen";
    }

    @Override
    public String help() {
        return "Amen";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        event.getChannel().sendMessage("Amen brother :relieved: :pray:").queue();
    }
}
