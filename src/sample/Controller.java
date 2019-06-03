package sample;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Definiuje całą logikę aplikacji, zaczynając od ustawień interfejsu
 * graficznego, po modelowanie pola elektrycznego, symulację ruchu krążka
 * oraz wykrywanie kolizji.
 */
public class Controller implements Initializable {
    /** Definicja głównej planszy gry*/
    @FXML private Pane field;

    /** Opcja ustawienia bardzo łatwego poziomu trudności*/
    @FXML private RadioButton peaceful;
    /** Opcja ustawienia łatwego poziomu trudności*/
    @FXML private RadioButton easy;
    /** Opcja ustawienia średniego poziomu trudności*/
    @FXML private RadioButton medium;
    /** Opcja ustawienia trudnego poziomu trudności*/
    @FXML private RadioButton hard;
    /** Stworzenie obiektu ustawiającego poziom trudności gry*/
    private final ToggleGroup difficulty = new ToggleGroup();

    /** Aktualny poziom trudności - 0=bardzo łatwy
     1=łatwy
     2=średni
     3=trudny     */
    private int diff = 0;

    /** Pole do wyświetlania komunikatów o aktualnym stanie gry*/
    @FXML private Text messages;
    /** Wyswietlenie aktualnie ustawionej masy elektronu */
    @FXML private Text mass_counter;
    /** Suwak do ustawenia pożądanej masy elektronu */
    @FXML private Slider mass_changer;

    /** Przycisk uruchamiający symulację */
    @FXML private Button start;
    /** Przycisk resetujący planszę */
    @FXML private Button reset;
    /** Przycisk wstrzymujący symulację, nie resetując ustawień */
    @FXML private Button pause;
    /** Aktualny tryb, w którym jest plansza 0 - stop, 1 - work, 2 - win/lost */
    private int mode = 0;

    /** Stworzenie listy przeszkód dla poziomu łatwego*/
    private ArrayList<Rectangle> er = new ArrayList<>();
    /** Stworzenie listy przeszkód dla poziomu średniego*/
    private ArrayList<Rectangle> mr = new ArrayList<>();
    /** Stworzenie listy przeszkód dla poziomu trudnego*/
    private ArrayList<Rectangle> hr = new ArrayList<>();

    @FXML private Rectangle barrier1;
    @FXML private Rectangle barrier21;
    @FXML private Rectangle barrier22;
    @FXML private Rectangle barrier31;
    @FXML private Rectangle barrier32;
    @FXML private Rectangle barrier33;
    @FXML private Rectangle barrier34;
    @FXML private Rectangle barrier35;
    @FXML private Rectangle barrier36;

    /** Odpowiada za ustawienie znaku krążka*/
    @FXML private ToggleButton ball_sign_toggle;
    /** Tworzy nowy obiekt - krążek o odpowiednim promieniu*/
    private Electron ball = new Electron( 7 );

    /**
     * Inicjalizuje obiekty potrzebne do obsługi oraz wyświetlenia
     * poszczególnych elementów interfejsu graficznego.
     */
    @Override
    public void initialize(URL url, ResourceBundle resource){

        /** Utworzenie krążka */
        field.getChildren().add( ball );
        /** Tworzy tablicę źródeł pola elektrycznego */
        ArrayList<Source> sources = new ArrayList<>();  //creating array for sources

        peaceful.setToggleGroup(difficulty);
        easy.setToggleGroup(difficulty);
        medium.setToggleGroup(difficulty);
        hard.setToggleGroup(difficulty);

        /** Zdefiniowanie obiektu górnej ściany bocznej bramki*/
        Rectangle post1 = new Rectangle(1100,340,50,5);
        /** Zdefiniowanie obiektu dolnej ściany bocznej bramki*/
        Rectangle post2 = new Rectangle(1100,455,50,5);
        /** Zdefiniowanie tylnej ściany bramki */
        Rectangle backpost = new Rectangle (1145, 345, 5, 110);
        /** Utworzenie górnej ściany bocznej bramki na planszy*/
        field.getChildren().add(post1);
        /** Utworzenie dolnej ściany bocznej bramki na planszy*/
        field.getChildren().add(post2);
        /** Utworzenie tylnej ściany bramki na planszy*/
        field.getChildren().add(backpost);


        /** Utworzenie przeszkód na planszy */
        addRectangles();
        /** Ustawienie przeźroczystości przeszkód trybu łatwego. */
        for(Rectangle rect: er){rect.setFill(Color.TRANSPARENT);}
        /** Ustawienie przeźroczystości przeszkód trybu średniego. */
        for(Rectangle rect: mr){rect.setFill(Color.TRANSPARENT);}
        /** Ustawienie przeźroczystości przeszkód trybu trudnego. */
        for(Rectangle rect: hr){rect.setFill(Color.TRANSPARENT);}

        /** Tworzenie źródeł pola elektrycznego za pomocą myszy. Lewy przycisk myszy (PRIMARY) tworzy
         * ładunek ładunek dodatni (czerwony), prawy przycik myszy (SECONDARY) tworzy ładunek ujemny (niebieski). */
        field.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            if (! (me.getPickResult().getIntersectedNode() instanceof Source)) {
                if (me.getButton().equals(MouseButton.PRIMARY)) {
                    Source source = new Source(me.getX(), me.getY(), 7, true);
                    sources.add(source);
                    addEventHandler(source);
                    field.getChildren().add(source);


                }
                if (me.getButton().equals(MouseButton.SECONDARY)) {
                    Source source = new Source(me.getX(), me.getY(), 7, false);
                    sources.add(source);
                    addEventHandler(source);
                    field.getChildren().add(source);
                }
            }
        });


        /** Dostosowanie pola gry do wybranego poziomu trudności. */
        difficulty.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle old_toggle, Toggle new_toggle) {
                /** Ustawnienie przeźroczystości dla wszystkich przeszkód - tryb bardzo łatwy. */
                if (difficulty.getSelectedToggle() == peaceful) {
                    for(Rectangle rect: er){rect.setFill(Color.TRANSPARENT);}
                    for(Rectangle rect: mr){rect.setFill(Color.TRANSPARENT);}
                    for(Rectangle rect: hr){rect.setFill(Color.TRANSPARENT);}
                    diff = 0;
                }
                /** Ustawienie jednej przeszkody na planszy - tryb prosty.*/
                if (difficulty.getSelectedToggle() == easy) {
                    for(Rectangle rect: er){rect.setFill(Color.BLACK);}
                    for(Rectangle rect: mr){rect.setFill(Color.TRANSPARENT);}
                    for(Rectangle rect: hr){rect.setFill(Color.TRANSPARENT);}
                    diff = 1;
                }
                /** Ustawienie dwóch przeszkód na planszy - tryb średni.*/
                else if (difficulty.getSelectedToggle() == medium) {
                    for(Rectangle rect: er){rect.setFill(Color.TRANSPARENT);}
                    for(Rectangle rect: mr){rect.setFill(Color.BLACK);}
                    for(Rectangle rect: hr){rect.setFill(Color.TRANSPARENT);}
                    diff = 2;
                }
                /** Ustawienie trzech przeszkód na planszy - tryb trudny. */
                else if (difficulty.getSelectedToggle() == hard) {
                    for(Rectangle rect: er){rect.setFill(Color.TRANSPARENT);}
                    for(Rectangle rect: mr){rect.setFill(Color.TRANSPARENT);}
                    for(Rectangle rect: hr){rect.setFill(Color.BLACK);}
                    diff = 3;
                }

            }
        });

        /** Funckja zmieniająca masę elektronu, obsługuje suwak. */
        mass_changer.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                ball.mass = new_val.intValue();   //cast new_val to int
                mass_counter.setText(String.valueOf(ball.mass));
            }
        });

        /** Pętla odpowiadająca za wykonanie symulacji */
        AnimationTimer timer = new AnimationTimer()
        {
            /**
             * Wywołanie funkcji implementujących rozkład sił pola elektrycznego,
             * sprawdzania kolizji oraz stanu rozgrywki, a także symulacja ruchu krążka.
             * @param currentNanoTime Aktualny czas
             */
            public void handle(long currentNanoTime)
            {
                double[] results = DetermineForce(sources, this);
                change_electron(results);
                ball.change();
                ball.setCenterX(ball.locX);
                ball.setCenterY(ball.locY);
                //if(results[0]>10||results[0]<-10||results[1]>10||results[1]<-10)
                boundary_checker(this);
                collision_checker(this);
                goal_check(post1,post2,backpost,this);
            }
        };

        /** Obsługa przycisku start. Zmiana trybu planszy na work - 1 oraz zatrzymanie czasu symuacji. */
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(mode != 2) {
                    timer.start();
                    mode = 1;
                }
            }
        });

        /** Obsługa przycisku pause. Zmiana trybu planszy na pasue - 0 oraz zatrzymanie czasu symulacji. */
        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (mode != 2) {
                    timer.stop();
                    mode = 0;
                }
            }
        });

        /**
         * Obssługa przycisku reset. Wyczyszczenie planszy, zresetowanie
         * pozycji krążka oraz ustawienie trybu planszy na 0 - stan poczatkowy
         * oraz zatrzymanie czasu symulacji.
         */
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                ball.reset();
                ball.setCenterX(ball.locX);
                ball.setCenterY(ball.locY);
                for(Source source: sources){
                    field.getChildren().remove(source);
                }
                sources.clear();
                messages.setText("");
                mode = 0;
                double [] results = {0,0};
                timer.stop();
            }
        });
        ball_sign_toggle.setOnAction(e -> {
            ball.change_sign();
        });

    }

    /**
     *  Metoda usuwająca postawiony ładunek elektryczny poprzez ponowne jego kliknięcie.
     * @param node współrzędne ładunku elektrycznego
     */
    private void addEventHandler(Node node) {
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
            field.getChildren().remove(node);
        });
    }

    /**
     * Odpowiada za przypisanie przyśpieszenia obliczonego w metodzie DetermineForce do zmiennych accX oraz accY.
     * @param results tablica przyśpieszeń
     */
    private void change_electron(double[] results){
        ball.accX = results[0];
        ball.accY = results[1];
    }

    /**
     * Główna metoda modelująca pole elektryczne poprzez obliczenie odległości r pomiędzy źródłem,
     * a elektronem, oraz przyśpieszeń składowych a_x, a_y za pomocą rozkładu siły F na składowe,
     * oraz wyprowadzeniu wzorów z zależności trygonometrycznych.
     * @param sources Lista źródeł emitujących pole elektryczne umieszczonych na planszy
     * @param timer Czas, według którego wykonujemy symulację.
     * @return double [] results tablica przyśpieszeń składowych
     */
    private double[] DetermineForce(ArrayList<Source> sources, AnimationTimer timer){
        /** Tablica dwuelementwoa, w której zapisane zostaną przyśpieszenia składowe {a_x, a_y} */
        double[] results = {0,0};
        for(Source source: sources){
            /** stała oddziaływań ładunków w próżni */
            final int k = 100000000;
            /** Odległość r pomiędzy źródłem pola elektrycznego, a elektronem. */
            double r = Math.sqrt(Math.pow(ball.locX - source.getCenterX(),2)+Math.pow(ball.locY - source.getCenterY(),2))/1.0;
            /** Sprawdzenie czy wystąpiła kolizja krążka z ładunkiem elektrycznym. */
            if(r<14){
                ball.setCenterX(ball.plocX);
                ball.setCenterY(ball.plocY);
                timer.stop();
                messages.setText("You Lost");
                mode = 2;
            }
            /** Sinus kąta alfa pomiędzy F_x, a F - rysunek w dokumentacji*/
            double sinus = (source.getCenterX() - ball.locX)/r;
            /** Cosinus kąta alfa pomiędzy F_x, a F - rysunek w dokumentacji */
            double cosin = (source.getCenterY() - ball.locY)/r;

            if(source.sign == ball.sign) {
                /** Przyspieszenie składowe a_x, gdy znak źródła i krążka są równe.*/
                results[0] -= (sinus / (Math.pow(r, 2)*ball.mass)) * k;
                /** Przyspieszenie składowe a_y, gdy znak źródła i krążka są równe*/
                results[1] -= (cosin / (Math.pow(r, 2)*ball.mass)) * k;
            }
            else{
                /** Przyspieszenie składowe a_x, gdy znak źródła i krążka są różne*/
                results[0] += (sinus / (Math.pow(r, 2)*ball.mass)) * k;
                /** Przyspieszenie składowe a_y, gdy znak źródła i krążka są róźne*/
                results[1] += (cosin / (Math.pow(r, 2)*ball.mass)) * k;
            }
        }
        return results;
    }

    /**
     * Metoda tworząca przeszkody dla wszystkich poziomów trudności.
     */
    private void addRectangles(){
        er.add(barrier1);
        mr.add(barrier21);
        mr.add(barrier22);
        hr.add(barrier31);
        hr.add(barrier32);
        hr.add(barrier33);
        hr.add(barrier34);
        hr.add(barrier35);
        hr.add(barrier36);
    }

    /**
     * Dynamicznie sprawdza czy wystąpiła kolizja z przeszkodą. Jeżeli tak wyświetla komunikat 'You Lost'
     * Sprawdzenie odbywa sie za pomocą metody intersect, która jest częścią klasy Shape.
     * @param timer Czas do dynamicznego wykonywania sprawdzenia
     */
    private void collision_checker(AnimationTimer timer){
        if (diff == 1){
            for(Rectangle rect: er){
                Shape intersect = Shape.intersect(rect, ball);
                if (intersect.getBoundsInLocal().getWidth() != -1) {
                    ball.locX=ball.plocX;
                    ball.locY=ball.plocY;
                    timer.stop();
                    messages.setText("You Lost");
                    mode = 2;
                }
            }
        }
        else if (diff == 2){
            for(Rectangle rect: mr){
                Shape intersect = Shape.intersect(rect, ball);
                if (intersect.getBoundsInLocal().getWidth() != -1) {
                    ball.setCenterX(ball.plocX);
                    ball.setCenterY(ball.plocY);
                    timer.stop();
                    messages.setText("You Lost");
                    mode = 2;
                }
            }
        }
        else if (diff == 3){
            for(Rectangle rect: hr){
                Shape intersect = Shape.intersect(rect, ball);
                if (intersect.getBoundsInLocal().getWidth() != -1) {
                    ball.setCenterX(ball.plocX);
                    ball.setCenterY(ball.plocY);
                    timer.stop();
                    messages.setText("You Lost");
                    mode = 2;
                }
            }
        }
    }

    /**
     * Sprawdza, czy krążek trafił do bramki poprzez badanie kolizji z tylną ścianką
     * backpost. Kolizja wykrywana jest za pomocą funkcji intersect, z klasy Shape.
     * @param post1 górna ściana bramki
     * @param post2 dolna ściana bramki
     * @param backpost tylna ściana bramki
     * @param timer Czas, który determinuje symulację
     */
    private void goal_check(Rectangle post1, Rectangle post2, Rectangle backpost, AnimationTimer timer){
        Shape intersect1 = Shape.intersect(post1, ball);
        Shape intersect2 = Shape.intersect(post2, ball);
        Shape intersect_back = Shape.intersect(backpost, ball);
        if ((intersect1.getBoundsInLocal().getWidth() != -1) || (intersect2.getBoundsInLocal().getWidth() != -1)) {
            ball.bounce();
        }
        else if (intersect_back.getBoundsInLocal().getWidth() != -1) {
            if (ball.plocX > 1150) ball.pounce();
            else win(timer);
        }
    }

    /**
     * Metoda zatrzymuje czas symulacji, wyświetla napis 'You Win' i ustawia tryb planszy na 2 - win/lose
     * @param timer Czas symulacji
     */
    private void win(AnimationTimer timer){
        ball.locX=ball.plocX;
        ball.locY=ball.plocY;
        timer.stop();
        messages.setText("You Win");
        mode = 2;
    }

    /**
     * Metoda wykrywa kolizję z obramowaniem planszy, na podstawie położenia krążka.
     * Jeźeli kolizja wystąpiła, czas jest zatrzymywany i wyswietlany jest napis 'You lost'
     * @param timer Czas symulacji
     */
    private void boundary_checker(AnimationTimer timer){
        if(ball.locX < 0 || ball.locX > 1200 || ball.locY < 0 || ball.locY > 800){
            ball.setCenterX(ball.plocX);
            ball.setCenterY(ball.plocY);
            timer.stop();
            messages.setText("You lost");
            mode = 2;
        }

    }

}
