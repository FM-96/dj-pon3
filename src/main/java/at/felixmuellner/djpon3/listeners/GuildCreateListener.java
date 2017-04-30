package at.felixmuellner.djpon3.listeners;

import at.felixmuellner.djpon3.GuildSettingsManager;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;

/**
 * Created by Felix on 06.04.2017.
 */
public class GuildCreateListener implements IListener<GuildCreateEvent> {
    @Override
    public void handle(GuildCreateEvent event) {
        event.getGuild().getVoiceChannelByID(GuildSettingsManager.getInstance().getVoiceChannelId(event.getGuild())).join();
    }
}
