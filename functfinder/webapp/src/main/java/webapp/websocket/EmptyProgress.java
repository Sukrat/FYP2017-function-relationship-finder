package webapp.websocket;

import core.command.IProgress;

public class EmptyProgress implements IProgress {
    @Override
    public void setWork(int outOf, String message) {

    }

    @Override
    public void setWork(int outOf, String message, Object... args) {

    }

    @Override
    public void increment() {

    }

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
