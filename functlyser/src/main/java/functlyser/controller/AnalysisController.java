package functlyser.controller;

import functlyser.controller.messages.Message;
import functlyser.service.GridService;
import functlyser.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.String.format;

@RestController
public class AnalysisController extends Controller {

    private GridService gridService;

    private ScanService scanService;

    @Autowired
    public AnalysisController(GridService gridService, ScanService scanService) {
        this.gridService = gridService;
        this.scanService = scanService;
    }


    @RequestMapping(value = "/analysis/grid/cluster", method = RequestMethod.POST)
    public ResponseEntity<Message> gridCluster(@RequestBody ArrayList<Double> tolerances) {
        long count = gridService.cluster(tolerances);
        Message message = new Message(Arrays.asList(format("%d groups created!", count),
                "Grid analysis successfully completed!"));
        return ResponseEntity.ok(message);
    }

    @RequestMapping(value = "/analysis/grid/functioncheck", method = RequestMethod.POST)
    public ResponseEntity<Resource> functionCheck(@RequestBody double tolerance) {
        String filename = format("functioncheck-%f", tolerance);
        Resource file = gridService.functionalCheck(tolerance);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"").body(file);
    }

    @RequestMapping(value = "/analysis/grid/column", method = RequestMethod.POST)
    public ResponseEntity<Resource> analyseParameter(@RequestBody int columnNo) {
        String filename = format("analysedColNo-%d", columnNo);
        Resource file = gridService.analyseParameter(columnNo);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"").body(file);
    }

    @RequestMapping(value = "/analysis/dbscan/functioncheck", method = RequestMethod.POST)
    public ResponseEntity<Resource> dbscanFunctionCheck(@RequestParam("radius") double radius,
                                                        @RequestParam("outputTolerance") double outputTolerance) {
        String filename = format("scan-functioncheck-%f-%f", radius, outputTolerance);
        Resource file = scanService.functionalCheck(radius, outputTolerance);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"").body(file);
    }
}
