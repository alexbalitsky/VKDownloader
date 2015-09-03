package sample;


import javafx.application.Application;
import javafx.stage.Stage;
import sample.view.Controller;

/**
 * Головний клас
 */
public class Main extends Application {

    /**
     * запуск програми
     * @param primaryStage - головна сцена
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Controller.setStage(primaryStage);
        primaryStage.setTitle("Vk Downloader");
        Authentification.getInstance().setPrimaryStage(primaryStage);
        Authentification.getInstance().performAuthentification();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
