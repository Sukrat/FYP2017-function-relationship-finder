package functlyser;

import functlyser.model.Data;
import org.junit.Assert;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static List<Data> nextData(String filename, int num, int col) {
        List<Data> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Data data = new Data();
            data.setFileName(filename);
            data.setColumns(new HashMap<>());
            for (int j = 0; j < col; j++) {
                data.getColumns().put(Data.colName(j), i + Faker.nextDouble());
            }
            list.add(data);
        }
        return list;
    }

    public static List<Data> nextData(int num, int col) {
        List<Data> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Data data = new Data();
            data.setFileName("testcsv.csv");
            data.setColumns(new HashMap<>());
            for (int j = 0; j < col; j++) {
                data.getColumns().put(Data.colName(i), Faker.nextDouble());
            }
            list.add(data);
        }
        return list;
    }

}
