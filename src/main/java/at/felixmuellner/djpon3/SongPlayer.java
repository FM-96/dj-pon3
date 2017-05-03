package at.felixmuellner.djpon3;

import at.felixmuellner.djpon3.songs.SongManager;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.audio.AudioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 23.04.2017.
 */
public class SongPlayer {
    public static void addSong(IGuild guild, String songTitle, boolean play) {
        boolean allowInterruption = GuildSettingsManager.getInstance().isAllowInterruption(guild);
        String voiceChannelId = GuildSettingsManager.getInstance().getVoiceChannelId(guild);

        IVoiceChannel voiceChannel = guild.getVoiceChannelByID(voiceChannelId);
        if (voiceChannel == null) {
            //configured voice channel not found, try to fall back to default channel
            if (guild.getVoiceChannels().size() == 0) {
                //the guild doesn't have any voice channels at all
                return;
            } else {
                voiceChannel = guild.getVoiceChannels().get(0);
            }
        }
        voiceChannel.join();

        File songFile = SongManager.getInstance().getSong(songTitle);
        if (songFile != null) {
            AudioPlayer audioPlayer = AudioPlayer.getAudioPlayerForGuild(guild);
            try {
                if (play && allowInterruption) {
                    audioPlayer.clear();
                    audioPlayer.queue(songFile);
                    audioPlayer.setPaused(false);
                } else {
                    if (!play && audioPlayer.getPlaylistSize() == 0) {
                        audioPlayer.setPaused(true);
                    }
                    audioPlayer.queue(songFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        } else {
            //song file not found; this should never happen
            throw new IllegalStateException("Song file not found");
        }
    }

    public static boolean isPlay(IGuild guild) {
        AudioPlayer audioPlayer = AudioPlayer.getAudioPlayerForGuild(guild);
        return !audioPlayer.isPaused();
    }

    public static List<String> getQueue(IGuild guild) {
        AudioPlayer audioPlayer = AudioPlayer.getAudioPlayerForGuild(guild);
        List<AudioPlayer.Track> trackList = audioPlayer.getPlaylist();
        List<String> queue = new ArrayList<String>();
        for (AudioPlayer.Track track : trackList) {
            queue.add(SongManager.getInstance().getTitle((File) track.getMetadata().get("file")));
        }
        return queue;
    }

    public static void next(IGuild guild) {
        boolean allowInterruption = GuildSettingsManager.getInstance().isAllowInterruption(guild);
        if (allowInterruption) {
            AudioPlayer audioPlayer = AudioPlayer.getAudioPlayerForGuild(guild);
            if (audioPlayer.getPlaylistSize() > 1) {
                audioPlayer.skip();
            } else {
                SongPlayer.stop(guild);
            }
        }
    }

    public static void pause(IGuild guild) {
        AudioPlayer audioPlayer = AudioPlayer.getAudioPlayerForGuild(guild);
        audioPlayer.setPaused(true);
    }

    public static void play(IGuild guild) {
        AudioPlayer audioPlayer = AudioPlayer.getAudioPlayerForGuild(guild);
        if (audioPlayer.getPlaylistSize() > 0) {
            audioPlayer.setPaused(false);
        }
    }

    public static void stop(IGuild guild) {
        boolean allowInterruption = GuildSettingsManager.getInstance().isAllowInterruption(guild);
        if (allowInterruption) {
            AudioPlayer audioPlayer = AudioPlayer.getAudioPlayerForGuild(guild);
            audioPlayer.clear();
        }
    }
}
