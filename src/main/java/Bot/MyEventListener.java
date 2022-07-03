package Bot;

import Bot.CommandManagement.GeneralCommandManager;
import Bot.Utils.Constants;
import Bot.Utils.Utils;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;


public class MyEventListener extends ListenerAdapter {

    private final GeneralCommandManager generalCommandManager = new GeneralCommandManager();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;

        String content = event.getMessage().getContentRaw();
        if (content.startsWith(Constants.PREFIX))
            generalCommandManager.handleCommand(event);

    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        if (!event.getJDA().getAudioManagers().isEmpty())
            if (event.getJDA().getAudioManagers().get(0).isConnected())
                if (!Utils.isActualUserLeftInVoiceChannel(event.getChannelLeft()))
                    event.getJDA().getAudioManagers().get(0).closeAudioConnection();
    }
}
