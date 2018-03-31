package webapp.controller;

public class EmptyProgress implements core.command.IProgress {
    @Override
    public void update(int done, int outOf) {

    }

    @Override
    public void update(String message) {

    }

    @Override
    public void update(String format, Object... args) {

    }

    @Override
    public void update(int done, int outOf, String message) {

    }

    @Override
    public void update(int done, int outOf, String format, Object... args) {

    }
}
