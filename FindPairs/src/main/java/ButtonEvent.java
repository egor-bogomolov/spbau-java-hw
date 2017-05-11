
public class ButtonEvent {

    /**
     * IS0 and IS1 represent that the card now is face up and number written on it.
     * CLOSED represents that the card is face down.
     * WIN represents that the game has finished.
     */
    public enum Event {
        IS0,
        IS1,
        CLOSED,
        WIN
    }
    private int x;
    private int y;
    private Event event;

    ButtonEvent(int x, int y, Event event) {
        this.x = x;
        this.y = y;
        this.event = event;
    }

    static ButtonEvent win() {
        return new ButtonEvent(-1, -1, Event.WIN);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Event getEvent() {
        return event;
    }
}
