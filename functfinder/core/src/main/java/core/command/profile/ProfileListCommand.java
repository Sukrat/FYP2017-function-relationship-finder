package core.command.profile;

import core.arango.Operations;
import core.command.ICommand;
import core.command.IProgress;
import core.model.Data;

import java.util.Collection;
import java.util.stream.Collectors;

public class ProfileListCommand implements ICommand<Collection<String>> {

    private Operations operations;

    public ProfileListCommand(Operations operations) {
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
