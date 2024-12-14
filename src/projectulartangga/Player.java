package projectulartangga;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public abstract class Player {
    protected ImageView piece;
    protected int position = 1; // Posisi awal pemain

    public Player(ImageView piece) {
        this.piece = piece;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ImageView getPiece() {
        return piece;
    }

    // Method untuk memperbarui posisi pemain di papan
    public void updatePosition(GridPane board, int newPosition) {
        this.position = newPosition;
        int[] gridPos = toGridPosition(newPosition);
        GridPane.setRowIndex(piece, gridPos[0] - 1);
        GridPane.setColumnIndex(piece, gridPos[1] - 1);
    }

    // Konversi posisi indeks ke koordinat grid
    private int[] toGridPosition(int index) {
        int row = 10 - ((index - 1) / 10);
        int col = (index - 1) % 10 + 1;

        if (row % 2 == 1) {
            col = 11 - col;
        }

        return new int[]{row, col};
    }

    // Abstract method untuk menangani giliran pemain
    public abstract void takeTurn(int diceRoll);
}
