import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


public class FieldTest {

    private boolean hasEnded(List<ButtonEvent> events) {
        for (ButtonEvent event : events) {
            if (event.getEvent() == ButtonEvent.Event.WIN) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void gameEnds() {
        boolean ended = false;
        Field field = new Field();
        int size = field.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    for (int l = 0; l < size; l++) {
                        ended |= hasEnded(field.processClick(i, j));
                        ended |= hasEnded(field.processClick(k, l));
                    }
                }
            }
        }
        assertTrue(ended);
    }

    @Test
    public void multipleClicks() {
        Field field = new Field();
        assertFalse(field.processClick(0, 0).isEmpty());
        assertTrue(field.processClick(0, 0).isEmpty());
        assertTrue(field.processClick(0, 0).isEmpty());
    }
}