package bot.actualcommands.audiocommands;

import bot.commandmanagement.ICommand;
import bot.utils.Constants;
import bot.lavaplayer.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class RemoveCommand implements ICommand {

    private final GuildAudioManager guildAudioManager;

    public RemoveCommand(GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
    }

    @Override
    public String command() {
        return "remove";
    }

    @Override
    public String help() {
        return "Removes a track by its position in the queue or part of its name.\nUsage: " +
                Constants.PREFIX + "remove <number/title>";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        BlockingQueue<AudioTrack> tracks = guildAudioManager.getMusicManager().scheduler.getQueue();

        if (args.length > 1) {
            if (args[1].chars().allMatch(Character::isDigit)) {
                try {
                    int index = Integer.parseInt(args[1]) - 1;

                    //TODO that blockingqueue is really making this a lot harder

                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage(Constants.FACE_WITH_RAISED_EYEBROWS).queue();
                }
            } else {// if a text is given

            }
        }
    }

    @Override
    public List<String> getAliases() {
        return List.of("rm");
    }
}
