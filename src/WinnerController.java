package projectulartangga;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.animation.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.Random;

public class WinnerController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label winnerLabel;

    private Timeline fireworkTimeline; // Timeline untuk animasi berulang

    public void setWinnerText(String text) {
        winnerLabel.setText(text);
        playFireworkAnimation();
    }

    private void playFireworkAnimation() {
        Random random = new Random();

        // Hentikan timeline jika sudah ada yang sedang berjalan
        if (fireworkTimeline != null) {
            fireworkTimeline.stop();
        }

        fireworkTimeline = new Timeline(
            new KeyFrame(Duration.seconds(0.1), event -> {  // Mengatur interval lebih cepat
                for (int i = 0; i < 10; i++) {  // Mengurangi jumlah partikel (lingkaran)
                    // Warna lebih cerah
                    Circle particle = new Circle(4, Color.color(random.nextDouble() * 0.5 + 0.5, random.nextDouble() * 0.5 + 0.5, random.nextDouble() * 0.5 + 0.5));  // Warna lebih cerah
                    
                    // Menampilkan partikel secara acak di seluruh layar fullscreen
                    particle.setLayoutX(random.nextDouble() * rootPane.getPrefWidth());
                    particle.setLayoutY(random.nextDouble() * rootPane.getPrefHeight());

                    rootPane.getChildren().add(particle);

                    // Gerakan lebih lambat
                    TranslateTransition tt = new TranslateTransition(Duration.seconds(4), particle);  // Memperpanjang durasi gerakan
                    tt.setByX(random.nextDouble() * 600 - 300);  // Range gerakan horizontal
                    tt.setByY(random.nextDouble() * 600 - 300);  // Range gerakan vertikal
                    tt.setAutoReverse(true);
                    tt.setCycleCount(2);

                    // Efek memudar lebih lambat
                    FadeTransition ft = new FadeTransition(Duration.seconds(4), particle);  // Memperpanjang durasi fade
                    ft.setToValue(0);

                    ParallelTransition pt = new ParallelTransition(tt, ft);
                    pt.setOnFinished(event1 -> rootPane.getChildren().remove(particle));  // Menghapus partikel setelah animasi selesai
                    pt.play();
                }
            })
        );

        fireworkTimeline.setCycleCount(Timeline.INDEFINITE); // Animasi berulang terus-menerus
        fireworkTimeline.play(); // Mulai animasi
    }
}
