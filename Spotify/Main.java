package Spotify;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    public class User {
        private final String id, username, password;
        private final Map<String, Playlist> playlists;
        private final PlaybackSession playback;

        public User(String username, String password) {
            this.id = UUID.randomUUID().toString();
            this.username = username;
            this.password = password;
            this.playback = new PlaybackSession();
            this.playlists = new HashMap<>();
        }

        public Playlist createPlayList(String name) {
            Playlist playlist = new Playlist(name, this);
            playlists.put(playlist.getId(), playlist);
            return playlist;
        }

        public void removePlaylist(String name) {
            Playlist playlist = playlists.get(name);
            playlists.remove(name);
        }

        public void play(Song song) {
            playback.play(song);
        }

        public void pause() {
            playback.pause();
        }

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public Playlist getPlaylist(String playlistId) {
            return playlists.get(playlistId);
        }

        public List<Playlist> getPlaylists() {
            return playlists.values().stream().toList();
        }
    }

    public class PlaybackSession {
        private Song currentSong;
        private boolean isPlaying = false;
        private int currentTime;

        public synchronized void play(Song song) {
            this.currentSong = song;
            this.isPlaying = true;
            currentTime = 0;
            System.out.println("Now playing: " + song.getTitle());
        }

        public synchronized void pause() {
            if (currentSong != null && isPlaying) {
                isPlaying = false;
                System.out.println("Paused: " + currentSong.getTitle());
            }
        }

        public void seekTo(int time) {
            currentTime = time;
            // Seek to the specified time in the song
            // ...
        }

        public synchronized Song getCurrentSong() {
            return currentSong;
        }

        public synchronized boolean isPlaying() {
            return isPlaying;
        }
    }

    public class Playlist {
        private final String id, name;
        private final User owner;
        private final List<Song> songs;

        public Playlist(String name, User owner) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.owner = owner;
            this.songs = new CopyOnWriteArrayList<>();
        }

        public void addSong(Song song) {
            songs.add(song);
        }

        public void removeSong(Song song) {
            songs.remove(song);
        }

        public List<Song> getSongs() {
            return List.copyOf(songs);
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class Song {
        private final String id, title;
        private final Artist artist;
        private final Album album;
        private final Duration duration;

        public Song(String title, Artist artist, Album album, Duration duration) {
            this.id = UUID.randomUUID().toString();
            this.title = title;
            this.artist = artist;
            this.album = album;
            this.duration = duration;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Artist getArtist() {
            return artist;
        }

        public String getArtistName() {
            return artist.getName();
        }

        public Album getAlbum() {
            return album;
        }
    }

    public class Album {
        private final String id;
        private final String title;
        private final Artist artist;
        private final List<Song> songs;

        public Album(String title, Artist artist) {
            this.id = UUID.randomUUID().toString();
            this.title = title;
            this.artist = artist;
            this.songs = new ArrayList<>();
        }

        public void addSong(Song song) {
            songs.add(song);
        }

        public void addSongs(List<Song> songs) {
            this.songs.addAll(songs);
        }

        public List<Song> getSongs() {
            return songs;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Artist getArtist() {
            return artist;
        }
    }

    public class Artist {
        private final String id;
        private final String name;
        private final List<Album> albums;

        public Artist(String name) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.albums = new ArrayList<>();
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void addAlbum(Album album) {
            albums.add(album);
        }

        public void addAlbums(List<Album> albums) {
            this.albums.addAll(albums);
        }

        public List<Album> getAlbums() {
            return albums;
        }
    }
}
