package sample.model;

import javafx.scene.image.ImageView;
import org.json.simple.JSONObject;

/**
 * клас-модель для друга
 */
public class Friend {
    private ImageView photo;
    private String firstName;
    private String lastName;
    private String id;

    public Friend(JSONObject object) {
        this.photo = new ImageView((String)object.get("photo_50"));
        this.firstName = (String)object.get("first_name");
        this.lastName = (String)object.get("last_name");
        this.id = object.get("user_id").toString();
    }

    /**
     * Отримання фото друга
     * @return - фото
     */
    public ImageView getPhoto() {
        return photo;
    }

    /**
     * Встановлення фото для друга
     * @param photo - фото
     */
    public void setPhoto(ImageView photo) {
        this.photo = photo;
    }

    /**
     * Отримання імені друга
     * @return - ім'я
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Встановлення імені друга
     * @param firstName - ім'я
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Отримання прізвища друга
     * @return - прізвище
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Встановлення прізвища друга
     * @param lastName - прізвище
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Отримання id друга
     * @return - id
     */
    public String getId() {
        return id;
    }

    /**
     * Встановлення id друга
     * @param id - id
     */
    public void setId(String id) {
        this.id = id;
    }
}
