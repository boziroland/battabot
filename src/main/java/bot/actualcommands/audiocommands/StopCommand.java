package bot.actualcommands.audiocommands;

import bot.commandmanagement.ICommand;
import bot.lavaplayer.GuildAudioManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StopCommand implements ICommand {

    private final GuildAudioManager guildAudioManager;

    public StopCommand(GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
    }

    @Override
    public String command() {
        return "stop";
    }

    @Override
    public String help() {
        return "Stops the currently playing track";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        guildAudioManager.stop(event);
    }
}
