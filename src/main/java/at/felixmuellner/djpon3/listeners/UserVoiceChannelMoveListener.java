package at.felixmuellner.djpon3.listeners;

import at.felixmuellner.djpon3.GuildSettingsManager;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;

/**
 * Created by Felix on 06.04.2017.
 */
public class UserVoiceChannelMoveListener implements IListener<UserVoiceChannelMoveEvent> {
    @Override
    public void handle(UserVoiceChannelMoveEvent event) {
        if (event.getUser().equals(event.getClient().getOurUser())) {
            GuildSettingsManager.getInstance().setVoiceChannelId(event.getGuild(), event.getNewChannel().getLongID());
        }
    }
}
