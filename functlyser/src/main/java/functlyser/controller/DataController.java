package functlyser.controller;

import functlyser.controller.messages.Message;
import functlyser.model.Data;
import functlyser.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

@RestController
public class DataController extends Controller {

    private DataService dataService;

    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @RequestMapping(value = "/data/upload", method = RequestMethod.POST)
    public ResponseEntity<Message> uploadcsv(@RequestParam("file") MultipartFile file) {
        Collection<Data> data = dataService.uploadCsv(file);
        Message message = new Message(Arrays.asList(format("%d records inserted!", data.stream().count()),
                format("%s successfully uploaded!", file.getOriginalFilename())));
        return ResponseEntity.ok(message);
    }

    @RequestMapping(value = "/data/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadcsv(@RequestParam("filename") String filename) {

        Resource file = dataService.downloadCsv(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"").body(file);
    }

    @RequestMapping(value = "/data/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Message> delete(@RequestParam("filename") String filename) {

        long deleteCount = dataService.delete(filename);
        Message message = new Message(Arrays.asList(format("%d records deleted!", deleteCount),
                format("%s successfully deleted!", filename)));
        return ResponseEntity.ok(message);
    }

    @RequestMapping(value = "/data/filenames", method = RequestMethod.GET)
    public ResponseEntity<List<String>> list() {
        List<String> excelList = dataService.listExcels();
        return ResponseEntity.ok(excelList);
    }
}
