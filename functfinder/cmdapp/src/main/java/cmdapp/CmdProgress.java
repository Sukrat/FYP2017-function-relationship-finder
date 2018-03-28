package cmdapp;

import core.command.CommandProgess;


public class CmdProgress implements CommandProgess {

    private int done = 0;
    private int outOf = 0;
    private String message = "working on...";

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
        System.out.println(String.format("%d / %d : %s", this.done, this.outOf, this.message));
    }
}
