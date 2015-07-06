package sample.manager;

import javafx.concurrent.Task;
import javafx.scene.image.ImageView;
import org.codehaus.plexus.util.FileUtils;
import sample.model.Album;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * клас для створення завдання завантаження альбому
 */
public class PhotosDownloadManager {
    private static String photoPath;

    private Album album;

    public PhotosDownloadManager() {
    }

    /**
     * Отримання альбому
     * @return альбом
     */
    public Album getAlbum() {
        return album;
    }

    /**
     * Встановлення альбому
     * @param album - альбом
     */
    public void setAlbum(Album album) {
        this.album = album;
    }


    /**
     * Отримання директорії, де зберігаються фото
     * @return директорія
     * @throws IOException
     */
    public static String getPhotoPath() throws IOException {
        FileReader reader = new FileReader("photoPath.txt");
        BufferedReader bufferedReader = new BufferedReader(reader);
        PhotosDownloadManager.photoPath = bufferedReader.readLine();
        return photoPath;
    }

    /**
     * Встановлення директорії, де будуть  зберігатися фото
     * @param photoPath - директорія
     * @throws IOException
     */
    public static void setPhotoPath(String photoPath) throws IOException {
        FileWriter writer = new FileWriter("photoPath.txt", false);
        BufferedWriter bufferedWriter = new BufferedWriter(writer, 8*1024);
        bufferedWriter.write(photoPath);
        bufferedWriter.flush();
    }


    /**
     * Отримання завдання для завантаження альбому
     * @return завдання
     */
    public Task<ImageView> getTask() {
        return task;

    }

    private Task<ImageView> task = new Task<ImageView>() {
        @Override
        protected ImageView call() throws Exception {
            for (int i = 0; i < album.getPhotoList().size(); i++) {
                String pathname = PhotosDownloadManager.getPhotoPath() + "\\" + album.getPhotoList().get(i).getTitle();
                try {
                    File destination = new File(pathname + ".jpg");
                    System.out.println(destination.getPath());
                    if (!destination.exists()) {
                        FileUtils.copyURLToFile(new URL(album.getPhotoList().get(i).getUrl()), destination);

                    }
                } catch (FileNotFoundException e) {
                    System.out.print("ERROR " + PhotosDownloadManager.getPhotoPath());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new ImageView("sample/images/check19.png");
        }
    };
}
