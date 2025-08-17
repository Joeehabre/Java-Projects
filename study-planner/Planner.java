import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Planner {
    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    public Task add(String title, LocalDate dueDate, Priority priority, Set<String> tags) {
        Task t = new Task(nextId++, title, dueDate, priority == null ? Priority.MEDIUM : priority, Status.TODO, tags);
        tasks.add(t);
        return t;
    }

    public Task find(int id) {
        for (Task t : tasks) if (t.getId() == id) return t;
        return null;
    }

    public boolean delete(int id) { return tasks.removeIf(t -> t.getId() == id); }

    public List<Task> filtered(Status st, Priority pr, String tag, LocalDate dueBefore, String sort) {
        return sorted(sort, tasks.stream()
            .filter(t -> st == null || t.getStatus() == st)
            .filter(t -> pr == null || t.getPriority() == pr)
            .filter(t -> tag == null || t.getTags().contains(tag))
            .filter(t -> dueBefore == null || (t.getDueDate() != null && !t.getDueDate().isAfter(dueBefore)))
            .collect(Collectors.toList()));
    }

    private List<Task> sorted(String sort, List<Task> list) {
        Comparator<Task> cmp = switch (sort) {
            case "priority" -> Comparator.comparing(Task::getPriority);
            case "status" -> Comparator.comparing(Task::getStatus);
            case "id" -> Comparator.comparing(Task::getId);
            default -> Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()));
        };
      
        cmp = cmp.thenComparing((Task t) -> t.getPriority().ordinal(), Comparator.reverseOrder())
                 .thenComparing(Task::getId);
        return list.stream().sorted(cmp).collect(Collectors.toList());
    }

    public List<Task> allSorted() { return filtered(null, null, null, null, "due"); }
  
    public long count(Status s) { return tasks.stream().filter(t -> t.getStatus()==s).count(); }
    public long count(Priority p) { return tasks.stream().filter(t -> t.getPriority()==p).count(); }
    public long overdue() {
        LocalDate now = LocalDate.now();
        return tasks.stream().filter(t -> t.getDueDate()!=null && t.getDueDate().isBefore(now) && t.getStatus()!=Status.DONE).count();
    }

    void addRestored(Task t) {
        tasks.add(t);
        nextId = Math.max(nextId, t.getId()+1);
    }
}
