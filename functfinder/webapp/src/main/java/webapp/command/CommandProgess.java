package webapp.command;

public interface CommandProgess {

    void setTotalWork(int totalWork);

    void setTotalWork(int totalWork, String message);

    void setTotalWork(int totalWork, String format, Object... args);

    void update(int done);

    void update(String message);

    void update(String format, Object... args);

    void update(int done, String message);

    void update(int done, String format, Object... args);
}
