package Bot.ActualCommands.AudioCommands;

import Bot.CommandManagement.ICommand;
import Bot.LavaPlayer.GuildAudioManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


//this is a special command class, it has no actual command
//its execute function gets called when nothing else matches the called command
//and it'll see in its execute method whether it actually executes something or not
public class LocalFileCommand implements ICommand {

    private final String trackFolder = "data/sounds/tracks/";
    private final GuildAudioManager guildAudioManager;
    private final Map<String, String> namesAndExtensions;

    public LocalFileCommand(GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
        namesAndExtensions = new HashMap<>();

        try {
            var files = Files.walk(Paths.get(trackFolder)).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toSet());
            for (var file : files) {
                String nameAndExt = file.getName().toLowerCase();

                String name = nameAndExt.substring(0, nameAndExt.lastIndexOf('.'));
                String extension = nameAndExt.substring(nameAndExt.lastIndexOf('.') + 1);

                namesAndExtensions.put(name, extension);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String command() {
        return null;
    }

    @Override
    public String help() {
        return "yeet";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {

        args[0] = args[0].toLowerCase();

        if (namesAndExtensions.containsKey(args[0])) {
            String file = args[0] + "." + namesAndExtensions.get(args[0]);
            guildAudioManager.loadAndPlay(trackFolder + file, event, false);
        }
    }

    public boolean hasCommand(String command) {
        return namesAndExtensions.containsKey(command);
    }
}
