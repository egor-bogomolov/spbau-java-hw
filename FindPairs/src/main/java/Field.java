import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a field for "Find pairs" game. Saves state of the field.
 */
public class Field {
    private static final int SIZE = 4;

    /**
     * OPENED - card is face up
     * CLOSED - card is face down
     * INACTIVE - card isn't in game (it was matched)
     */
    private enum State {
        INACTIVE,
        OPENED,
        CLOSED
    }

    private State[][] fieldState;
    private int[][] field;
    private int pairsToFind = 0;
    private boolean sthOpened = false;
    private int xOpened, yOpened;

    /**
     * Creates a new field of fixed size. Numbers of cards are random, it's just guaranteed that the game
     * can be finished.
     */
    public Field() {
        field = new int[SIZE][SIZE];
        fieldState = new State[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                fieldState[i][j] = State.CLOSED;
            }
        }
        Random rand = new Random();
        int count = rand.nextInt(SIZE * SIZE / 2 + 1) * 2;
        while(count > 0) {
            int x, y;
            while(true) {
                x = rand.nextInt(SIZE);
                y = rand.nextInt(SIZE);
                if (field[x][y] == 0) {
                    field[x][y] = 1;
                    count--;
                    break;
                }
            }
        }

        pairsToFind = SIZE * SIZE / 2;
    }

    /**
     * Handles click on a card. If the card isn't closed - does nothing. Otherwise reacts according to rules of
     * the game.
     * @param x - x coordinate of card in range  [0..SIZE)
     * @param y - y coordinate of card in range  [0..SIZE)
     * @return - list of events that happened.
     */
    public List<ButtonEvent> processClick(int x, int y) {
        List<ButtonEvent> events = new ArrayList<>();
        if (fieldState[x][y] != State.CLOSED) {
            return events;
        }
        if (field[x][y] == 0) {
            events.add(new ButtonEvent(x, y, ButtonEvent.Event.IS0));
        } else {
            events.add(new ButtonEvent(x, y, ButtonEvent.Event.IS1));
        }
        if (sthOpened) {
            if (field[x][y] == field[xOpened][yOpened]) {
                pairsToFind--;
                sthOpened = false;
                fieldState[x][y] = State.INACTIVE;
                fieldState[xOpened][yOpened] = State.INACTIVE;
            } else {
                events.add(new ButtonEvent(xOpened, yOpened, ButtonEvent.Event.CLOSED));
                events.add(new ButtonEvent(x, y, ButtonEvent.Event.CLOSED));
                sthOpened = false;
                fieldState[x][y] = State.CLOSED;
                fieldState[xOpened][yOpened] = State.CLOSED;
            }
        } else {
            fieldState[x][y] = State.OPENED;
            xOpened = x;
            yOpened = y;
            sthOpened = true;
        }
        if (pairsToFind == 0) {
            events.add(ButtonEvent.win());
        }
        return events;
    }

    /**
     * @return - size of the field
     */
    public int getSize() {
        return SIZE;
    }
}
