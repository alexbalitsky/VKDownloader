package sample.model;

/**
 * клас-модель для фото
 */
public class Photo {
    private String title;
    private String url;

    public Photo(String title, String url) {
        this.title = title;
        this.url = url;
    }

    /**
     * Отримання назви фото
     * @return - назва
     */
    public String getTitle() {
        return title;
    }

    /**
     * Встановлення назви фото
     * @param title - назва
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *Отримання URL фото
     * @return URl
     */
    public String getUrl() {
        return url;
    }

    /**
     *Встановлення URL фото
     * @param url - URL
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
