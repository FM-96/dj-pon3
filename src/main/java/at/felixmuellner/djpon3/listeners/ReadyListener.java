package at.felixmuellner.djpon3.listeners;

import at.felixmuellner.djpon3.GuildSettingsManager;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;

/**
 * Created by Felix on 28.04.2017.
 */
public class ReadyListener implements IListener<ReadyEvent> {
    @Override
    public void handle(ReadyEvent event) {
        for (IGuild guild : event.getClient().getGuilds()) {
            String voiceChannelId = GuildSettingsManager.getInstance().getVoiceChannelId(guild);
            IVoiceChannel voiceChannel = guild.getVoiceChannelByID(voiceChannelId);
            voiceChannel.join();
        }
    }
}
