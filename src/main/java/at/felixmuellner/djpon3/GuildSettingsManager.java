package at.felixmuellner.djpon3;

import com.google.gson.Gson;
import sx.blah.discord.handle.obj.IGuild;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Felix on 25.04.2017.
 */
public class GuildSettingsManager {
    private static GuildSettingsManager instance = null;

    private Map<String, GuildSettings> guildSettingsMap;

    private GuildSettingsManager() {}

    public static synchronized GuildSettingsManager getInstance() {
        if (GuildSettingsManager.instance == null) {
            GuildSettingsManager.instance = new GuildSettingsManager();
            GuildSettingsManager.instance.guildSettingsMap = new HashMap<>();
        }
        return GuildSettingsManager.instance;
    }

    public static synchronized void load(Map<String, GuildSettings> savedSettings) {
        if (GuildSettingsManager.instance == null) {
            GuildSettingsManager.instance = new GuildSettingsManager();
            GuildSettingsManager.instance.guildSettingsMap = savedSettings;
        } else {
            throw new IllegalStateException("Settings already loaded");
        }
    }

    public synchronized boolean isAllowInterruption(IGuild guild) {
        if (this.guildSettingsMap.containsKey(guild.getStringID())) {
            return this.guildSettingsMap.get(guild.getStringID()).isAllowInterruption();
        }
        return true;
    }

    public synchronized long getVoiceChannelId(IGuild guild) {
        if (this.guildSettingsMap.containsKey(guild.getStringID())) {
            long savedId = this.guildSettingsMap.get(guild.getStringID()).getVoiceChannelId();
            if (guild.getVoiceChannelByID(savedId) != null) {
                return savedId;
            }
        }
        if (guild.getVoiceChannels().size() > 0) {
            return guild.getVoiceChannels().get(0).getLongID();
        }
        return -1;
    }

    public synchronized void setAllowInterruption(IGuild guild, boolean allowInterruption) {
        GuildSettings guildSettings;
        if (this.guildSettingsMap.containsKey(guild.getStringID())) {
            guildSettings = this.guildSettingsMap.get(guild.getStringID());
        } else {
            guildSettings = new GuildSettings();
            if (guild.getVoiceChannels().size() > 0) {
                guildSettings.setVoiceChannelId(guild.getVoiceChannels().get(0).getLongID());
            } else {
                guildSettings.setVoiceChannelId(-1);
            }
        }
        guildSettings.setAllowInterruption(allowInterruption);

        this.guildSettingsMap.put(guild.getStringID(), guildSettings);
        this.save();
    }

    public synchronized void setVoiceChannelId(IGuild guild, Long voiceChannelId) {
        GuildSettings guildSettings;
        if (this.guildSettingsMap.containsKey(guild.getStringID())) {
            guildSettings = this.guildSettingsMap.get(guild.getStringID());
        } else {
            guildSettings = new GuildSettings();
            guildSettings.setAllowInterruption(true);
        }
        guildSettings.setVoiceChannelId(voiceChannelId);

        this.guildSettingsMap.put(guild.getStringID(), guildSettings);
        this.save();
    }

    private void save() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(this.guildSettingsMap) + "\n";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./guildSettings.json"))) {
            writer.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
