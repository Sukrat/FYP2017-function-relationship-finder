package core.command.profile;

import core.Util;
import core.arango.Operations;
import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;

public class ProfileDeleteCommand implements ICommand<Void> {

    private Operations operations;
    private String profile;

    public ProfileDeleteCommand(Operations operations, String profile) {
        this.operations = operations;
        this.profile = profile;
    }

    @Override
    public Void execute(IProgress progress) {
        if (profile == null || profile.trim().isEmpty()) {
            throw new CommandException("Profile cannot be empty when deleting!");
        }

        String collectionName = Util.dataCollectionName(profile);
        if (operations.collectionExists(collectionName)) {
            operations.dropCollection(collectionName);
        } else {
            throw new CommandException("Profile '%s' doesnot exists", profile);
        }
        return null;
    }
}
