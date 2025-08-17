import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Task {
    private final int id;
    private String title;
    private LocalDate dueDate;
    private Priority priority;
    private Status status;
    private Set<String> tags = new LinkedHashSet<>();

    public Task(int id, String title, LocalDate dueDate, Priority priority, Status status, Set<String> tags) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        if (tags != null) this.tags = new LinkedHashSet<>(tags);
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public LocalDate getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public Set<String> getTags() { return tags; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setStatus(Status status) { this.status = status; }
    public void setTags(Set<String> tags) { this.tags = new LinkedHashSet<>(tags); }

    public String pretty() {
        String due = (dueDate == null) ? "-" : dueDate.toString();
        String tagStr = tags.isEmpty() ? "-" : String.join(",", tags);
        String pr = UI.colorPriority(priority);
        String st = UI.colorStatus(status);
        return String.format("#%d %s %s  %s  due:%s  tags:%s",
                id, pr, st, title, due, tagStr);
    }

    @Override public boolean equals(Object o) { return (o instanceof Task t) && t.id == id; }
    @Override public int hashCode() { return Objects.hash(id); }
}
