package at.felixmuellner.djpon3;

import at.felixmuellner.djpon3.listeners.*;
import at.felixmuellner.djpon3.songs.Song;
import at.felixmuellner.djpon3.songs.SongManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Felix on 01.04.2017.
 */
public class DjPon3 {

    public static void main(String[] args) throws DiscordException {
        //create config file
        File configFile = new File("./config.json");
        if (!configFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("./config.json"))) {
                writer.write("{\n\t\"token\": \"\",\n\t\"prefix\": \"prefix\"\n}\n");
                configFile = new File("./config.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //create song list file
        File songFile = new File("./songs.json");
        if (!songFile.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("./songs.json"))) {
                writer.write("[]\n");
                songFile = new File("./songs.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //load configuration
        Gson gson = new Gson();
        String jsonString = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("./config.json"))) {
            String line = reader.readLine();
            while (line != null) {
                jsonString += line;
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Configuration configuration = gson.fromJson(jsonString, Configuration.class);

        if (configuration.getToken().isEmpty()) {
            System.exit(0);
        }

        //load guild settings (if they exist)
        File guildSettingsFile = new File("./guildSettings.json");
        if (guildSettingsFile.exists()) {
            jsonString = "";
            try (BufferedReader reader = new BufferedReader(new FileReader("./guildSettings.json"))) {
                String line = reader.readLine();
                while (line != null) {
                    jsonString += line;
                    line = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Type guildSettingsType = new TypeToken<Map<String, GuildSettings>>(){}.getType();
            Map<String, GuildSettings> guildSettingsMap = gson.fromJson(jsonString, guildSettingsType);
            GuildSettingsManager.load(guildSettingsMap);
        }

        //create client
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(configuration.getToken());
        IDiscordClient client = clientBuilder.build();

        //register listeners
        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(new MessageReceivedListener(configuration));
        dispatcher.registerListener(new ReactionAddListener());
        dispatcher.registerListener(new ReadyListener());
        dispatcher.registerListener(new GuildCreateListener());
        dispatcher.registerListener(new UserVoiceChannelMoveListener());

        //load songs
        jsonString = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("./songs.json"))) {
            String line = reader.readLine();
            while (line != null) {
                jsonString += line;
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Song[] songs = gson.fromJson(jsonString, Song[].class);

        SongManager songManager = SongManager.getInstance();
        int loaded = songManager.load(songs);

        System.out.println("Loaded " + loaded + " / " + songs.length + " songs");

        //login
        try {
            client.login();
        } catch (RateLimitException e) {
            e.printStackTrace();
        }
    }
}
