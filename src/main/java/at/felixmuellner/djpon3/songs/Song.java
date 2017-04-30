package at.felixmuellner.djpon3.songs;

import java.io.File;

/**
 * Created by Felix on 05.04.2017.
 */
public class Song {
    private String episode;
    private transient File file;
    private String path;
    private String title;

    public Song() {}

    public boolean checkFile() {
        this.file = new File(this.path);
        if (this.file.exists() && !this.file.isDirectory() && this.file.canRead()) {
            return true;
        }
        return false;
    }

    public File getFile() {
        if (this.file == null) {
            this.checkFile();
        }
        return this.file;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean searchEpisode(String searchTerm) {
        return this.episode.toLowerCase().contains(searchTerm.toLowerCase());
    }

    public boolean searchTitle(String searchTerm) {
        return this.title.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
