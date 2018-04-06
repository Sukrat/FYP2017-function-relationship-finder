package core.command.data;

import core.Faker;
import core.command.CommandException;
import core.command.ICommandTest;
import core.model.Data;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


public class DataInsertCommandTest extends ICommandTest {

    public DataInsertCommand sut;

    @Test
    public void execute() {
        List<Data> datas = Faker.nextData("test.csv", 10, 3);
        sut = new DataInsertCommand(dataService, datas);

        Long execute = execute(sut);

        assertThat(execute, is(10L));
    }

    @Test
    public void execute_whenListIsEmpty() {
        List<Data> datas = null;
        sut = new DataInsertCommand(dataService, datas);

        Long execute = execute(sut);

        assertThat(execute, is(0L));
    }

    @Test(expected = CommandException.class)
    public void execute_whenColumnsDontMatch() {
        operations.insert(Faker.nextData("testcsv.csv", 4, 5),
                collectionName);
        List<Data> datas = Faker.nextData("test.csv", 10, 3);
        sut = new DataInsertCommand(dataService, datas);

        Long execute = execute(sut);
    }

    @Test(expected = CommandException.class)
    public void execute_whenColumnsLessThan2() {
        operations.insert(Faker.nextData("testcsv.csv", 4, 5),
                collectionName);
        List<Data> datas = Faker.nextData("test.csv", 10, 1);
        sut = new DataInsertCommand(dataService, datas);

        Long execute = execute(sut);
    }
}