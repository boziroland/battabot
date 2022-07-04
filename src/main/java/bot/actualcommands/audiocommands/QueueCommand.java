package bot.actualcommands.audiocommands;

import bot.commandmanagement.ICommand;
import bot.lavaplayer.GuildAudioManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;

public class QueueCommand implements ICommand {

    GuildAudioManager guildAudioManager;

    public QueueCommand(GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
    }

    @Override
    public String command() {
        return "queue";
    }

    @Override
    public String help() {
        return "Shows the songs in the queue.";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Tracks");
        StringBuilder desc = new StringBuilder();

        if (guildAudioManager.getMusicManager() == null) {
            desc.append("No track in queue");
        } else {

            var tracks = guildAudioManager.getMusicManager().scheduler.getQueue();

            var trackNames = new ArrayList<String>();

            for (var track : tracks) {
                trackNames.add(track.getInfo().title);
            }

            desc.append("Current track: ").append(guildAudioManager.getMusicManager().player.getPlayingTrack().getInfo().title);

            if (guildAudioManager.getMusicManager().scheduler.isLooping())
                desc.append(" - looping");

            desc.append("\n\n");
            for (int i = 0; i < trackNames.size(); i++) {
                desc.append(i + 1).append(". ");
                desc.append(trackNames.get(i)).append("\n");
            }
            eb.setDescription(desc);
            eb.setColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));

            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }
}
