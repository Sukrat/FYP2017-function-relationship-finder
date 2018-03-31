package core.command.profile;

import core.Util;
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
                .filter(m -> Util.getCollection(m).equals(Data.class.getSimpleName()))
                .filter(m -> !Util.getProfile(m).isEmpty())
                .map(m -> Util.getProfile(m))
                .collect(Collectors.toList());
    }
}
