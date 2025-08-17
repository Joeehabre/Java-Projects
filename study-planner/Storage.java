import java.io.*;
import java.time.LocalDate;
import java.util.Set;

public class Storage {
    // CSV: id,title,dueDate,priority,status,tags (tags like tag1|tag2)
    public static void save(String path, Planner planner) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("id,title,dueDate,priority,status,tags");
            for (Task t : planner.allSorted()) {
                String due = t.getDueDate()==null ? "" : t.getDueDate().toString();
                String tags = t.getTags().isEmpty()? "" : String.join("|", t.getTags());
                pw.printf("%d,%s,%s,%s,%s,%s%n",
                        t.getId(), escape(t.getTitle()), due, t.getPriority(), t.getStatus(), escape(tags));
            }
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    public static void load(String path, Planner planner) {
        File f = new File(path);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = Csv.split(line, 6);
                if (parts.length < 6) continue;
                int id = Integer.parseInt(parts[0]);
                String title = Csv.unescape(parts[1]);
                LocalDate due = parts[2].isEmpty()? null : LocalDate.parse(parts[2]);
                Priority pr = Priority.valueOf(parts[3]);
                Status st = Status.valueOf(parts[4]);
                Set<String> tags = TagUtil.parsePipe(Csv.unescape(parts[5]));
                planner.addRestored(new Task(id, title, due, pr, st, tags));
            }
        } catch (Exception e) {
            System.out.println("Load failed: " + e.getMessage());
        }
    }

    public static void exportCsv(String path, Planner planner) { save(path, planner); }

    public static void importCsv(String path, Planner planner) {

        File f = new File(path);
        if (!f.exists()) { System.out.println("No " + path + " found."); return; }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] p = Csv.split(line, 6);
                if (p.length < 6) continue;
                String title = Csv.unescape(p[1]);
                LocalDate due = p[2].isEmpty()? null : LocalDate.parse(p[2]);
                Priority pr = Priority.valueOf(p[3]);
                Status st = Status.valueOf(p[4]);
                Set<String> tags = TagUtil.parsePipe(Csv.unescape(p[5]));
                planner.add(title, due, pr, tags);
            }
        } catch (Exception e) {
            System.out.println("Import failed: " + e.getMessage());
        }
    }

    private static String escape(String s) { return Csv.escape(s); }
}
