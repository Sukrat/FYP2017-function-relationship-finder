package core.command;

public interface CommandProgess {

    void update(int done, int outOf);

    void update(String message);

    void update(String format, Object... args);

    void update(int done, int outOf, String message);

    void update(int done, int outOf, String format, Object... args);
}
