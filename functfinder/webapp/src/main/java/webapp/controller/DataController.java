package webapp.controller;

import com.arangodb.ArangoCursor;
import core.command.csv.CsvToDataCommand;
import core.command.csv.DataToCsvCommand;
import core.command.data.*;
import core.model.Data;
import core.service.CsvService;
import core.service.DataServiceCreator;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import webapp.controller.messages.Message;
import webapp.service.WebSocketProgressService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/data/{profile}")
public class DataController {

    private WebSocketProgressService webSocketProgressService;
    private SyncCommandExecutor syncCommandExecutor;
    private DataServiceCreator dataServiceCreator;
    private CsvService csvService;

    public DataController(WebSocketProgressService webSocketProgressService, SyncCommandExecutor syncCommandExecutor,
                          DataServiceCreator dataServiceCreator,
                          CsvService csvService) {
        this.webSocketProgressService = webSocketProgressService;
        this.syncCommandExecutor = syncCommandExecutor;
        this.dataServiceCreator = dataServiceCreator;
        this.csvService = csvService;
    }


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity upload(@PathVariable("profile") String profile,
                                 @RequestParam("file") MultipartFile file) throws IOException {
        Collection<Data> datas = syncCommandExecutor.execute(new CsvToDataCommand(
                csvService,
                file.getInputStream(),
                file.getOriginalFilename()
        ));
        Long result = syncCommandExecutor.execute(new DataInsertCommand(
                dataServiceCreator.create(profile),
                datas
        ));
        DataUploadCommand.Param param = new DataUploadCommand.Param(file.getInputStream(), file.getOriginalFilename());
        return ResponseEntity.ok().body(new Message(result + " records added successfully!"));
    }

    @RequestMapping(value = "/filenames", method = RequestMethod.GET)
    public Collection<String> filenames(@PathVariable("profile") String profile) {
        Collection<String> result = syncCommandExecutor.execute(new DataGetFileNamesCommand(
                dataServiceCreator.create(profile)
        ));
        return result;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("profile") String profile,
                                 @RequestParam("fileName") String fileName) {
        Long result = syncCommandExecutor.execute(new DataDeleteByFileNameCommand(
                dataServiceCreator.create(profile),
                fileName
        ));
        return ResponseEntity.ok().body(new Message(result + " records deleted successfully!"));
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity download(@PathVariable("profile") String profile,
                                   @RequestParam("fileName") String fileName) {
        ArangoCursor<Data> datas = syncCommandExecutor.execute(new DataByFileNameCommand(
                dataServiceCreator.create(profile),
                fileName
        ));
        ByteArrayOutputStream file = syncCommandExecutor.execute(new DataToCsvCommand(
                csvService,
                datas.asListRemaining()
        ));

        return returnFile(fileName, file);
    }

    @RequestMapping(value = "/normalize", method = RequestMethod.POST)
    public ResponseEntity normalize(@PathVariable("profile") String profile) {
        Long result = syncCommandExecutor.execute(new DataNormalizeCommand(
                dataServiceCreator.create(profile)
        ));
        return ResponseEntity.ok().body(new Message(result + " records normalized successfully!"));
    }

    @RequestMapping(value = "/normalize/undo", method = RequestMethod.POST)
    public ResponseEntity unNormalize(@PathVariable("profile") String profile) {
        Long result = syncCommandExecutor.execute(new DataUnNormalizeCommand(
                dataServiceCreator.create(profile)
        ));
        return ResponseEntity.ok().body(new Message(result + " records un-normalized successfully!"));
    }

    private ResponseEntity returnFile(String fileName, ByteArrayOutputStream file) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + fileName + "\"")
                .body(new ByteArrayResource(file.toByteArray()));
    }
}
