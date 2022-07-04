package bot.actualcommands.textcommands;

import bot.commandmanagement.ICommand;
import bot.utils.Constants;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ActivityCommand implements ICommand {
    @Override
    public String command() {
        return "activity";
    }

    @Override
    public String help() {
        return "Changes the bot's activity\nUsage: !activity watching/playing/listening <your thing>";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length >= 3) {

            StringBuilder activity = new StringBuilder();

            for (int i = 2; i < args.length; i++)
                activity.append(args[i]).append(" ");

            switch (args[1].toLowerCase()) {
                case "watching":
                    event.getMessage().getJDA().getPresence().setActivity(Activity.watching(activity.toString()));
                    break;
                case "playing":
                    event.getMessage().getJDA().getPresence().setActivity(Activity.playing(activity.toString()));
                    break;
                case "listening":
                    event.getMessage().getJDA().getPresence().setActivity(Activity.listening(activity.toString()));
                    break;
                default:
                    event.getChannel().sendMessage("I can only watch (watching), play (playing) and listen (listening) " + Constants.PENSIVE).queue();
                    break;
            }
        }
    }
}
