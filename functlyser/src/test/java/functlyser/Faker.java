package functlyser;

import org.junit.Assert;

import java.nio.charset.Charset;
import java.util.Random;

public class Faker {

    public static Random random = new Random();

    public static int nextInt() {
        return random.nextInt();
    }

    public static int nextInt(int min, int max) {
        Assert.assertFalse("When calling random min must be less than max!", min > max);
        return min + random.nextInt(max - min + 1);
    }

    public static double nextDouble() {
        return random.nextDouble();
    }

    public static String nextString(int maxLength) {
        Assert.assertFalse("Max length of random string cannot be less than 1", maxLength <= 0);
        byte[] array = new byte[maxLength];
        random.nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
}
