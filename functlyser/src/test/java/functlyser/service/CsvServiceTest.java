package functlyser.service;

import functlyser.BaseTest;
import javafx.util.Pair;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class CsvServiceTest extends BaseTest {


    @Autowired
    private CsvService sut;


    @Test
    public void convert_withKnownHeader() {
        String csv = "69.53716376,43.85339759,27.0789345\n" +
                "28.60979912,64.06039564,33.7528938\n" +
                "22.25475914,86.61652591,57.5153819\n" +
                "48.64121873,86.71553793,31.9302133\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes());
        String[] headers = {"a", "b", "c"};
        CellProcessor[] processors = {new NotNull(new ParseDouble()),
                new NotNull(new ParseDouble()),
                new NotNull(new ParseDouble())};

        List<Map<String, Object>> result = sut.convert(inputStream, headers, processors,
                (row) -> row);

        assertThat(result.size(), is(4));
        assertTrue(result.stream().allMatch(m -> m.size() == 3));
    }

    @Test
    public void convert_withOutKnowingHeader() {
        String csv = "69.53716376,43.85339759,27.0789345\n" +
                "28.60979912,64.06039564,33.7528938\n" +
                "22.25475914,86.61652591,57.5153819\n" +
                "48.64121873,86.71553793,31.9302133\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes());

        List<Map<String, Object>> result = sut.convert(inputStream, false,
                (header) -> {
                    String[] headers = {"a", "b", "c"};
                    CellProcessor[] processors = {new NotNull(new ParseDouble()),
                            new NotNull(new ParseDouble()),
                            new NotNull(new ParseDouble())};
                    return new Pair<>(headers, processors);
                },
                (row) -> row
        );

        assertThat(result.size(), is(3));
        assertTrue(result.stream().allMatch(m -> m.size() == 3));
    }

    @Test
    public void convert_withOutKnowingHeaderAndAddingTheHeader() {
        String csv = "69.53716376,43.85339759,27.0789345\n" +
                "28.60979912,64.06039564,33.7528938\n" +
                "22.25475914,86.61652591,57.5153819\n" +
                "48.64121873,86.71553793,31.9302133\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes());

        List<Map<String, Object>> result = sut.convert(inputStream, true,
                (header) -> {
                    String[] headers = {"a", "b", "c"};
                    CellProcessor[] processors = {new NotNull(new ParseDouble()),
                            new NotNull(new ParseDouble()),
                            new NotNull(new ParseDouble())};
                    return new Pair<>(headers, processors);
                },
                (row) -> row
        );

        assertThat(result.size(), is(4));
        assertTrue(result.stream().allMatch(m -> m.size() == 3));
    }

    @Test(expected = ServiceException.class)
    public void convert_emptyStringShouldThrow() {
        String csv = "";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes());

        List<Map<String, Object>> result = sut.convert(inputStream, true,
                (header) -> {
                    String[] headers = {"a"};
                    CellProcessor[] processors = {new NotNull(new ParseDouble())};
                    return new Pair<>(headers, processors);
                },
                (row) -> row
        );

        assertThat(result.size(), is(4));
        assertTrue(result.stream().allMatch(m -> m.size() == 3));
    }

    @Test
    public void convert_Resource() throws Exception {
        List<String> from = Arrays.asList("abc", "cde");
        String[] header = new String[]{"1", "2"};
        CellProcessor[] cellProcessor = new CellProcessor[]{new NotNull(), new NotNull()};


        Resource result = sut.convert(from, false, header, cellProcessor, (elem) -> new HashMap<String, Object>() {{
            put(header[0], elem);
            put(header[1], elem);
        }});

        assertThat(result.contentLength(), greaterThan(0l));
    }

    @Test
    public void convert_Resource_whenEmpty() throws Exception {
        List<String> from = Arrays.asList();
        String[] header = new String[]{"1", "2"};
        CellProcessor[] cellProcessor = new CellProcessor[]{new NotNull(), new NotNull()};


        Resource result = sut.convert(from, false, header, cellProcessor, (elem) -> new HashMap<String, Object>() {{
            put(header[0], elem);
            put(header[1], elem);
        }});

        assertThat(result.contentLength(), is(0l));
    }
}
