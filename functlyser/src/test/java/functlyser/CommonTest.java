package functlyser;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommonTest {

    @Test
    public void test_isNullOrEmpty_shouldReturnTrue_whenStringIsNull() {
        String str = null;

        boolean result = Common.isNullOrEmpty(str);

        assertTrue(result);
    }

    @Test
    public void test_isNullOrEmpty_shouldReturnTrue_whenStringIsEmpty() {
        String str = "";

        boolean result = Common.isNullOrEmpty(str);

        assertTrue(result);
    }

    @Test
    public void test_isNullOrEmpty_shouldReturnTrue_whenStringIsWhiteSpace() {
        String str = "  ";

        boolean result = Common.isNullOrEmpty(str);

        assertTrue(result);
    }
}
