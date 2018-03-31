package cmdapp;

import core.command.IProgress;

public class CmdProgress implements IProgress {

    private boolean called = false;
    private int done = 0;
    private int outOf = 0;
    private String message = "working on...";

    @Override
    public void setWork(int outOf, String message) {
        send(done, outOf, message);
    }

    @Override
    public void setWork(int outOf, String message, Object... args) {
        send(done, outOf, String.format(message, args));
    }

    @Override
    public void increment() {
        send(++done, outOf, message);
    }

    @Override
    public void update(int done, int outOf) {
        send(done, outOf, message);
    }

    @Override
    public void update(String message) {
        send(done, outOf, message);
    }

    @Override
    public void update(String format, Object... args) {
        send(done, outOf, String.format(format, args));
    }

    @Override
    public void update(int done, int outOf, String message) {
        send(done, outOf, message);
    }

    @Override
    public void update(int done, int outOf, String format, Object... args) {
        send(done, outOf, String.format(format, args));
    }

    private synchronized void send(int done, int outOf, String message) {
        this.done = done;
        this.outOf = outOf;
        int previousLength = this.message != null ? this.message.length() : 0;
        this.message = message;
        if (!called) {
            called = true;
            System.out.println();
        }
        System.out.print(String.format("[%d / %d]: %s", this.done, this.outOf, this.message));
        for (int i = 0; i < previousLength - message.length(); i++) {
            System.out.print(" ");
        }
        System.out.print("\r");
        System.out.flush();
    }
}
