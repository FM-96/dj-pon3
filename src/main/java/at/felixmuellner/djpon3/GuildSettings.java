package at.felixmuellner.djpon3;

/**
 * Created by Felix on 25.04.2017.
 */
public class GuildSettings {
    private boolean allowInterruption;
    private long voiceChannelId;

    public GuildSettings() {}

    public boolean isAllowInterruption() {
        return this.allowInterruption;
    }

    public long getVoiceChannelId() {
        return this.voiceChannelId;
    }

    public void setAllowInterruption(boolean allowInterruption) {
        this.allowInterruption = allowInterruption;
    }

    public void setVoiceChannelId(long voiceChannelId) {
        this.voiceChannelId = voiceChannelId;
    }
}
