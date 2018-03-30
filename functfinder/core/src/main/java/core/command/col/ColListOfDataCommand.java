package core.command.col;

import core.arango.Operations;
import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;

import java.util.Collection;
import java.util.stream.Collectors;

public class ColListOfDataCommand implements ICommand<Collection<String>> {

    private Operations operations;

    public ColListOfDataCommand(Operations operations) {
        this.operations = operations;
    }

    @Override
    public Collection<String> execute(IProgress progress) {
        return operations.getCollectionNames()
                .stream()
                .filter(m -> m.contains("-" + Data.class.getSimpleName()))
                .collect(Collectors.toList());
    }
}
