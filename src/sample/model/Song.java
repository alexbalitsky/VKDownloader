package sample.model;

import javafx.beans.property.StringProperty;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * клас-модель для пісні
 */
public class Song {
    private String artist;
    private String title;
    private String  duration;
    private String url;

    public Song(JSONObject response) throws ParseException {
        this.artist = (String)response.get("artist");
        this.title = (String)response.get("title");
        this.url = (String)response.get("url");
        this.duration = response.get("duration").toString();
    }

    /**
     * Отримання тривалості пісні
     * @return - тривалість
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Встановлення тривалості пісні
     * @param duration - тривалість
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     *Отримання імені виконавця пісні
     * @return - ім'я
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Встановлення імені виконавця пісні
     * @param artist - ім'я
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Отримання назви пісні
     * @return - назва
     */
    public String getTitle() {
        return title;
    }

    /**
     * Встановлення назви пісні
     * @param title - назва
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Отримання URL пісні
     * @return URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Встановлення URL пісні
     * @param url - URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Приведення до радка
     * @return - рядок
     */
    @Override
    public String toString() {
        return this.getArtist() + " - " + this.getTitle() + " " + this.getDuration();
    }
}

