package bot;

import bot.commandmanagement.GeneralCommandManager;
import bot.utils.Constants;
import bot.utils.Utils;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


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
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (!event.getJDA().getAudioManagers().isEmpty())
            if (event.getJDA().getAudioManagers().get(0).isConnected())
                if (!Utils.isActualUserLeftInVoiceChannel(event.getChannelLeft()))
                    event.getJDA().getAudioManagers().get(0).closeAudioConnection();
    }
}
