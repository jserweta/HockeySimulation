package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * Projekt z Symulacji Dynamicznej Systemów Złożonych
 * ''Zagraj w hokeja z ładunkami elektrycznymi''
 *
 * Poniższy program modeluje pole elektryczne w formie mini-gry.
 * W polu elektrycznym umieszczamy ładunki elektryczne oraz elektron,
 * który porusza się bez tarcia po powierzchni.
 *
 * Celem gry jest trafienie elektronem do bramki unikając kolizji
 * z przeszkodami oraz granicami pola.
 *
 * @author Przemysław Nocoń
 * @author Mateusz Ruciński
 * @author Jakub Serweta
 */
public class Main extends Application {
    /**
     * Medoda odpowiadająca za wygenerowanie interfejsu graficznego.
     * @param primaryStage tworzy główne pole, któremu nadajemy odpowiednie cechy
     * @throws Exception  jeżeli utworzenie plaszy sie nie powiedzie
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hockey");
        primaryStage.setScene(new Scene(root, 1200, 900));
        primaryStage.show();
    }

    /**
     * Główna metoda, która uruchamia całą symulację.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
