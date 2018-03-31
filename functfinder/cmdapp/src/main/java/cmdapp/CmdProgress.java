package cmdapp;

import core.command.CommandProgess;
import core.command.IProgress;


public class CmdProgress implements IProgress {

    private int done = 0;
    private int outOf = 0;
    private String message = "working on...";

    public CmdProgress() {
        System.out.println();
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

    private void send(int done, int outOf, String message) {
        this.done = done;
        this.outOf = outOf;
        this.message = message;
        System.out.print(String.format("%d / %d : %s\r", this.done, this.outOf, this.message));
    }
}
