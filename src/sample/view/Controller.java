package sample.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sample.Authentification;
import sample.manager.Executor;
import sample.manager.PhotosDownloadManager;
import sample.manager.SongDownloadManager;
import sample.model.Album;
import sample.model.Friend;
import sample.model.Song;


import java.io.File;
import java.io.IOException;


/**
 * клас-контроллер
 */
public class Controller {
    private static Stage stage;

    private ObservableList<BorderPane> audioList = FXCollections.observableArrayList();
    private ObservableList<BorderPane> friendsList = FXCollections.observableArrayList();
    private ObservableList<BorderPane> friendsAudioList = FXCollections.observableArrayList();

    private ObservableList<BorderPane> albumsList = FXCollections.observableArrayList();

    private ObservableList<BorderPane> searchListFriends = FXCollections.observableArrayList();

    private final String audioRequest = "https://api.vk.com/method/audio.get?need_uer=0&count=6000&access_token=";
    private final String friendsRequest = "https://api.vk.com/method/friends.get?order=hints&fields=photo_50&access_token=";
    private final String albumsRequest = "https://api.vk.com/method/photos.getAlbums?need_system=1&need_covers=1&photo_sizes=1&access_token=";
    private final String photosRequest = "https://api.vk.com/method/photos.get?";

    @FXML
    private ListView<BorderPane> listMyMusic;

    @FXML
    private ListView<BorderPane> listMyFriends;

    @FXML
    private GridPane myPhoto;


    private int countOfFriends = 0;

    /**
     * Ініціалізація компонентів контролера
     * @throws IOException
     * @throws ParseException
     */
    @FXML
    private void initialize() throws IOException, ParseException {
        initializeMusicList();
        initializeFriendsLists();
        initializeAlbumItems("");
    }

    /**
     * Ініціалізація таблиці власних фотоальбомів
     * @param owner - власник фото
     * @throws IOException
     * @throws ParseException
     */
    private void initializeAlbumItems(String owner) throws IOException, ParseException {
        int columnIndex = 0;
        int rowIndex = 0;
        String response = Executor.getInstance().execute(albumsRequest + Authentification.getInstance().getAccessToken());
        JSONArray array = parse(response);
        for (int i = 0; i < array.size(); i++, columnIndex++) {
            JSONObject object = (JSONObject) array.get(i);
            Album album = new Album(object);

            String responsePhotos = Executor.getInstance().execute(photosRequest + "album_id=" + album.getId() + "&access_token=" + Authentification.getInstance().getAccessToken() + "&owner_id=" + owner);
            JSONArray photosList = parse(responsePhotos);
            album.setPhotosList(photosList);

            if (columnIndex == 4) {
                columnIndex = 0;

                rowIndex++;
            }
            BorderPane pane = new BorderPane();
            pane = this.createPhotosPane(pane, album);
            myPhoto.add(pane, columnIndex, rowIndex);
            myPhoto.setVgap(40);

        }


    }

    /**
     * Створення панелі-обробника для кожного альбому
     * @param pane - панель-обробник
     * @param album - вибраний альбом
     * @return - панель-обробник
     */
    private BorderPane createPhotosPane(BorderPane pane, Album album) {

        BorderPane bottom = new BorderPane();
        bottom.setCenter(new Label(album.getTitle()));
        pane.setCenter(album.getPhoto());
        pane.setBottom(bottom);
        pane.setCursor(Cursor.HAND);
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                pane.setRight(new ProgressIndicator());
                downloadPhotos(pane, album);
            }
        });

        return pane;
    }

    /**
     * Запуск додаткового потоку, який завантажує альбом
     * @param pane - кнопка-обробник
     * @param album - вибраний альбом
     */
    private void downloadPhotos(BorderPane pane, Album album) {
        PhotosDownloadManager manager = new PhotosDownloadManager();
        manager.setAlbum(album);
        Task<ImageView> task = manager.getTask();

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                ImageView result = task.getValue();
                pane.getChildren().remove(pane.getRight());
                pane.setRight(result);
            }
        });
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    /**
     * Ініціалізація списку власних аудіозаписів
     * @throws IOException
     * @throws ParseException
     */
    private void initializeMusicList() throws IOException, ParseException {
        String response = Executor.getInstance().execute(audioRequest + Authentification.getInstance().getAccessToken());
        JSONArray array = parse(response);
        audioList = this.setAudioList(0, audioList, array);

        listMyMusic.setItems(audioList);
    }

    /**
     * Ініціалізація списку аудіозаписів вибраного друга
     * @param id - id друга
     * @throws IOException
     * @throws ParseException
     */
    private void initializeFriendsMusicList(String id) throws IOException, ParseException {
        friendsAudioList.remove(0, friendsAudioList.size());
        String response = Executor.getInstance().execute(audioRequest + Authentification.getInstance().getAccessToken() + "&owner_id=" + id);
        JSONArray array = parse(response);
        friendsAudioList = this.setAudioList(1, friendsAudioList, array);
        listMyFriends.setFixedCellSize(30);
        listMyFriends.setItems(friendsAudioList);
    }

    /**
     * Ініціалізація списку друзів
     * @throws IOException
     * @throws ParseException
     */
    private void initializeFriendsLists() throws IOException, ParseException {
        String response = Executor.getInstance().execute(friendsRequest + Authentification.getInstance().getAccessToken());
        JSONArray array = parse(response);
        Friend friend = null;
        for (int i = countOfFriends; i < countOfFriends + 10; i++) {
            JSONObject object = (JSONObject) array.get(i);
            friend = new Friend(object);
            HBox hBox = new HBox();
            hBox.getChildren().add(new Label(friend.getFirstName() + " " + friend.getLastName(), friend.getPhoto()));
            BorderPane pane = new BorderPane();
            pane.setCursor(Cursor.HAND);
            pane.setLeft(hBox);
            pane = this.showMusicList(pane, friend);
            friendsList.add(pane);
        }
        countOfFriends += 10;

        friendsList.add(this.createBottomPane(array, true, friend));
        listMyFriends.setItems(friendsList);
    }

    /**
     * Створення панелі-обробника для кожного рядка списку аудіозаписів
     * @param begin - ознака списку аудіозаписів друзів
     * @param list - список
     * @param array - масив друзів
     * @return список
     * @throws ParseException
     */
    private ObservableList<BorderPane> setAudioList(int begin, ObservableList<BorderPane> list, JSONArray array) throws ParseException {
        for (int i = begin; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            Song song = new Song(object);
            BorderPane pane = new BorderPane();
            pane.setLeft(new Label(song.toString()));
            Button button = new Button("SAVE");
            button.setOpacity(0.5);
            button.setOnMouseEntered(event -> button.setOpacity(1));
            button.setOnMouseExited(event -> button.setOpacity(0.5));
            pane.setRight(button);
            pane.setOnMouseEntered(event -> pane.getRight().setStyle("visibility: visible"));
            pane.setOnMouseExited(event -> pane.getRight().setStyle("visibility: hidden"));
            button.setOnMouseClicked(event -> {
                try {
                    pane.setRight(new ProgressIndicator());
                    pane.setOnMouseExited(myEvent -> pane.getRight().setStyle("visibility: visible"));
                    downloadMusic(song, pane);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            list.add(pane);
        }
        if (begin == 1) {
            BorderPane pane = new BorderPane();
            pane.setCenter(new Label("To Friends List"));
            pane.setStyle("-fx-background-color: #cfe0ff");
            pane = this.toFriendsList(pane);
            list.add(pane);
        }
        return list;
    }

    /**
     * Створення обробника для запуску методу initializeFriendsMusicList
     * @param pane - обробник
     * @param friend - вибраний друг
     * @return обробник
     */
    private BorderPane showMusicList(BorderPane pane, Friend friend) {
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    initializeFriendsMusicList(friend.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        return pane;
    }

    /**
     * Створення переходів від списку аудіозаписів до списку друзів
     * @param array - масив друзів
     * @param isNext - ознака списку друзів
     * @param friend - вибраний друг
     * @return - кнопка-перехід
     */
    private BorderPane createBottomPane(JSONArray array, boolean isNext, Friend friend) {
        BorderPane pane = new BorderPane();

        Label label = new Label();

        if (isNext) {
            pane = this.loadFriends(pane, array, friend);
            label.setText("More friends");
        } else {
            pane = this.toFriendsList(pane);
            label.setText("To friends list");
        }


        HBox search = new HBox();

        Label searchLabel = new Label("Search");
        TextField searchField = new TextField();

        search.getChildren().addAll(searchLabel, searchField);

        pane.setCenter(label);
        pane.setRight(search);

        searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    searchListFriends.remove(0, searchListFriends.size());
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        if (object.get("first_name").equals(searchField.getCharacters().toString())) {
                            Friend friend = new Friend(object);
                            HBox hBox = new HBox();
                            hBox.getChildren().add(new Label(friend.getFirstName() + " " + friend.getLastName(), friend.getPhoto()));
                            BorderPane pane = new BorderPane();
                            pane.setCursor(Cursor.HAND);
                            pane.setLeft(hBox);
                            pane = showMusicList(pane, friend);
                            searchListFriends.add(pane);
                        }
                    }
                    searchListFriends.add(createBottomPane(array, false, friend));
                    if (searchListFriends.size() != 0)
                        listMyFriends.setItems(searchListFriends);
                }
            }
        });

        pane.setStyle("-fx-background-color: #cfe0ff");
        pane.setMargin(label, new Insets(0, 0, 0, 220));

        search.setOpacity(0.5);
        search.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                search.setOpacity(1);
            }
        });
        search.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                search.setOpacity(0.5);
            }
        });

        pane.setMargin(search, new Insets(15));

        search.setMargin(searchLabel, new Insets(3));

        return pane;
    }

    /**
     * Дозавантаження друзів
     * @param next - кнопка-перехід
     * @param array - масив друзів
     * @param friend - вибраний друг
     * @return кнопка перехід
     */
    private BorderPane loadFriends(BorderPane next, JSONArray array, Friend friend) {
        next.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                friendsList.remove(next);

                for (int i = countOfFriends; i < countOfFriends + 10; i++) {
                    JSONObject object = (JSONObject) array.get(i);
                    Friend friend = new Friend(object);
                    HBox hBox = new HBox();
                    hBox.getChildren().add(new Label(friend.getFirstName() + " " + friend.getLastName(), friend.getPhoto()));
                    BorderPane pane = new BorderPane();
                    pane.setCursor(Cursor.HAND);
                    pane.setLeft(hBox);
                    pane = showMusicList(pane, friend);
                    friendsList.add(pane);

                }
                countOfFriends += 10;
                if (countOfFriends + 10 >= array.size())
                    countOfFriends += array.size() - countOfFriends - 10;
                friendsList.add(next);
            }
        });
        return next;
    }

    /**
     * Створення переходу від списку аудіо до списку друзів
     * @param pane - кнопка-перехід
     * @return - кнопка-перехід
     */
    private BorderPane toFriendsList(BorderPane pane) {
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                listMyFriends.setFixedCellSize(55);
                listMyFriends.setItems(friendsList);
            }
        });
        return pane;
    }

    /**
     * Розбиває відповідь запросу у масив об’єктів
     * @param response - відповідь з серверу
     * @return масив об'єктів
     * @throws ParseException
     */
    private JSONArray parse(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonResponse = (JSONObject) parser.parse(response);
        return (JSONArray) jsonResponse.get("response");
    }

    /**
     * Вибрати директорію, де будуть зберігатися аудіозаписи
     * @throws IOException
     */
    @FXML
    private void chooseFileMusic() throws IOException {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose source file");
        File defaultDirectory = new File("Music");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(stage);
        SongDownloadManager.setAudioPath(selectedDirectory.getPath());
    }

    /**
     * Вибрати директорію, де будуть зберігатися фото
     * @throws IOException
     */
    @FXML
    private void chooseFilePhoto() throws IOException {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose source file");
        File defaultDirectory = new File("Photos");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(stage);
        PhotosDownloadManager.setPhotoPath(selectedDirectory.getPath());
    }

    /**
     * Запуск додаткового потоку, який завантажує пісню
     * @param song - пісня на завантаження
     * @param pane - панель-обробник
     * @throws InterruptedException
     * @throws IOException
     */
    private void downloadMusic(Song song, BorderPane pane) throws InterruptedException, IOException {
        SongDownloadManager manager = new SongDownloadManager();
        manager.setSong(song);
        Task<ImageView> task = manager.getTask();

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                ImageView result = task.getValue();
                pane.getChildren().remove(1);
                pane.setRight(result);
            }
        });
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();

    }


    /**
     * Встановлює stage
     * @param primaryStage
     */
    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

}
