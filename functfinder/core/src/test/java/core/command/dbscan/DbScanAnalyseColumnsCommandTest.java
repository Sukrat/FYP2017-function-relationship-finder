package core.command.dbscan;

import core.Faker;
import core.command.CommandException;
import core.command.ICommandTest;
import core.model.CompiledRegression;
import core.model.Data;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class DbScanAnalyseColumnsCommandTest extends ICommandTest {

    private DbScanAnalyseColumnsCommand sut;

    @Test
    public void execute() {
        Double n1Radius = 2.0;
        Integer columnNo = 1;
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);
        sut = new DbScanAnalyseColumnsCommand(dataService, n1Radius, columnNo);

        Collection<CompiledRegression> result = execute(sut);

        assertThat(result.size(), is(greaterThan(0)));
    }

    @Test(expected = CommandException.class)
    public void execute_throwWhenIndex0Columns() {
        Double n1Radius = 2.0;
        Integer columnNo = 0;
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);
        sut = new DbScanAnalyseColumnsCommand(dataService, n1Radius, columnNo);

        Collection<CompiledRegression> result = execute(sut);
    }

    @Test
    public void execute_shouldWorkWhenIndexMinus1Columns() {
        Double n1Radius = 2.0;
        Integer columnNo = -1;
        List<Data> perfectDataFor = Faker.nextData("test.csv", 30, 5);
        operations.insert(perfectDataFor, collectionName);
        sut = new DbScanAnalyseColumnsCommand(dataService, n1Radius, columnNo);

        Collection<CompiledRegression> result = execute(sut);

        assertThat(result.size(), is(greaterThan(0)));
    }
}