package hu.boga.midiai.gui.test;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.ObservableList;

public class Main extends Application {
    @Override//from www.  j av  a 2  s .c om
    public void start(Stage primaryStage) {
        final double WINDOW = 400.0;

        Pane graphPane = new Pane();
        drawArrow(0, WINDOW / 5 * 4, WINDOW, WINDOW / 5 * 4, graphPane);
        drawArrow(WINDOW / 2, WINDOW, WINDOW / 2, 0, graphPane);

        Polyline sinLine = new Polyline();
        sinLine.setStroke(Color.RED);
        ObservableList<Double> list = sinLine.getPoints();
        double scaleFactor = 50;
        for (int x = -170; x <= 170; x++) {
            list.add(x + 200.0);
            list.add((WINDOW / 5 * 4) - scaleFactor * Math.sin((x / 100.0) * 2 * Math.PI));
        }

        graphPane.getChildren().addAll(sinLine);

        StackPane pane = new StackPane();
        pane.setPadding(new Insets(20));
        pane.setAlignment(Pos.CENTER);
        pane.getChildren().add(graphPane);

        Scene scene = new Scene(pane);
        primaryStage.setTitle("java2s.com");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void drawArrow(double startX, double startY,
                                 double endX, double endY, Pane pane) {
        double slope = (startY - endY) / (startX - endX);
        double lineAngle = Math.atan(slope);
        double arrowAngle = endX <= startX ? Math.toRadians(45) : -Math.toRadians(225);

        Line line = new Line(startX, startY, endX, endY);

        double lineLength = Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
        double arrowLength = lineLength / 12;
        Line arrow1 = new Line();
        arrow1.setStartX(line.getEndX());
        arrow1.setStartY(line.getEndY());
        arrow1.setEndX(line.getEndX() + arrowLength * Math.cos(lineAngle + arrowAngle));
        arrow1.setEndY(line.getEndY() + arrowLength * Math.sin(lineAngle + arrowAngle));
        Line arrow2 = new Line();
        arrow2.setStartX(line.getEndX());
        arrow2.setStartY(line.getEndY());
        arrow2.setEndX(line.getEndX() + arrowLength * Math.cos(lineAngle - arrowAngle));
        arrow2.setEndY(line.getEndY() + arrowLength * Math.sin(lineAngle - arrowAngle));

        pane.getChildren().addAll(line, arrow1, arrow2);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
