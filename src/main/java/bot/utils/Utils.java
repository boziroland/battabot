package bot.utils;

import bot.service.Translator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

public class Utils {

    public static EmbedBuilder createBasicReactionEmbed(String title, List<String> fields) {
        EmbedBuilder ret = new EmbedBuilder();
        ret.setTitle(StringEscapeUtils.unescapeHtml4(title));
        for (var f : fields) {
            ret.addField((fields.indexOf(f) + 1) + "\u20E3 " + StringEscapeUtils.unescapeHtml4(f), "", false);
        }
        return ret;
    }

    public static EmbedBuilder createBasicReactionEmbed(String title, List<String> fields, String translateTo) {
        EmbedBuilder ret = new EmbedBuilder();
        ret.setTitle(Translator.getInstance().translate("en", translateTo, StringEscapeUtils.unescapeHtml4(title)));
        for (var f : fields) {
            ret.addField((fields.indexOf(f) + 1) + "\u20E3 " + Translator.getInstance().translate("en", translateTo, StringEscapeUtils.unescapeHtml4(f)), "", false);
        }
        return ret;
    }

    private static String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char) (i + 'A')) : null;
    }

    public static String getJsonFromAPI(String url) {
        var receivedContent = new StringBuilder();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            int status = connection.getResponseCode();
            if (status == 200) {
                var in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String receivedLine;
                while ((receivedLine = in.readLine()) != null)
                    receivedContent.append(receivedLine);
                in.close();
                connection.disconnect();
            } else {
                throw new RuntimeException("Response: " + status);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return receivedContent.toString();
    }

    public static String getJsonPropertyValue(String json, String value) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(json).getAsJsonObject();
        return obj.get(value).getAsString();
    }

    public static Integer sendHttpRequest(String url, String type, @Nullable String body) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(type);
            if (body != null) {
                connection.addRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-length", Integer.toString(body.length()));
                connection.setDoOutput(true);
                connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
            }
            int status = connection.getResponseCode();
            connection.disconnect();
            return status;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 69;
    }

    public static Boolean isActualUserLeftInVoiceChannel(AudioChannelUnion channel) {
        if (channel == null) return false;
        int userCount = 0;
        for (var m : channel.getMembers()) {
            if (!m.getUser().isBot())
                userCount++;
        }
        return userCount != 0;
    }

    public static <T> Optional<T> resolveToOptional(final Supplier<T> supplier)
    {
        try
        {
            final T result = supplier.get();
            return Optional.ofNullable(result);
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    public static <T> T resolve(final Supplier<T> supplier)
    {
        return resolveToOptional(supplier).orElse(null);
    }

    public static HttpResponse<InputStream> sendPOSTHTTPRequest(String url, String body)
        throws IOException, InterruptedException
    {
        HttpClient httpClient = HttpClient.newHttpClient();

        return httpClient.send(
            HttpRequest.newBuilder().header("Content-Type", "application/json; charset=UTF-8").uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofInputStream());
    }

    public static Properties getPropertiesFromResourceFile(String fileInResourcesFolder){
        Properties properties = null;
        try {
            FileReader reader = new FileReader("src/main/resources/" + fileInResourcesFolder);
            properties = new Properties();
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

}
