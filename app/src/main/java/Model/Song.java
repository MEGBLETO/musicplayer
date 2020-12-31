package Model;

public class Song {

    private long id;
    private String title;
    private String artist;
    private String songUrl;
    private long numAlbum;
    private String singerPlaylist;


    public Song(long id, String title, String artist, String songUrl, long numAlbum, String singerPlaylist) {

        this.id = id;
        this.title = title;
        this.artist = artist;
        this.songUrl = songUrl;
        this.numAlbum = numAlbum;
        this.singerPlaylist = singerPlaylist;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public long numAlbum() {
        return numAlbum;
    }

    public String getSingerPlaylist() {
        return singerPlaylist;
    }
}
