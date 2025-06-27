package bot.actualcommands.textcommands;

import bot.commandmanagement.ICommand;
import bot.utils.Constants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

import static bot.service.TrainFinder.findTrain;
import static bot.service.TrainFinder.maxDelay;

public class MavCommand implements ICommand
{

    @Override
    public String command()
    {
        return "mav";
    }

    @Override
    public String help()
    {
        return "```"
            + "Usage:\n"
            + Constants.PREFIX + command() + "<start station/city> <end station/city> : Returns information about every train currently on their way between the given start and end stations."
            + Constants.PREFIX + command() + "<delay> (<amount>) : Returns information about the top <amount> most delayed trains at the time of the message."
            + "```";
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event)
    {
        if (args.length == 2)
        {
            try
            {
                int trainAmount = Integer.parseInt(args[1]);
                var trains = maxDelay(trainAmount);

                // TODO: send message
            }
            catch (NumberFormatException e)
            {
                event.getChannel().sendMessage("That's not a number").queue();
            }
            catch (IOException | InterruptedException e)
            {
                e.printStackTrace();
                event.getChannel().sendMessage("Error while trying to find train!").queue();
            }

        }
        else if (args.length == 3)
        {
            String startStation = args[1];
            String endStation = args[2];

            try
            {
                var train = findTrain(startStation, endStation);

                // TODO: send message
            }
            catch (IOException | InterruptedException e)
            {
                e.printStackTrace();
                event.getChannel().sendMessage("Error while trying to find train!").queue();
            }
        }
    }
}
