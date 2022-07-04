package bot.actualcommands.audiocommands;

import bot.commandmanagement.ICommand;
import bot.utils.Constants;
import bot.lavaplayer.GuildAudioManager;
import bot.lavaplayer.TrackScheduler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LoopCommand implements ICommand {

    private final GuildAudioManager guildAudioManager;

    public LoopCommand(GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
    }

    @Override
    public String command() {
        return "loop";
    }

    @Override
    public String help() {
        return "loop music duh";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        TrackScheduler scheduler = guildAudioManager.getGuildAudioPlayer(event.getGuild()).scheduler;

        if (event.getGuild().getAudioManager().isConnected()) {
            if (!scheduler.isLooping()) {
                scheduler.setLooping(true);
                event.getChannel().sendMessage("Looping mode activated!").queue();
            } else {
                scheduler.setLooping(false);
                event.getChannel().sendMessage("Looping has been turned off.").queue();
            }
        } else {
            event.getChannel().sendMessage("but how " + Constants.THINKING).queue();
        }
    }
}
