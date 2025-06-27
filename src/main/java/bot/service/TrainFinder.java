package bot.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;

import static bot.utils.Utils.resolve;
import static bot.utils.Utils.sendPOSTHTTPRequest;

/**
 * Class to receive and parse data from the train information site of the Hungarian State Railways
 */
public class TrainFinder
{
    private static final String MAV_URL = "http://vonatinfo.mav-start.hu/map.aspx/getData";

    public static class TrainInfo
    {
        private String relation;
        private String trainNumber;
        private String elviraID;
        private Integer delay;
        private String latitude;
        private String longitude;

        public TrainInfo()
        {
        }

        public TrainInfo(String relation, String trainNumber, String elviraID, Integer delay, String latitude,
            String longitude)
        {
            this.relation = relation;
            this.trainNumber = trainNumber;
            this.elviraID = elviraID;
            this.delay = delay;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getRelation()
        {
            return relation;
        }

        public void setRelation(String relation)
        {
            this.relation = relation;
        }

        public String getTrainNumber()
        {
            return trainNumber;
        }

        public void setTrainNumber(String trainNumber)
        {
            this.trainNumber = trainNumber;
        }

        public String getElviraID()
        {
            return elviraID;
        }

        public void setElviraID(String elviraID)
        {
            this.elviraID = elviraID;
        }

        public Integer getDelay()
        {
            return delay;
        }

        public void setDelay(Integer delay)
        {
            this.delay = delay;
        }

        public String getLatitude()
        {
            return latitude;
        }

        public void setLatitude(String latitude)
        {
            this.latitude = latitude;
        }

        public String getLongitude()
        {
            return longitude;
        }

        public void setLongitude(String longitude)
        {
            this.longitude = longitude;
        }
    }

    public static class TrainRoute
    {
        private String station;
        private boolean reached;
        private String expectedArrival;
        private String actualArrival;

        public TrainRoute()
        {
        }

        public TrainRoute(boolean reached)
        {
            this.reached = reached;
        }

        public TrainRoute(String station, boolean reached, String expectedArrival, String actualArrival)
        {
            this.station = station;
            this.reached = reached;
            this.expectedArrival = expectedArrival;
            this.actualArrival = actualArrival;
        }

        public String getExpectedArrival()
        {
            return expectedArrival;
        }

        public void setExpectedArrival(String expectedArrival)
        {
            this.expectedArrival = expectedArrival;
        }

        public String getActualArrival()
        {
            return actualArrival;
        }

        public void setActualArrival(String actualArrival)
        {
            this.actualArrival = actualArrival;
        }

        public String getStation()
        {
            return station;
        }

        public void setStation(String station)
        {
            this.station = station;
        }

        public boolean isReached()
        {
            return reached;
        }

        public void setReached(boolean reached)
        {
            this.reached = reached;
        }

        public void setArrivalTimes(List<String> arrivalTimes)
        {
            if (arrivalTimes.size() < 2)
                throw new IllegalArgumentException("Incorrect arrival time size!");

            this.expectedArrival = arrivalTimes.get(0);
            this.actualArrival = arrivalTimes.get(1);
        }
    }

    public static List<TrainInfo> maxDelay(int n) throws IOException, InterruptedException
    {
        var encodedResponse = sendPOSTHTTPRequest(MAV_URL,
            "{\"a\":\"TRAINS\",\"jo\":{\"pre\":true,\"history\":false,\"id\":false}}");

        String response = decompress(new GZIPInputStream(encodedResponse.body()));

        JsonElement root = JsonParser.parseReader(new StringReader(response));

        JsonArray trains = ((JsonObject) root).getAsJsonObject("d").getAsJsonObject("result").getAsJsonObject("Trains")
            .getAsJsonArray("Train");

        Stream<JsonElement> trainStream = StreamSupport.stream(trains.spliterator(), true)
            .filter(e -> e.getAsJsonObject().get("@Delay") != null);

        var sortedTrains = trainStream
            .sorted(Comparator.comparing(e -> ((JsonElement) e).getAsJsonObject().get("@Delay").getAsInt()).reversed())
            .limit(n)
            .collect(Collectors.toList());

        var ret = new ArrayList<TrainInfo>();

        for (var train : sortedTrains)
        {
            ret.add(
                new TrainInfo(
                    train.getAsJsonObject().get("@Relation").getAsString(),
                    train.getAsJsonObject().get("@TrainNumber").getAsString(),
                    train.getAsJsonObject().get("@ElviraID").getAsString(),
                    train.getAsJsonObject().get("@Delay").getAsInt(),
                    train.getAsJsonObject().get("@Lat").getAsString(),
                    train.getAsJsonObject().get("@Lon").getAsString()
                )
            );
        }

        return ret;
    }

    public static Map<TrainInfo, TrainRoute> findTrain(String start, String destination)
        throws IOException, InterruptedException
    {
        var startGeoLocation = Geocode.getBoundingSquare(start);
        var destinationGeoLocation = Geocode.getBoundingSquare(destination);

        var encodedResponse = sendPOSTHTTPRequest(MAV_URL,
            "{\"a\":\"TRAINS\",\"jo\":{\"pre\":true,\"history\":false,\"id\":false}}");

        String response = decompress(new GZIPInputStream(encodedResponse.body()));

        JsonElement root = JsonParser.parseReader(new StringReader(response));

        List<TrainInfo> trains = new ArrayList<>();

        ((JsonObject) root).getAsJsonObject("d").getAsJsonObject("result").getAsJsonObject("Trains")
            .getAsJsonArray("Train").forEach(e -> {

                float trainLongitude = e.getAsJsonObject().get("@Lon").getAsFloat();
                float trainLatitude = e.getAsJsonObject().get("@Lat").getAsFloat();

                if (isWithin(startGeoLocation, destinationGeoLocation, trainLongitude, trainLatitude))
                {
                    trains.add(new TrainInfo(
                        e.getAsJsonObject().get("@Relation").getAsString(),
                        e.getAsJsonObject().get("@TrainNumber").getAsString(),
                        e.getAsJsonObject().get("@ElviraID").getAsString(),
                        resolve(() -> e.getAsJsonObject().get("@Delay").getAsInt()),
                        e.getAsJsonObject().get("@Lon").getAsString(),
                        e.getAsJsonObject().get("@Lat").getAsString()
                    ));
                }
            });

        //        if (trainRoutes.size() == 0) {
        //            System.out.println("rák máv \"api\", offline af atm");
        //        }

        var info = getRelevantTrain(trains, start, destination);

        for (var entry : info.entrySet())
        {
            System.out.println("POSSIBLY RELEVANT TRAIN:");
            System.out.println(entry.getKey().relation);
            System.out.println("delay: " + entry.getKey().delay);
            System.out.println(
                "should arrive to " + entry.getValue().station + " at " + entry.getValue().expectedArrival);
            System.out.println("will arrive to " + entry.getValue().station + " at " + entry.getValue().actualArrival);
            System.out.println();
        }

        return null;
    }

    private static Map<TrainInfo, TrainRoute> getRelevantTrain(List<TrainInfo> trains, String start, String destination)
        throws IOException, InterruptedException
    {
        Map<TrainInfo, TrainRoute> ret = new HashMap<>();

        for (var train : trains)
        {
            var encodedHTMLFragment = sendPOSTHTTPRequest(MAV_URL,
                "{a: \"TRAIN\", jo: {v: \"" + train.elviraID + "\", vsz: \"" + train.getTrainNumber()
                    + "\", zoom: false, csakkozlekedovonat: true}}");

            String htmlResponse = decompress(new GZIPInputStream(encodedHTMLFragment.body()));

            var trainRoute = getRouteList(StringEscapeUtils.unescapeJava(htmlResponse));

            boolean foundFlag = false;
            for (var route : trainRoute)
            {
                if (route.getStation().contains(start))
                {
                    foundFlag = true;
                }

                if (route.getStation().contains(destination) && foundFlag)
                {
                    ret.put(train, route);
                    foundFlag = false;
                }
            }
        }

        return ret;
    }

    private static List<TrainRoute> getRouteList(String html)
    {
        //        String regPattern =
        //            "className=('row(_past)?_((odd)|(even)))'(.|\\s)+?STATION',\\s\\{\\si:\\s'[0-9]+',\\sa:\\s'\\p{Alpha}+";
        //String regPattern = "className=('row(_past)?_((odd)|(even)))(.|\\s)+?</tr>";
        String regPattern = "<tr(.|\\s)+?</tr>";

        Pattern pattern = Pattern.compile(regPattern, Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = pattern.matcher(html);
        List<TrainRoute> ret = new ArrayList<>();
        while (m.find())
        {
            var match = m.group();

            if (match.startsWith("<tr onmouseover"))
            {
                String[] tokens = match.split("<td");

                String station = parseStation(tokens[2]);
                boolean concurrency = tokens[0].substring(0, 100).contains("past");
                List<String> arrivalTimes = parseTimeList(tokens[4]);

                if (arrivalTimes.size() == 0 || (arrivalTimes.get(0) == null && arrivalTimes.get(1) == null))
                { // for the destination of the train, could probably be nicer but meh
                    arrivalTimes = parseTimeList(tokens[3]);
                }

                List<String> finalArrivalTimes = arrivalTimes; // cancer java
                ret.add(new TrainRoute(station, concurrency, resolve(() -> finalArrivalTimes.get(0)),
                    resolve(() -> finalArrivalTimes.get(1))));
            }
        }

        if (!ret.isEmpty())
            ret.get(0).setReached(true); // xd cancer api

        return ret;
    }

    private static String parseStation(String htmlFragment)
    {
        String regPattern = "'\\p{Lu}\\p{Ll}+[\\s-]*\\p{Lu}*\\p{Ll}*'";
        Pattern pattern = Pattern.compile(regPattern, Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = pattern.matcher(htmlFragment);

        if (m.find())
        {
            return m.group().substring(1, m.group().length() - 1);
        }

        return "";
    }

    private static List<String> parseTimeList(String html)
    {
        String regPattern = "[0-9][0-9]:[0-9][0-9]";
        Pattern pattern = Pattern.compile(regPattern, Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = pattern.matcher(html);

        List<String> ret = new ArrayList<>();

        while (m.find())
        {
            ret.add(m.group());
        }

        while (ret.size() < 2)
        {
            ret.add("-");
        }

        return ret;
    }

    // this method uses Jsoup and is an alternative to the getRouteList() method, however that one is faster (albeit probably less robust)
    private static List<TrainRoute> getRouteList2(String html)
    {
        Document document = Jsoup.parse(html);

        Elements elements = document.select("tr td");

        List<TrainRoute> ret = new ArrayList<>();

        for (int i = 0; i < elements.size() - 4; i += 5)
        {
            ret.add(new TrainRoute(elements.get(i).parent().html().substring(0, 100).contains("past")));
            ret.get(ret.size() - 1).station = elements.get(i + 1).text();

            if (i < elements.size() - 5)
            {
                ret.get(ret.size() - 1).setArrivalTimes(parseTimeList(elements.get(i + 3).text()));
            }
            else
            {
                ret.get(ret.size() - 1).setArrivalTimes(parseTimeList(elements.get(i + 2).text()));
            }
        }

        return ret;
    }

    private static boolean isWithin(Geocode.BoundingSquare loc1, Geocode.BoundingSquare loc2, float longitude, float latitude)
    {
        List<Float> latitudes = List.of(loc1.getNorthEast().getLatitude(), loc1.getSouthWest().getLatitude(),
            loc2.getNorthEast().getLatitude(), loc2.getSouthWest().getLatitude());
        List<Float> longitudes = List.of(loc1.getNorthEast().getLongitude(), loc1.getSouthWest().getLongitude(),
            loc2.getNorthEast().getLongitude(), loc2.getSouthWest().getLongitude());

        float minLat = Collections.min(latitudes);
        float maxLat = Collections.max(latitudes);
        float minLon = Collections.min(longitudes);
        float maxLon = Collections.max(longitudes);

        return longitude > minLon && longitude < maxLon
            && latitude > minLat && latitude < maxLat;
    }

    private static String decompress(GZIPInputStream gis) throws IOException
    {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8)))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
            gis.close();
            return sb.toString();
        }
    }

}
