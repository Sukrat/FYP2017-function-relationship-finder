package webapp.controller;

import core.arango.Operations;
import core.command.profile.ProfileCreateCommand;
import core.command.profile.ProfileDeleteCommand;
import core.command.profile.ProfileListCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webapp.controller.messages.Message;
import webapp.websocket.SyncCommandExecutor;

import java.util.Collection;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private Operations operations;
    private SyncCommandExecutor syncCommandExecutor;

    @Autowired
    public ProfileController(Operations operations, SyncCommandExecutor syncCommandExecutor) {
        this.operations = operations;
        this.syncCommandExecutor = syncCommandExecutor;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody String profile) {
        syncCommandExecutor.execute(new ProfileCreateCommand(
                operations,
                profile
        ));
        return ResponseEntity
                .ok(new Message(String.format("Profile '%s' successfully created!", profile)));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Collection<String> list() {
        Collection<String> result = syncCommandExecutor.execute(new ProfileListCommand(
                operations
        ));
        return result;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity delete(@RequestBody String profile) {
        syncCommandExecutor.execute(new ProfileDeleteCommand(
                operations,
                profile
        ));
        return ResponseEntity
                .ok(new Message(String.format("Profile '%s' successfully deleted!", profile)));
    }
}
