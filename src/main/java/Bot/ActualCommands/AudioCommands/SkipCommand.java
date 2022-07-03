package Bot.ActualCommands.AudioCommands;

import Bot.CommandManagement.ICommand;
import Bot.LavaPlayer.GuildAudioManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class SkipCommand implements ICommand {

    private final GuildAudioManager guildAudioManager;

    public SkipCommand(GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
    }

    @Override
    public String command() {
        return "skip";
    }

    @Override
    public String help() {
        return "Skips the currently playing track";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        guildAudioManager.skip(event.getGuild());
    }

    @Override
    public List<String> getAliases() {
        return List.of("s");
    }
}
