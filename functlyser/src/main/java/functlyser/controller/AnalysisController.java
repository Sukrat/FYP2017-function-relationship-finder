package functlyser.controller;

import functlyser.command.grid.ClusterDataCommand;
import functlyser.controller.messages.Message;
import functlyser.service.WebSocketProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import static java.lang.String.format;

@RestController
@RequestMapping(value = "/analysis")
public class AnalysisController {

    public static String REPLY = "/reply/data";

    private WebSocketProgressService webSocketProgressService;

    private ClusterDataCommand clusterDataCommand;

    @Autowired
    public AnalysisController(WebSocketProgressService webSocketProgressService, ClusterDataCommand clusterDataCommand) {
        this.webSocketProgressService = webSocketProgressService;
        this.clusterDataCommand = clusterDataCommand;
    }


    @RequestMapping(value = "/grid/cluster", method = RequestMethod.POST)
    public ResponseEntity gridCluster(@RequestBody ArrayList<Double> tolerances) {
        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);
        long count = clusterDataCommand.execute(webSocketProgress, tolerances);
        return ResponseEntity.ok(new Message(format("%d groups created!", count)));
    }
//
//    @RequestMapping(value = "/analysis/grid/functioncheck", method = RequestMethod.POST)
//    public ResponseEntity<Resource> functionCheck(@RequestBody double tolerance) {
//        String filename = format("functioncheck-%.1f.csv", tolerance);
//        Resource file = gridService.functionalCheck(tolerance);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment;filename=\"" + filename + "\"").body(file);
//    }
//
//    @RequestMapping(value = "/analysis/grid/column", method = RequestMethod.POST)
//    public ResponseEntity<Resource> analyseParameter(@RequestBody int columnNo) {
//        String filename = format("analysedColNo-%d.csv", columnNo);
//        Resource file = gridService.analyseParameter(columnNo);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment;filename=\"" + filename + "\"").body(file);
//    }
//
//    @RequestMapping(value = "/analysis/dbscan/functioncheck", method = RequestMethod.POST)
//    public ResponseEntity<Resource> dbscanFunctionCheck(@RequestParam("radius") double radius,
//                                                        @RequestBody double outputTolerance) {
//        String filename = format("scan-functioncheck-%.1f-%.1f.csv", radius, outputTolerance);
//        Resource file = scanService.functionalCheck(radius, outputTolerance);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment;filename=\"" + filename + "\"").body(file);
//    }
//
//    @RequestMapping(value = "/analysis/dbscan/column", method = RequestMethod.POST)
//    public ResponseEntity<Resource> dbscanAnalyseParameter(@RequestParam("radius") double radius,
//                                                           @RequestBody int columnNo) {
//        String filename = format("analysedColNo-%.1f-%d.csv", radius, columnNo);
//        Resource file = scanService.analyseParameter(radius, columnNo);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment;filename=\"" + filename + "\"").body(file);
//    }
}
