import org.monome.pages.api.GroovyAPI
import org.monome.pages.configuration.PatternBank

class MIDIChannellerPage extends GroovyAPI {

    int baseMidiChannel = 0
    def notes = []

    void init() {
        log("MIDIChannellerPage starting up")
        for (int x = 0; x < sizeX(); x++) {
            patterns().ignore(x, sizeY() - 1)
            notes[x] = []
            for (int y = 0; y < sizeY(); y++) {
                notes[x][y] = 0
            }
        }
    }

    void stop() {
        log("MIDIChannellerPage shutting down")
        patterns().clearIgnore()
    }

    void press(int x, int y, int val) {
        if (val == 0) return
        if (y == sizeY() - 1) {
            if (x == sizeX() - 1) {
                return
            }
            patterns().handlePress(x);
            redraw()
        } else if (y == sizeY() - 2) {
            baseMidiChannel = x
            redraw()
        } else {
            int note = ((y * sizeY()) + x)
            int channel = baseMidiChannel + (note / 128)
            note = note % 128
            noteOut(note, 127, channel, 1)
            noteOut(note, 127, channel, 0)
        }
    }

    void redraw() {
        for (int x = 0; x < sizeX(); x++) {
            for (int y = 0; y < sizeY() - 1; y++) {
                led(x, y, notes[x][y])
            }
        }
        for (int x = 0; x < sizeX(); x++) {
            led(x, sizeY() - 2, x == baseMidiChannel ? 1 : 0)            
            led(x, sizeY() - 1, patterns().getPatternState(x) == PatternBank.PATTERN_STATE_EMPTY ? 0 : 1)
        }
    }

    void note(int num, int velo, int chan, int on) {
        num += (chan - baseMidiChannel) * 128
        if (num < 0 || num > 255) {
            return
        }
        int x = num % sizeX()
        int y = num / sizeY()
        if (y > sizeY() - 3) {
            return
        }
        led(x, y, on)
        notes[x][y] = on
    }
}