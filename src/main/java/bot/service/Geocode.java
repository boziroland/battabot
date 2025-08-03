package bot.service;

import bot.utils.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static bot.utils.Utils.getJsonFromAPI;

public class Geocode implements Serializable
{
    private static final long serialVersionUID = 1L;

    private static transient final String MAPS_API_URL = "https://maps.googleapis.com/maps/api/geocode";

    private static Map<String, BoundingSquare> locationCache;

    private static final String cacheFile = "data/geocache.batta";

    public static class BoundingSquare implements Serializable
    {
        private static final long serialVersionUID = 3015492490259699644L;

        public static class LatLon implements Serializable
        {
            private static final long serialVersionUID = 5489903418290118676L;

            private float latitude;
            private float longitude;

            public LatLon(float latitude, float longitude)
            {
                this.latitude = latitude;
                this.longitude = longitude;
            }

            public float getLatitude()
            {
                return latitude;
            }

            public float getLongitude()
            {
                return longitude;
            }
        }

        private LatLon northEast;
        private LatLon southWest;

        public BoundingSquare(float northEastLat, float northEastLon, float southWestLat, float southEastLon)
        {
            this.northEast = new LatLon(northEastLat, northEastLon);
            this.southWest = new LatLon(southWestLat, southEastLon);
        }

        public BoundingSquare(LatLon northEast, LatLon southWest)
        {
            this.northEast = northEast;
            this.southWest = southWest;
        }

        public LatLon getNorthEast()
        {
            return northEast;
        }

        public LatLon getSouthWest()
        {
            return southWest;
        }
    }

    private static void serializeCache()
    {
        try (var file = new FileOutputStream(cacheFile))
        {
            var out = new ObjectOutputStream(file);

            out.writeObject(locationCache);

            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, BoundingSquare> deserializeCache()
    { //lehet ezt nem itt kéne idk
        Map<String, BoundingSquare> data = null;
        try (var fileIn = new FileInputStream(cacheFile))
        {
            var in = new ObjectInputStream(fileIn);

            data = (Map<String, BoundingSquare>) in.readObject();
            //LOGGER.info("Deserialized cache!");

            in.close();
        }
        catch (FileNotFoundException e)
        {
            return new HashMap<>();
        }
        catch (ClassNotFoundException | IOException e)
        {
            e.printStackTrace();
        }

        return data;
    }

    private static BoundingSquare getBoundingSquareFromJson(String json)
    {
        JsonElement root = JsonParser.parseReader(new StringReader(json));

        float neLat =
            ((JsonObject) root).getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonObject("geometry")
                .getAsJsonObject("bounds").getAsJsonObject("northeast").get("lat").getAsFloat();

        float neLon =
            ((JsonObject) root).getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonObject("geometry")
                .getAsJsonObject("bounds").getAsJsonObject("northeast").get("lng").getAsFloat();

        float swLat =
            ((JsonObject) root).getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonObject("geometry")
                .getAsJsonObject("bounds").getAsJsonObject("southwest").get("lat").getAsFloat();

        float swLon =
            ((JsonObject) root).getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonObject("geometry")
                .getAsJsonObject("bounds").getAsJsonObject("southwest").get("lng").getAsFloat();

        return new BoundingSquare(neLat, neLon, swLat, swLon);
    }

    public static BoundingSquare getBoundingSquare(String location)
    {
        if (locationCache == null)
        {
            locationCache = deserializeCache();
        }

        location = location.toLowerCase(Locale.ROOT);

        if (locationCache.containsKey(location))
        {
            return locationCache.get(location);
        }

        String response = getJsonFromAPI(
            MAPS_API_URL
                + "/json"
                + "?address=" + location
                + "&key=" + getGoogleApiKey());

        BoundingSquare boundingSquare = getBoundingSquareFromJson(response);

        if (!locationCache.containsKey(location))
        {
            locationCache.put(location, boundingSquare);
            serializeCache();
        }

        return boundingSquare;
    }

    private static String getGoogleApiKey()
    {
        return (String)Utils.getPropertiesFromResourceFile("ConfigurationKeys.properties").get("MapsApiKey");
    }
}