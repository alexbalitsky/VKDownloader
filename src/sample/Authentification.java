package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.simple.parser.ParseException;


import java.io.IOException;

/**
 * Клас для авторизації
 */
public class Authentification {
    private static Authentification authentification;
    private String clientID;
    private String apiVersion;
    private String redirectURI;
    private final WebView browser;
    private final WebEngine webEngine;
    private Stage primaryStage;
    private static String accessToken;

    Parent root;

    private Authentification() {
        clientID = "4865339";
        apiVersion = "5.30";
        redirectURI = "https://oauth.vk.com/blank.html";
        browser = new WebView();
        webEngine = browser.getEngine();
    }

    public static Authentification getInstance() {
        if (authentification == null) {
            authentification = new Authentification();
        }
        return authentification;
    }
    /**
     * виконання авторизації
     * @throws IOException
     */

    public void performAuthentification() throws IOException {
        final Stage authentificationWindow = new Stage(StageStyle.DECORATED);
        authentificationWindow.setScene(new Scene(browser));
        authentificationWindow.setTitle("Vk Authentification");
        authentificationWindow.setWidth(650);
        authentificationWindow.setHeight(600);
        authentificationWindow.setResizable(false);
        authentificationWindow.initModality(Modality.WINDOW_MODAL);
        authentificationWindow.initOwner(primaryStage);
        String url = "https://oauth.vk.com/authorize?" +
                "client_id=" + clientID + "&" +
                "scope=audio,photos&" +
                "redirect_uri=" + redirectURI + "&" +
                "display=popup&" +
                "v=" + apiVersion + "&" +
                "revoke=1&" +
                "response_type=token";
        webEngine.load(url);

        authentificationWindow.show();
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                String responseUrl = webEngine.getLocation();
                if (responseUrl.contains("#access_token")) {
                    accessToken = getTokenString(responseUrl);
                    authentificationWindow.hide();
                    Parent root = null;
                    FXMLLoader loader = new FXMLLoader();
                    try {
                        root = loader.load(getClass().getResource("view/downloader.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(Main.class.getResource("style/style.css").toExternalForm());
                    primaryStage.setScene(scene);
                    primaryStage.setResizable(false);
                    primaryStage.show();
                }

            }
        });
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    /**
     * встановлення головної сцени
     * @param primaryStage головна сцена
     */

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * првертає токен у вигляді рядку
     * @param inputUrl
     * @return токен у вигляді рядку
     */
    private String getTokenString(String inputUrl) {
        int from = inputUrl.indexOf("#access_token=") + 14;
        int to = inputUrl.indexOf("&", from);
        String returnString = "";
        try {
            returnString = inputUrl.substring(from, to);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Uncorrect URL");
        }
        return returnString;
    }

    /**
     * отримання токену доступу
     * @return токен доступу
     */
    public String getAccessToken() {
        return accessToken;
    }
}

