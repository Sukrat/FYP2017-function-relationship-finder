package core.command.profile;

import com.arangodb.ArangoDBException;
import core.Util;
import core.arango.Operations;
import core.command.CommandException;
import core.command.ICommand;
import core.command.IProgress;
import core.service.ServiceException;

public class ProfileCreateCommand implements ICommand<Void> {

    private Operations operations;
    private String profile;

    public ProfileCreateCommand(Operations operations, String profile) {
        this.operations = operations;
        this.profile = profile;
    }

    @Override
    public Void execute(IProgress progress) {
        if (profile == null || profile.trim().isEmpty()) {
            throw new CommandException("Profile name cannot be null!");
        }
        String collectionName = Util.dataCollectionName(profile);
        try {
            if (operations.collectionExists(collectionName)) {
                throw new CommandException("'%s' profile already exists!", Util.getProfile(collectionName));
            }
            operations.collection(collectionName);
        } catch (ArangoDBException ex) {
            if (ex.getErrorNum() == 1208) {
                throw new ServiceException("'%s' is not allowed! Valid character are [a-zA-Z0-9_-]", profile);
            }
            throw ex;
        }
        return null;
    }
}
