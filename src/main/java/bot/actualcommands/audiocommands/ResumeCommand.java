package bot.actualcommands.audiocommands;

import bot.commandmanagement.ICommand;
import bot.utils.Constants;
import bot.lavaplayer.GuildAudioManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static bot.actualcommands.audiocommands.VolumeCommand.botAndCallerAreInTheSameVoiceChannel;

public class ResumeCommand implements ICommand {

    private final GuildAudioManager guildAudioManager;

    public ResumeCommand(GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
    }

    @Override
    public String command() {
        return "resume";
    }

    @Override
    public String help() {
        return "Resumes a paused track";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (!guildAudioManager.getMusicManager().player.isPaused()) {
            event.getChannel().sendMessage("Resume WHAT " + Constants.THINKING).queue();
        } else {
            if (event.getGuild().getAudioManager().isConnected() && botAndCallerAreInTheSameVoiceChannel(event.getMember())) {
                Guild guild = event.getGuild();
                guildAudioManager.pause(guild, false);
            } else {
                event.getChannel().sendMessage("You are not in the same voice channel as the bot " + Constants.PENSIVE).queue();
            }
        }
    }
}
