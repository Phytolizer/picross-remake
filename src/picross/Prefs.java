package picross;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Prefs {
    private static final Map<String, String> defaultPrefs = new HashMap<>();
    private static Map<String, String> prefs = new HashMap<>();

    public static Map<String, String> read() {
        initDefaultPrefs();
        File prefsFile = new File("prefs.txt");
        if (!prefsFile.exists()) {
            return defaultPrefs;
        } else {
            Scanner prefsScanner = new Scanner("prefs.txt");
            while (prefsScanner.hasNext()) {
                String line = prefsScanner.nextLine();
                if (line.length() == 0 || !line.contains(":")) {
                    continue;
                }
                String key = line.substring(0, line.indexOf(':'));
                String value = line.substring(line.indexOf(':') + 1);
                prefs.put(key, value);
            }
        }
        return prefs;
    }

    private static void initDefaultPrefs() {
        defaultPrefs.put("puzzle_size_x", "10");
        defaultPrefs.put("puzzle_size_y", "10");
        prefs = defaultPrefs;
    }

    private static void writePrefs() {
        try {
            FileWriter outFile = new FileWriter("prefs.txt");
            for (Object o : prefs.entrySet()) {
                HashMap.Entry pair = (HashMap.Entry) o;
                outFile.write(pair.getKey() + ":" + pair.getValue());
            }
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
