package bot.actualcommands.textcommands;

import bot.commandmanagement.ICommand;
import bot.service.TrainFinder;
import bot.service.TrainFinderOld;
import bot.utils.Constants;
import bot.utils.Utils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static bot.service.TrainFinderOld.findTrain;
import static bot.service.TrainFinderOld.findTrain2;
import static bot.service.TrainFinderOld.maxDelay;

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
                if (args[1].matches("-?[1-9]\\d*|0"))
                {
                    int trainAmount = Integer.parseInt(args[1]);

                    if (trainAmount < 1000)
                    {
                        if (trainAmount < 1)
                        {
                            trainAmount = 1;
                        }
                        else if (trainAmount > 25)
                        {
                            trainAmount = 25;
                        }

                        event.getChannel().sendMessage(
                            getMostDelayedTrains(trainAmount)
                        ).queue();
                    }
                    else
                    {
                        event.getChannel().sendMessage(getTrainWithId(args[1])).queue();
                    }
                }
                else
                {
                    event.getChannel().sendMessage(getTrainWithId(args[1])).queue(); // TODO make it nicer
                }
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
            try
            {
                var trainsBetweenStations =
                    getTrainsBetweenStations(args[1].toLowerCase(Locale.ROOT), args[2].toLowerCase(
                        Locale.ROOT));
                for (var train : trainsBetweenStations)
                {
                    Utils.sendMessage(event.getChannel(), train);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                event.getChannel().sendMessage("Error while trying to find train or no train found!").queue();
            }
        }
    }

    private String getMostDelayedTrains(int trainAmount) throws IOException, InterruptedException
    {
        var trains = maxDelay(trainAmount);

        StringBuilder maxDelayTrainsString =
            new StringBuilder("```\nThe top " + trainAmount + " train(s) with the most delay are:\n\n");
        trains.forEach(train -> {
            maxDelayTrainsString.append(train.getTrainNumber()).append(": ")
                .append(train.getRelation()).append(" - ").append(train.getDelay())
                .append(" minutes\n");
        });
        maxDelayTrainsString.append("\n```");

        return maxDelayTrainsString.toString();
    }

    private List<String> getTrainsBetweenStations(String start, String destination)
        throws IOException, InterruptedException
    {
        var train = findTrain2(start, destination);

        List<String> possibleTrains = new ArrayList<>();

        if (!train.isEmpty())
        {
            train.forEach((key, value) -> {
                StringBuilder possibleTrain = new StringBuilder();
                possibleTrain.append("```");

                possibleTrain
                    .append("Train ")
                    .append(key.getTrainNumber())
                    .append(": ")
                    .append(key.getRelation())
                    .append("\n")
                    .append("Current delay: ")
                    .append(key.getDelay())
                    .append(" minutes\n\n");

                possibleTrain
                    .append(StringUtils.rightPad("Station", 30))
                    .append(StringUtils.rightPad("Expected Dep/Arr", 20))
                    .append(StringUtils.rightPad("Actual Dep/Arr", 20))
                    .append("Reached?\n");

                possibleTrain
                    .append("==============================================================================\n");

                value.forEach(tr -> {
                    possibleTrain
                        .append(StringUtils.rightPad(tr.getStation(), 30))
                        .append(StringUtils.rightPad(tr.getExpectedArrival(), 20))
                        .append(StringUtils.rightPad(tr.getActualArrival(), 20))
                        .append(tr.isReached() ? "+" : "-")
                        .append("\n");
                });
                possibleTrain
                    .append("==============================================================================\n");
                possibleTrain.append("```");
                possibleTrains.add(possibleTrain.toString());
            });
        }
        else
        {
            possibleTrains.add("No such trains found!");
        }

        return possibleTrains;
    }

    private String getTrainWithId(String elviraId)
    {
        try
        {
            var details = TrainFinderOld.getTrainDetails(elviraId);

            StringBuilder ret = new StringBuilder("```\n");

            details.forEach(station -> {
                ret.append(station.getStation())
                    .append("\t\tscheduled arrival: ").append(station.getExpectedArrival())
                    .append("\t actual arrival: ").append(station.getActualArrival())
                    .append("\t").append(station.isReached() ? "(already reached)" : "").append("\n");
            });

            if (ret.length() == 4)
            {
                ret.append("No train with such id found!");
            }
            ret.append("```");

            return ret.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "No train with the given ID exists!";
        }
    }

    @Override
    public List<String> getAliases()
    {
        return List.of("mav");
    }
}
