package sample.manager;

import javafx.concurrent.Task;
import javafx.scene.image.ImageView;
import org.codehaus.plexus.util.FileUtils;
import sample.model.Song;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * клас для створення завдання завантаження аудіо
 */
public class SongDownloadManager {
    private static String audioPath;

    private Song song;

    public SongDownloadManager() {

    }


    /**
     * Отримання директорії, де зберігається аудіо
     * @return директорія
     * @throws IOException
     */
    public static String getAudioPath() throws IOException {
        FileReader reader = new FileReader("audioPath.txt");
        BufferedReader bufferedReader = new BufferedReader(reader);
        SongDownloadManager.audioPath = bufferedReader.readLine();
        return audioPath;
    }

    /**
     * Встановлення директорії, де буде зберігатися аудіо
     * @param audioPath - директорія
     * @throws IOException
     */
    public static void setAudioPath(String audioPath) throws IOException {
        FileWriter writer = new FileWriter("audioPath.txt", false);
        BufferedWriter bufferedWriter = new BufferedWriter(writer, 8*1024);
        bufferedWriter.write(audioPath);
        bufferedWriter.flush();

    }

    /**
     * Отримання пісні
     * @return пісня
     */
    public Song getSong() {
        return song;
    }

    /**
     * Встановлення пісні
     * @param song - пісня
     */
    public void setSong(Song song) {
        this.song = song;
    }

    /**
     * Отримання завдання для завантаження пісні
     * @return завдання
     */
    public Task<ImageView> getTask() {
        return task;
    }

    private Task<ImageView> task = new Task<ImageView>() {
        @Override
        protected ImageView call() throws Exception {
            String pathname = SongDownloadManager.getAudioPath() + "\\" + song.getArtist() + " - " + song.getTitle();
            try {
                File destination = new File(pathname + ".mp3");
                System.out.println(destination.getPath());
                if (!destination.exists()) {
                    FileUtils.copyURLToFile(new URL(song.getUrl()), destination);

                }
            } catch (FileNotFoundException e) {
                System.out.print("ERROR " + SongDownloadManager.getAudioPath());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ImageView("sample/images/check19.png");
        }
    };
}
