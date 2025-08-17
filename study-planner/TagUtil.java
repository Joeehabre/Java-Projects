import java.util.LinkedHashSet;
import java.util.Set;

public class TagUtil {
    public static Set<String> parseTags(String commaSeparated) {
        Set<String> s = new LinkedHashSet<>();
        if (commaSeparated == null || commaSeparated.isEmpty()) return s;
        for (String part : commaSeparated.split(",")) {
            String p = part.trim();
            if (!p.isEmpty()) s.add(p);
        }
        return s;
    }

    public static Set<String> parsePipe(String pipeSeparated) {
        Set<String> s = new LinkedHashSet<>();
        if (pipeSeparated == null || pipeSeparated.isEmpty()) return s;
        for (String part : pipeSeparated.split("\\|")) {
            String p = part.trim();
            if (!p.isEmpty()) s.add(p);
        }
        return s;
    }
}
