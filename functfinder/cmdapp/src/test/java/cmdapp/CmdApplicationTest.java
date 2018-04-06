package cmdapp;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class CmdApplicationTest {


    @Test
    public void main_withWrongDataBase() {
        run("");
    }

    @Test
    public void main_withHelp() {
        run("--help");
        run("-?");
    }

    private void run(String... args) {
        CmdApplication.main(args);
    }
}