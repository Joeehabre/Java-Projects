public class Csv {
    public static String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    public static String unescape(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length()-1).replace("\"\"", "\"");
        }
        return s;
    }

    // Minimal CSV split with quotes
    public static String[] split(String line, int expected) {
        String[] out = new String[expected];
        int idx = 0;
        boolean inQ = false;
        StringBuilder cur = new StringBuilder();
        for (int i=0;i<line.length();i++) {
            char c = line.charAt(i);
            if (c=='"') { inQ = !inQ; continue; }
            if (c==',' && !inQ) { out[idx++] = cur.toString(); cur.setLength(0); }
            else cur.append(c);
        }
        out[idx] = cur.toString();
        return out;
    }
}
