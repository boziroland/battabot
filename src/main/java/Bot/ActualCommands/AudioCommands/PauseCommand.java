package Bot.ActualCommands.AudioCommands;

import Bot.CommandManagement.ICommand;
import Bot.Utils.Constants;
import Bot.LavaPlayer.GuildAudioManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static Bot.ActualCommands.AudioCommands.VolumeCommand.botAndCallerAreInTheSameVoiceChannel;

public class PauseCommand implements ICommand {

    private final GuildAudioManager guildAudioManager;

    public PauseCommand(GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
    }

    @Override
    public String command() {
        return "pause";
    }

    @Override
    public String help() {
        return "Pauses the currently playing track";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (event.getGuild().getAudioManager().isConnected() && botAndCallerAreInTheSameVoiceChannel(event.getMember())) {
            Guild guild = event.getGuild();
            guildAudioManager.pause(guild, true);
        } else {
            event.getChannel().sendMessage("You are not in the same voice channel as the bot " + Constants.PENSIVE).queue();
        }
    }
}
