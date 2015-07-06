package sample.model;

import javafx.scene.image.ImageView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * клас-модель для альбому
 */
public class Album {
    private String id;
    private String title;
    private ImageView photo;
    private ArrayList<Photo> photosList;

    public Album(JSONObject object) {
        this.id = object.get("aid").toString();
        this.title = (String)object.get("title");
        this.photo = new ImageView((String)((JSONObject)((JSONArray)object.get("sizes")).get(1)).get("src"));
        this.photosList = new ArrayList<Photo>();
    }


    /**
     * Отримання id альбому
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Встановлення id альбому
     * @param id - id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Отримання назви альбому
     * @return назва
     */
    public String getTitle() {
        return title;
    }

    /**
     * Встановлення назви альбому
     * @param title - назва
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Отримання фото з альбому
     * @return - фото
     */
    public ImageView getPhoto() {
        return photo;
    }

    /**
     * Встановлення фото на альбом
     * @param photo - фото
     */
    public void setPhoto(ImageView photo) {
        this.photo = photo;
    }

    /**
     * Отримання усіх фото з альбому
     * @return усі фото
     */
    public ArrayList<Photo> getPhotoList() {
        return photosList;
    }

    /**
     * Встановлення всіх фото альбому
     * @param photoList - усі фото
     */
    public void setPhotosList(JSONArray photoList) {
        for (int i = 0; i < photoList.size(); i++) {
            String url = (String)((JSONObject)(photoList.get(i))).get("src_xxbig");
            if (url == null){
                url = (String)((JSONObject)(photoList.get(i))).get("src_xbig");
            }
            if (url == null){
                url = (String)((JSONObject)(photoList.get(i))).get("src_big");
            }
            if (url == null){
                url = (String)((JSONObject)(photoList.get(i))).get("src");
            }
            if (url == null){
                url = (String)((JSONObject)(photoList.get(i))).get("src_small");
            }
            this.photosList.add(new Photo((((JSONObject)(photoList.get(i))).get("pid")).toString(), url));
        }
    }
}
