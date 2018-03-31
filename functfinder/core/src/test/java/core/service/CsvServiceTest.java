package core.service;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class CsvServiceTest {

    private ICsvService sut;

    @Before
    public void before() {
        sut = new CsvService();
    }


    @Test
    public void parse_withKnownHeader() {
        String csv = "69.53716376,43.85339759,27.0789345\n" +
                "28.60979912,64.06039564,33.7528938\n" +
                "22.25475914,86.61652591,57.5153819\n" +
                "48.64121873,86.71553793,31.9302133\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes());
        String[] headers = {"a", "b", "c"};
        CellProcessor[] processors = {new NotNull(new ParseDouble()),
                new NotNull(new ParseDouble()),
                new NotNull(new ParseDouble())};

        List<Map<String, Object>> result = sut.parse(inputStream, headers, processors,
                (row) -> row);

        assertThat(result.size(), is(4));
        result.stream()
                .forEach(m -> {
                    assertThat(m.size(), is(3));
                    assertThat(m.keySet(), containsInAnyOrder("a", "b", "c"));
                });
    }

    @Test
    public void parse_withOutKnowingHeader() {
        String csv = "a,b,c\n" +
                "28.60979912,64.06039564,33.7528938\n" +
                "22.25475914,86.61652591,57.5153819\n" +
                "48.64121873,86.71553793,31.9302133\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes());

        List<Map<String, Object>> result = sut.parse(inputStream, true,
                (header) -> {
                    CellProcessor[] processors = {new NotNull(new ParseDouble()),
                            new NotNull(new ParseDouble()),
                            new NotNull(new ParseDouble())};
                    return new Pair<>(header, processors);
                },
                (row) -> row
        );

        assertThat(result.size(), is(3));
        result.stream()
                .forEach(m -> {
                    assertThat(m.size(), is(3));
                    assertThat(m.keySet(), containsInAnyOrder("a", "b", "c"));
                });
    }

    @Test
    public void parse_withOutKnowingHeaderAndAddingTheHeader() {
        String csv = "69.53716376,43.85339759,27.0789345\n" +
                "28.60979912,64.06039564,33.7528938\n" +
                "22.25475914,86.61652591,57.5153819\n" +
                "48.64121873,86.71553793,31.9302133\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes());

        List<Map<String, Object>> result = sut.parse(inputStream, false,
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
        result.stream()
                .forEach(m -> {
                    assertThat(m.size(), is(3));
                    assertThat(m.keySet(), containsInAnyOrder("a", "b", "c"));
                });
    }

    @Test(expected = ServiceException.class)
    public void parse_noDataShouldThrow() {
        String csv = "";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes());

        List<Map<String, Object>> result = sut.parse(inputStream, false,
                (header) -> {
                    String[] headers = {"a"};
                    CellProcessor[] processors = {new NotNull(new ParseDouble())};
                    return new Pair<>(headers, processors);
                },
                (row) -> row
        );
    }

    @Test
    public void parse_noRecordShouldReturnEmpty() {
        String csv = "a,b,c";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(csv.getBytes());

        List<Map<String, Object>> result = sut.parse(inputStream, true,
                (header) -> {
                    String[] headers = {"a"};
                    CellProcessor[] processors = {new NotNull(new ParseDouble())};
                    return new Pair<>(headers, processors);
                },
                (row) -> row
        );
        assertThat(result.size(), is(0));
    }

    @Test
    public void toCsv() {
        List<String> from = Arrays.asList("abc", "cde");
        String[] header = new String[]{"1", "2"};
        CellProcessor[] cellProcessor = new CellProcessor[]{new NotNull(), new NotNull()};


        ByteArrayOutputStream result = sut.toCsv(from, false, header, cellProcessor, (elem) -> new HashMap<String, Object>() {{
            put(header[0], elem);
            put(header[1], elem);
        }});

        assertThat(result.size(), greaterThan(0));
    }

    @Test
    public void toCsv_whenEmptyList() {
        List<String> from = Arrays.asList();
        String[] header = new String[]{"" +
                "1", "2"};
        CellProcessor[] cellProcessor = new CellProcessor[]{new NotNull(), new NotNull()};


        ByteArrayOutputStream result = sut.toCsv(from, false, header, cellProcessor, (elem) -> new HashMap<String, Object>() {{
            put(header[0], elem);
            put(header[1], elem);
        }});

        assertThat(result.size(), is(0));
    }
}