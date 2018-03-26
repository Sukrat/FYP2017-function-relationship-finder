package functlyser;

import functlyser.model.Data;
import org.junit.Assert;

import java.nio.charset.Charset;
import java.util.*;

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
            data.setRawColumns(new HashMap<>());
            data.setWorkColumns(new HashMap<>());
            for (int j = 0; j < col; j++) {
                data.getRawColumns().put(Data.colName(j), i + Faker.nextDouble());
                data.getWorkColumns().put(Data.colName(j), i + Faker.nextDouble());
            }
            list.add(data);
        }
        return list;
    }

    public static List<Data> nextData(int num, int col) {
        return nextData("testcsv.csv", num, col);
    }

//    public static List<Data> nextGridData(int num) {
//        List<GridData> list = new ArrayList<>();
//        for (int i = 0; i < num; i++) {
//            GridData gridData = new GridData();
//            gridData.setBoxIndex(Arrays.asList((long) i, (long) i + 1, (long) i + 2));
//            gridData.
//            data.setFileName("testcsv.csv");
//            data.setRawColumns(new HashMap<>());
//            for (int j = 0; j < col; j++) {
//                data.getRawColumns().put(Data.colName(i), Faker.nextDouble());
//            }
//            list.add(data);
//        }
//        return list;
//    }
}
