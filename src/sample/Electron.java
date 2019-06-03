package sample;

import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
/**
 * Definiuje właściwości fizyczne oraz graficzne elektronu poprzez roszerzenie klasy Circle.
 */
class Electron extends Circle {
    /**Prędkość krążka względem osi X */
    double veloX = 0;
    /**Prędkość krążka względem osi Y */
    double veloY = 0;
    /**Przyśpieszenie krążka względem osi X */
    double accX = 0;
    /**Przyśpieszenie krążka względem osi Y */
    double accY = 0;
    /** Poczatkowa lokalizacja elektronu - parametr X*/
    double locX = 100;
    /** Poczatkowa lokalizacja elektronu - parametr Y*/
    double locY = 400;
    /** Lokalizacja elektronu w poprzedniej iteracji - parametr X */
    double plocX = 100;
    /** Lokalizacja elektronu w poprzedniej iteracji - parametr Y */
    double plocY = 400;

    /** Określa znak krążka (elektronu) true = +, false = - */
    boolean sign = true; //true +, false -
    /** Definiuje masę krążka (elektronu)*/
    int mass = 25;
    /** Definiuje maksymalną prędkość krążka */
    double vMax=19*40000;

    /**
     * Ustawienie elektronu na odpowiedniej pozycji
     * @param radius promień elektronu
     */
    Electron (int radius){
        super(radius);
        setCenterX(locX);
        setCenterY(locY);
    }

    /**
     *
     */
    private void changeLoc(){
        double cX=veloX;
        double cY=veloY;
        plocX=locX;
        plocY=locY;
        if(veloX>vMax) cX = vMax;
        if(veloY>vMax) cY = vMax;
        locX += cX/40000.0;   //dzielnik 2500x mniejszy niż stała k z klasy main
        locY += cY/40000.0;
    }

    /**
     *
     */
    void change(){
        veloX += accX;
        veloY += accY;
        changeLoc();
        accX = 0;
        accY = 0;
    }

    /**
     * Resetuje całą planszę - usuwa wszystkie rozmieszczone wcześniej
     * ładunki i umieszcza elektron na pozycji startowej.
     */
    void reset(){
        veloX = 0;
        veloY = 0;
        accX = 0;
        accY = 0;
        locX = 100;
        locY = 400;
        plocX = 100;
        plocY = 400;
    }

    /**
     * Funkcja generująca odbicie od przeszkody.
     */
    void bounce(){
        if (plocX < 1100 || plocX >1150) {
            veloX = veloX * (-1);
        }
        else veloY = veloY*(-1);
    }

    /**
     * Obsługuje zmianę znaku krążka.
     * true = znak dodatni +
     * false = znak ujemny -
     */
    void change_sign(){
        if (sign) sign = false;
        else sign = true;
    }

    void pounce(){
        veloX = veloX * (-1);
    }
}

/**
 * Definiuje właściwości fizyczne oraz graficzne źródła pola elektrycznego poprzez roszerzenie klasy Circle.
 */
class Source extends Circle {
    /** Określa znak ładunku elektrycznego true = +, false = -*/
    boolean sign;

    /**
     * Definiuje promień, położenie oraz kolor ładunku w zależności od znaku.
     * Czerwony kolor - ładunek dodatni +
     * Niebieski kolor - ładunek ujemny -
     * @param x położenie ładunku zmienna X
     * @param y położenie ładunku zmienna Y
     * @param r promień
     * @param s znak ładunku elektrycznego
     */
    Source(double x, double y, int r, boolean s){
        super(x,y,r);
        Color color = Color.RED;
        if(!s) color = Color.BLUE;
        setFill(color);
        sign = s;
    }
}
