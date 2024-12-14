package projectulartangga;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class HumanPlayer extends Player {

    public HumanPlayer(ImageView piece) {
        super(piece);
    }

    @Override
    public void takeTurn(int diceRoll) {
        System.out.println("Human player Mendapatkan dadu: " + diceRoll);
    }
}
