package at.felixmuellner.djpon3.songs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Felix on 05.04.2017.
 */
public class SongManager {
    private static SongManager instance = new SongManager();

    private List<Song> songs;

    private SongManager() {}

    public static SongManager getInstance() {
        return SongManager.instance;
    }

    public File getSong(String songTitle) {
        for (Song song : this.songs) {
            if (song.getTitle().equals(songTitle)) {
                return song.getFile();
            }
        }
        return null;
    }

    public String getTitle(File songFile) {
        for (Song song : this.songs) {
            if (song.getFile().equals(songFile)) {
                return song.getTitle();
            }
        }
        return null;
    }

    public int load(Song[] songArray) {
        this.songs = new ArrayList<Song>();
        for (int i = 0; i < songArray.length; ++i) {
            if (songArray[i].checkFile()) {
                songs.add(songArray[i]);
            }
        }
        return this.songs.size();
    }

    public Song randomSong() {
        int randomIndex = new Random().nextInt(this.songs.size());
        return this.songs.get(randomIndex);
    }

    public List<Song> search(String searchTerm) {
        List<Song> results = new ArrayList<Song>();
        for (Song song : this.songs) {
            if (song.searchTitle(searchTerm)) {
                results.add(song);
            }
        }
        for (Song song : this.songs) {
            if (song.searchEpisode(searchTerm) && !results.contains(song)) {
                results.add(song);
            }
        }
        return results;
    }
}
