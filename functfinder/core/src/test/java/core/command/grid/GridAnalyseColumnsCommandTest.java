package core.command.grid;

import core.Faker;
import core.command.CommandException;
import core.command.ICommandTest;
import core.model.CompiledRegression;
import core.model.Data;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class GridAnalyseColumnsCommandTest extends ICommandTest {

    private GridAnalyseColumnsCommand sut;

    @Test
    public void execute() {
        List<Double> n1Tolerances = Arrays.asList(2.0);
        Integer columnNo = 1;
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);
        sut = new GridAnalyseColumnsCommand(dataService,
                n1Tolerances,
                columnNo);

        Collection<CompiledRegression> result = execute(sut);

        assertThat(result.size(), is(greaterThan(0)));
    }

    @Test
    public void execute_WhenIndexMinus1Columns() {
        List<Double> n1Tolerances = Arrays.asList(2.0);
        Integer columnNo = -1;
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);
        sut = new GridAnalyseColumnsCommand(dataService,
                n1Tolerances,
                columnNo);

        Collection<CompiledRegression> result = execute(sut);

        assertThat(result.size(), is(greaterThan(0)));
    }


    @Test(expected = CommandException.class)
    public void throwWhenIndexMoreThanColumns() {
        List<Double> n1Tolerances = Arrays.asList(2.0);
        Integer columnNo = 6;
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);
        sut = new GridAnalyseColumnsCommand(dataService,
                n1Tolerances,
                columnNo);

        Collection<CompiledRegression> result = execute(sut);
    }

    @Test(expected = CommandException.class)
    public void throwWhenIndexLessThanColumns() {
        List<Double> n1Tolerances = Arrays.asList(2.0);
        Integer columnNo = -5;
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);
        sut = new GridAnalyseColumnsCommand(dataService,
                n1Tolerances,
                columnNo);

        Collection<CompiledRegression> result = execute(sut);
    }

    @Test(expected = CommandException.class)
    public void throwWhenIndex0Columns() {
        List<Double> n1Tolerances = Arrays.asList(2.0);
        Integer columnNo = 0;
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);
        sut = new GridAnalyseColumnsCommand(dataService,
                n1Tolerances,
                columnNo);

        Collection<CompiledRegression> result = execute(sut);
    }
}