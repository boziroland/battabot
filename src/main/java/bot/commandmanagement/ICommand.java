package bot.commandmanagement;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public interface ICommand {
    String command();

    String help();

    void execute(String[] args, MessageReceivedEvent event);

    default List<String> getAliases() {
        return List.of();
    }

}