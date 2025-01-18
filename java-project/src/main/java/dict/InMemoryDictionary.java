package dict;

import java.io.*;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Map;

/**
 * Implements a persistent dictionary that can be held entirely in memory.
 * When flushed, it writes the entire dictionary back to a file.
 * <p>
 * The file format has one keyword per line:
 * <pre>word:def</pre>
 * <p>
 * Note that an empty definition list is allowed (in which case the entry would have the form: <pre>word:</pre>
 *
 * @author talm
 */
public class InMemoryDictionary extends TreeMap<String, String> implements PersistentDictionary {
    private static final long serialVersionUID = 1L; // (because we're extending a serializable class)
    private final File dictFile;

    public InMemoryDictionary(File dictFile) {
        // Throw exception if the file is null
        if (dictFile == null) {
            throw new IllegalArgumentException("Dictionary file cannot be null.");
        }
        this.dictFile = dictFile;
    }

    @Override
    public void open() throws IOException {
        // Clear the current dictionary content
        this.clear();
        // Check if the file exists; if not, no action is needed
        if (!this.dictFile.exists()){
            return;
        }
        // Read the file line by line
        try (BufferedReader reader = new BufferedReader(new FileReader(dictFile))) {
            String line;
            while ((line = reader.readLine()) != null){
                // Split the line into key (word) and value (definition)
                String[] parts = line.split(":", 2);
                String word = parts[0];
                String def = parts[1];
                this.put(word, def);
            }
        }

    }

    @Override
    public void close() throws IOException {
        // Write to the file line by line
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dictFile, false))) {
            String line;
            for (Map.Entry<String, String> entry : this.entrySet()){
                // Combine the key (word) and value (definition) to a line with a : separator
                line = entry.getKey() + ":" + entry.getValue();
                writer.write(line);
                writer.newLine();
            }
        }

    }
}
