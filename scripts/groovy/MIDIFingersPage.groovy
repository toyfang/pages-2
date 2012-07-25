import org.monome.pages.api.GroovyAPI
import org.monome.pages.configuration.PatternBank

class MIDIFingersPage extends GroovyAPI {

    void init() {
        log("MIDIFingersPage starting up")
        for (int x = 0; x < sizeX(); x++) {
            patterns().ignore(x, sizeY() - 1)
        }
    }

    void stop() {
        log("MIDIFingersPage shutting down")
        patterns().clearIgnore()
    }

    void press(int x, int y, int val) {
        if (val == 0) return
        if (y == sizeY() - 1) {
            if (x < sizeX() / 2) {
                monome().switchPage(x)
            } else if (x == sizeX() - 1) {
                return
            } else {
                int patternNum = x - sizeX() / 2
                patterns().handlePress(patternNum);
                redraw()
            }
        } else {
            int note = ((x * sizeX()) + y) % 128
            int channel = note / 128
            noteOut(note, 127, channel, 1)
        }
    }

    void redraw() {
        clear(0)
        for (int patternNum = 0; patternNum < sizeX() / 2; patternNum++) {
            if (patterns().getPatternState(patternNum) != PatternBank.PATTERN_STATE_EMPTY) {
                int x = patternNum + sizeX() / 2
                led(x, sizeY() - 1, 1)
            }
        }
    }

    void note(int num, int velo, int chan, int on) {
        num += chan * 128
        int x = num / sizeX()
        int y = num % sizeY()
        led(x, y, on)
    }
}