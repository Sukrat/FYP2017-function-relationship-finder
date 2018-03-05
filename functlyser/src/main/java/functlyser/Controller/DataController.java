package functlyser.Controller;

import functlyser.Message;
import functlyser.model.Data;
import functlyser.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@RestController
public class DataController extends Controller {

    private DataService dataService;

    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @RequestMapping(value = "/data/upload", method = RequestMethod.POST)
    public ResponseEntity<Message> uploadcsv(@RequestParam("file") MultipartFile file,
                                             @RequestParam("profileId") String profileId) {
        List<Data> data = dataService.uploadCsv(profileId, file);
        Message message = new Message(Arrays.asList(format("%d records inserted!", data.stream().count()),
                format("%s successfully uploaded!", file.getOriginalFilename())));
        return ResponseEntity.ok(message);
    }

    @RequestMapping(value = "/data/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadcsv(@RequestParam("profileId") String profileId,
                                                @RequestParam("filename") String filename) {

        Resource file = dataService.downloadCsv(profileId, filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"").body(file);
    }

    @RequestMapping(value = "/data/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Message> delete(@RequestParam("profileId") String profileId,
                                          @RequestParam("filename") String filename) {

        long deleteCount = dataService.delete(profileId, filename);
        Message message = new Message(Arrays.asList(format("%d records deleted!", deleteCount),
                format("%s successfully deleted!", filename)));
        return ResponseEntity.ok(message);
    }
}
