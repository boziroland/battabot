package Bot.CommandManagement;

import Bot.ActualCommands.TextCommands.ActivityCommand;
import Bot.ActualCommands.TextCommands.HangmanCommand;
import Bot.ActualCommands.TextCommands.AmenCommand;
import Bot.ActualCommands.TextCommands.DictionaryCommand;
import Bot.ActualCommands.TextCommands.HelpCommand;
import Bot.ActualCommands.TextCommands.PollCommand;
import Bot.ActualCommands.TextCommands.QuizCommand;
import Bot.ActualCommands.TextCommands.RandomCommand;
import Bot.ActualCommands.TextCommands.RandomcaseCommand;
import Bot.ActualCommands.TextCommands.RemindMeCommand;
import Bot.ActualCommands.TextCommands.SayCommand;
import Bot.ActualCommands.TextCommands.ShutdownCommand;
import Bot.ActualCommands.TextCommands.SpaghettiCommand;
import Bot.ActualCommands.TextCommands.TranslateCommand;
import Bot.ActualCommands.TextCommands.ZipCommand;
import Bot.Utils.Constants;
import Bot.CommandManagement.ImageCommands.ImageManager;
import Bot.LavaPlayer.GuildAudioManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;

public class GeneralCommandManager extends CommandManager {

    private final ImageManager imageManager;
    public static final GuildAudioManager guildAudioManager = new GuildAudioManager();
    private final AudioCommandManager audioCommandManager;
    private final HashMap<String, String> aliases = new HashMap<>();

    public GeneralCommandManager() {
        add(new RandomCommand());
        add(new TranslateCommand());
        add(new ShutdownCommand());
        add(new RandomcaseCommand());
        add(new ActivityCommand());
        add(new SayCommand());
        add(new RemindMeCommand());
        add(new SpaghettiCommand());
        add(new HangmanCommand());
        add(new HelpCommand(commands));
        add(new PollCommand());
        add(new QuizCommand());
        add(new DictionaryCommand());
        add(new ZipCommand());
        add(new AmenCommand());

        imageManager = new ImageManager();
        audioCommandManager = new AudioCommandManager(guildAudioManager);

        initalizeAliases();
    }

    @Override
    public void handleCommand(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentRaw();
        String[] contentArr = messageContent.split("\\s+");

        contentArr[0] = contentArr[0].substring(1);
        String command = contentArr[0].toLowerCase();

        if (!commands.containsKey(command) && aliases.containsKey(command)) {
            command = aliases.get(command);
        }

        if (command.equals("commands")) {
            listCommands(event.getChannel(), "Text commands");
        } else {

            if (commands.containsKey(command))
                commands.get(command).execute(contentArr, event);
            else if (audioCommandManager.hasCommand(command))
                audioCommandManager.handleCommand(event);
            else
                handleImageCommand(contentArr, event);
        }
    }

    private void handleImageCommand(String[] theCommands, MessageReceivedEvent event) {

        if (theCommands[0].equals(ImageManager.addCommand)) {
            if (theCommands.length >= 2 && !commands.containsKey(theCommands[1]) && !audioCommandManager.hasCommand(theCommands[1]))
                imageManager.checkImage(theCommands[1], event);
            else
                event.getChannel().sendMessage("No command was given, or it already exists " + Constants.PENSIVE).queue();
        } else if (theCommands[0].equals(ImageManager.listCommand)) {
            imageManager.help(event.getChannel());
        } else {
            imageManager.checkAndSend(theCommands[0], event.getChannel());
        }
    }

    private void initalizeAliases() {
        for (var command : commands.entrySet()) {
            for (var alias : command.getValue().getAliases()) {
                aliases.put(alias, command.getKey());
            }
        }
    }
}
