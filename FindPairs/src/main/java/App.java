import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import org.reactfx.util.FxTimer;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Game "Find pairs". You're given a field of cards with 0 or 1 on each of them, face down. On each turn you can
 * turn a pair of cards, see numbers on them and if the numbers are equal they stay turned. Otherwise they're
 * turned back and you choose the next pair.
 * The game ends when you've turned all the pairs (it is guaranteed that there is even number of cards with 1s
 * and 0s).
 */
public class App extends Application {

    private GridPane grid = new GridPane();
    private Field field = new Field();
    private static final Duration waitingTime =  Duration.ofSeconds(1);
    private Button[][] buttons;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method creates new game (field, cards etc).
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        int size = field.getSize();
        buttons = new Button[size][size];

        for (int row = 0 ; row < size ; row++ ){
            RowConstraints rc = new RowConstraints();
            rc.setFillHeight(true);
            rc.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rc);
        }
        for (int col = 0 ; col < size; col++ ) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setFillWidth(true);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }

        for (int i = 0 ; i < size ; i++) {
            for (int j = 0; j < size; j++) {
                Button button = createButton(i, j);
                grid.add(button, i, j);
            }
        }

        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * This method creates a button representing single cards at given coordinates.
     * @param x - x coordinate of the button
     * @param y - y coordinate of the button
     * @return - new instance of Button
     */
    private Button createButton(int x, int y) {
        buttons[x][y] = new Button();
        buttons[x][y].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttons[x][y].setOnAction(e -> {
            processEvents(field.processClick(x, y));
        });
        return buttons[x][y];
    }

    /**
     * This method processes events that happened to cards.
     * @param events - list of events to process.
     */
    private void processEvents(List<ButtonEvent> events) {
        for (ButtonEvent event : events) {
            int x = event.getX();
            int y = event.getY();
            switch (event.getEvent()) {
                case CLOSED:
                    FxTimer.runLater(waitingTime, () -> buttons[x][y].setText(""));
                    break;
                case IS0:
                    buttons[x][y].setText("0");
                    break;
                case IS1:
                    buttons[x][y].setText("1");
                    break;
                case WIN:
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Great victory!");
                    alert.setHeaderText("Congratulations!");
                    alert.setContentText("Do you want to play one more game?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK){
                        restart();
                    }
                    break;
            }
        }
    }

    /**
     * This method clears the buttons and created a field for a new game.
     */
    private void restart() {
        field = new Field();
        int size = field.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j].setText("");
            }
        }
    }
}
