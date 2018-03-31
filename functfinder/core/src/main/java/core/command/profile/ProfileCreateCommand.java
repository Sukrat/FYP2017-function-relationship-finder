package core.command.profile;

import core.Util;
import core.arango.Operations;
import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;

public class ProfileCreateCommand implements ICommand<Void> {

    private Operations operations;
    private String profile;

    public ProfileCreateCommand(Operations operations, String profile) {
        this.operations = operations;
        this.profile = profile;
    }

    @Override
    public Void execute(IProgress progress) {
        String collectionName = Util.dataCollectionName(profile);
        if (operations.collectionExists(collectionName)) {
            throw new CommandException("'%s' profile already exists!", Util.getProfile(collectionName));
        }
        operations.collection(collectionName);
        return null;
    }
}
