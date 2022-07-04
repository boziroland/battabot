package bot.actualcommands.audiocommands;

import bot.commandmanagement.ICommand;
import bot.lavaplayer.GuildAudioManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class PlayCommand implements ICommand {

    private final GuildAudioManager guildAudioManager;

    public PlayCommand(GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
    }

    @Override
    public String command() {
        return "play";
    }

    @Override
    public String help() {
        return "play music duh";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length > 1) {

            if (args[1].contains("https://")) { // meh
                guildAudioManager.loadAndPlay(args[1] + "&c=TVHTML5&cver=7.20190319", event, false);

            } else {
                StringBuilder search;
                int startIndex;

                if (args[1].equalsIgnoreCase("-sc")) {
                    search = new StringBuilder("scsearch:");
                    startIndex = 2;
                } else {
                    search = new StringBuilder("ytsearch:");

                    if (args[1].equalsIgnoreCase("-yt"))
                        startIndex = 2;
                    else
                        startIndex = 1;
                }

                for (int i = startIndex; i < args.length; i++)
                    search.append(args[i]).append(" ");

                guildAudioManager.loadAndPlay(search.toString(), event, false);
            }
            guildAudioManager.getMusicManager().scheduler.setLooping(false);
        }
    }

    @Override
    public List<String> getAliases() {
        return List.of("p");
    }
}
