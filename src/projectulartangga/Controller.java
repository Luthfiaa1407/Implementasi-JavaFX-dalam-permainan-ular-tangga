package projectulartangga;

import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Controller {

    @FXML
    private ImageView dadu1;

    @FXML
    private ImageView player1;

    @FXML
    private ImageView player2;
    
    @FXML
    private Label playerTurn;

    @FXML
    private GridPane papan;

    private MediaPlayer mediaPlayer;

    private static final String[] DICE_IMAGES = {
            "dadu1.png",
            "dadu2.png",
            "dadu3.png",
            "dadu4.png",
            "dadu5.png",
            "dadu6.png"
    };

    private static final int[] TANGGA_DAN_ULAR = new int[101];

    private final Random random = new Random();

    private int player1Pos = 1;
    private int player2Pos = 1;
    private boolean player1Turn = true;
    
    private HumanPlayer player1Human; // Player 1 Human object
    private HumanPlayer player2Human; // Player 2 Human object
    @FXML
    private Button kocokDadu;
    @FXML
    private Button resetButton;

    public void initialize() {
        initTanggaDanUlar();
        player1Human = new HumanPlayer(player1); // Inisialisasi untuk player1
        player2Human = new HumanPlayer(player2); // Inisialisasi untuk player2
        updatePlayerPosition(player1, player1Pos);
        updatePlayerPosition(player2, player2Pos);
        
        updatePlayerTurnLabel();
    }

    private void initTanggaDanUlar() {
        for (int i = 0; i < TANGGA_DAN_ULAR.length; i++) {
            TANGGA_DAN_ULAR[i] = i;
        }

        // Tangga
        TANGGA_DAN_ULAR[2] = 38;
        TANGGA_DAN_ULAR[7] = 14;
        TANGGA_DAN_ULAR[8] = 31;
        TANGGA_DAN_ULAR[15] = 26;
        TANGGA_DAN_ULAR[21] = 42;
        TANGGA_DAN_ULAR[28] = 84;
        TANGGA_DAN_ULAR[36] = 44;
        TANGGA_DAN_ULAR[51] = 67;
        TANGGA_DAN_ULAR[71] = 91;
        TANGGA_DAN_ULAR[78] = 98;
        TANGGA_DAN_ULAR[87] = 94;

        // Ular
        TANGGA_DAN_ULAR[16] = 6;
        TANGGA_DAN_ULAR[46] = 25;
        TANGGA_DAN_ULAR[49] = 11;
        TANGGA_DAN_ULAR[62] = 19;
        TANGGA_DAN_ULAR[64] = 60;
        TANGGA_DAN_ULAR[74] = 53;
        TANGGA_DAN_ULAR[89] = 68;
        TANGGA_DAN_ULAR[92] = 88;
        TANGGA_DAN_ULAR[95] = 75;
        TANGGA_DAN_ULAR[99] = 80;
    }

    @FXML
    void rollDadu(ActionEvent event) {
        animateDice();
    }

    private void animateDice() {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), dadu1);
        rotateTransition.setByAngle(360);
        rotateTransition.setInterpolator(javafx.animation.Interpolator.LINEAR);

        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100_000_000) {
                    int randomIndex = random.nextInt(6);
                    try {
                        dadu1.setImage(new Image(getClass().getResourceAsStream(DICE_IMAGES[randomIndex])));
                    } catch (NullPointerException e) {
                        System.err.println("Error loading dice image: " + e.getMessage());
                    }
                    lastUpdate = now;
                }
            }
        };

        rotateTransition.setOnFinished(e -> {
            timer.stop();
            int diceRoll = random.nextInt(6) + 1;
            try {
                dadu1.setImage(new Image(getClass().getResourceAsStream(DICE_IMAGES[diceRoll - 1])));
            } catch (NullPointerException ex) {
                System.err.println("Error loading dice image: " + ex.getMessage());
            }
            if (player1Turn) {
                player1Human.takeTurn(diceRoll); // Panggil takeTurn untuk player 1
            } else {
                player2Human.takeTurn(diceRoll); // Panggil takeTurn untuk player 2
            }
            movePlayer(diceRoll);
            updatePlayerTurnLabel();
        });

        timer.start();
        rotateTransition.play();
    }

    private int[] toGridPosition(int index) {
        int row = 10 - ((index - 1) / 10);
        int col = (index - 1) % 10 + 1;

        if (row % 2 == 1) {
            col = 11 - col;
        }

        return new int[]{row, col};
    }

    private void updatePlayerPosition(ImageView player, int position) {
        if (player == null) return;
        int[] gridPos = toGridPosition(position);
        GridPane.setRowIndex(player, gridPos[0] - 1);
        GridPane.setColumnIndex(player, gridPos[1] - 1);
    }

    private void movePlayer(int diceRoll) {
        ImageView currentPlayer = player1Turn ? player1 : player2;
        if (currentPlayer == null) {
            System.err.println("Error: currentPlayer adalah null.");
            return;
        }

        int newPos = player1Turn ? player1Pos + diceRoll : player2Pos + diceRoll;

        if (newPos > 100) {
            player1Turn = !player1Turn;
            return;
        }

        int currentPosition = player1Turn ? player1Pos : player2Pos;

        SequentialTransition sequentialTransition = new SequentialTransition();

        while (currentPosition < newPos) {
            int targetPos = currentPosition + 1;
            int[] currentGridPos = toGridPosition(currentPosition);
            int[] nextGridPos = toGridPosition(targetPos);

            TranslateTransition stepTransition = new TranslateTransition(Duration.seconds(0.3), currentPlayer);
            double moveX = (nextGridPos[1] - currentGridPos[1]) * (papan.getPrefWidth() / 10);
            double moveY = (nextGridPos[0] - currentGridPos[0]) * (papan.getPrefHeight() / 10);

            stepTransition.setByX(moveX);
            stepTransition.setByY(moveY);
            stepTransition.setOnFinished(e -> {
                currentPlayer.setTranslateX(0);
                currentPlayer.setTranslateY(0);
                updatePlayerPosition(currentPlayer, targetPos);
            });

            sequentialTransition.getChildren().add(stepTransition);
            currentPosition++;
        }

        sequentialTransition.setOnFinished(e -> {
            if (player1Turn) {
                player1Pos = newPos;
                checkForSnakesAndLadders(player1, true);
                if (player1Pos == 100) showWinnerScreen("Player 1");
            } else {
                player2Pos = newPos;
                checkForSnakesAndLadders(player2, false);
                if (player2Pos == 100) showWinnerScreen("Player 2");
            }
            player1Turn = !player1Turn;
            updatePlayerTurnLabel();
        });

        sequentialTransition.play();
    }
    private void updatePlayerTurnLabel() {
        if (player1Turn) {
            playerTurn.setText("Giliran Player 1");
        } else {
            playerTurn.setText("Giliran Player 2");
        }
    }

    private void showWinnerScreen(String winner) {
        try {
            playWinMusic()
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Winner.fxml"));
            Parent root = loader.load();

            WinnerController winnerController = loader.getController();
            winnerController.setWinnerText(winner + " Wins!");

            Stage stage = (Stage) papan.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void checkForSnakesAndLadders(ImageView player, boolean isPlayer1) {
        int currentPosition = isPlayer1 ? player1Pos : player2Pos;
        int newPosition = TANGGA_DAN_ULAR[currentPosition];

        if (newPosition != currentPosition) {
            System.out.println("Pindah dari " + currentPosition + " ke " + newPosition);
            int[] currentGridPos = toGridPosition(currentPosition);
            int[] newGridPos = toGridPosition(newPosition);

            TranslateTransition transition = new TranslateTransition(Duration.seconds(1), player);
            double moveX = (newGridPos[1] - currentGridPos[1]) * (papan.getPrefWidth() / 10);
            double moveY = (newGridPos[0] - currentGridPos[0]) * (papan.getPrefHeight() / 10);

            transition.setByX(moveX);
            transition.setByY(moveY);
            transition.setOnFinished(e -> {
                player.setTranslateX(0);
                player.setTranslateY(0);
                updatePlayerPosition(player, newPosition);
                if (isPlayer1) {
                    player1Pos = newPosition;
                } else {
                    player2Pos = newPosition;
                }
            });
            transition.play();
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
    // Reset posisi pemain ke awal
    player1Pos = 1;
    player2Pos = 1;

    // Update posisi grafis pemain pada papan
    updatePlayerPosition(player1, player1Pos);
    updatePlayerPosition(player2, player2Pos);

    // Reset giliran pemain ke Player 1
    player1Turn = true;
    updatePlayerTurnLabel();

    }

    private void playWinMusic() {
    try {
        URL musicUrl = getClass().getResource("Suara_Kemenangan.mp3");
        if (musicUrl != null) {
            Media sound = new Media(musicUrl.toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } else {
            System.err.println("File musik tidak ditemukan.");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
