package webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webapp.command.data.*;
import webapp.controller.messages.Message;
import webapp.service.WebSocketProgressService;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/data")
public class DataController {

    public static String REPLY = "/reply/data";

    private WebSocketProgressService webSocketProgressService;

    private DataUploadCommand dataUploadCommand;

    private ListFileNamesCommand listFileNamesCommand;

    private DeleteDataCommand deleteDataCommand;

    private DataGetCommand dataGetCommand;

    private NormalizeCommand normalizeCommand;

    private UnNormalizeCommand unNormalizeCommand;

    @Autowired
    public DataController(DataUploadCommand dataUploadCommand,
                          WebSocketProgressService webSocketProgressService,
                          ListFileNamesCommand listFileNamesCommand,
                          DeleteDataCommand dataCommand, DataGetCommand dataGetCommand, NormalizeCommand normalizeCommand, UnNormalizeCommand unNormalizeCommand) {
        this.dataUploadCommand = dataUploadCommand;
        this.webSocketProgressService = webSocketProgressService;
        this.listFileNamesCommand = listFileNamesCommand;
        this.deleteDataCommand = dataCommand;
        this.dataGetCommand = dataGetCommand;
        this.normalizeCommand = normalizeCommand;
        this.unNormalizeCommand = unNormalizeCommand;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) throws IOException {
        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);
        DataUploadCommand.Param param = new DataUploadCommand.Param(file.getInputStream(), file.getOriginalFilename());

        webSocketProgress.update("Uploading of the file '%s' has been added to the queue!", file.getOriginalFilename());
        Long result = dataUploadCommand.execute(webSocketProgress, param);

        return ResponseEntity.ok().body(new Message(result + " records added successfully!"));
    }

    @RequestMapping(value = "/filenames", method = RequestMethod.GET)
    public ResponseEntity filenames() {
        Collection<String> execute = listFileNamesCommand.execute(null);
        return ResponseEntity.ok().body(execute);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity delete(@RequestParam("fileName") String fileName) {
        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);

        Long result = deleteDataCommand.execute(webSocketProgress, fileName);

        return ResponseEntity.ok().body(new Message(result + " records deleted successfully!"));
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity download(@RequestParam("fileName") String fileName) {
        Resource result = dataGetCommand.execute(fileName);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + fileName + "\"").body(result);
    }

    @RequestMapping(value = "/normalize", method = RequestMethod.POST)
    public ResponseEntity normalize() {
        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);
        Long result = normalizeCommand.execute(webSocketProgress, null);
        return ResponseEntity.ok().body(new Message(result + " records normalized successfully!"));
    }

    @RequestMapping(value = "/normalize/undo", method = RequestMethod.POST)
    public ResponseEntity unNormalize() {
        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);
        Long result = unNormalizeCommand.execute(webSocketProgress, null);
        return ResponseEntity.ok().body(new Message(result + " records un-normalized successfully!"));
    }
}
