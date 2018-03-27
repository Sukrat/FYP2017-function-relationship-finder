package webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webapp.command.dbscan.DbScanAnalyseColumnCommand;
import webapp.command.dbscan.DbScanFunctionalCheckCommand;
import webapp.command.grid.AnalyseGridDataColumnCommand;
import webapp.command.grid.ClusterDataCommand;
import webapp.command.grid.GridFunctionCheckCommand;
import webapp.controller.messages.Message;
import webapp.service.WebSocketProgressService;

import java.util.ArrayList;

import static java.lang.String.format;

@RestController
@RequestMapping(value = "/analysis")
public class AnalysisController {

    public static String REPLY = "/reply/data";

    private WebSocketProgressService webSocketProgressService;

    private ClusterDataCommand clusterDataCommand;

    private GridFunctionCheckCommand gridFunctionCheckCommand;

    private AnalyseGridDataColumnCommand analyseGridDataColumnCommand;

    private DbScanFunctionalCheckCommand dbScanFunctionalCheckCommand;

    private DbScanAnalyseColumnCommand dbScanAnalyseColumnCommand;

    @Autowired
    public AnalysisController(WebSocketProgressService webSocketProgressService, ClusterDataCommand clusterDataCommand, GridFunctionCheckCommand gridFunctionCheckCommand, AnalyseGridDataColumnCommand analyseGridDataColumnCommand, DbScanFunctionalCheckCommand dbScanFunctionalCheckCommand, DbScanAnalyseColumnCommand dbScanAnalyseColumnCommand) {
        this.webSocketProgressService = webSocketProgressService;
        this.clusterDataCommand = clusterDataCommand;
        this.gridFunctionCheckCommand = gridFunctionCheckCommand;
        this.analyseGridDataColumnCommand = analyseGridDataColumnCommand;
        this.dbScanFunctionalCheckCommand = dbScanFunctionalCheckCommand;
        this.dbScanAnalyseColumnCommand = dbScanAnalyseColumnCommand;
    }


    @RequestMapping(value = "/grid/cluster", method = RequestMethod.POST)
    public ResponseEntity gridCluster(@RequestBody ArrayList<Double> tolerances) {
        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);
        long count = clusterDataCommand.execute(webSocketProgress, tolerances);
        return ResponseEntity.ok(new Message(format("%d groups created!", count)));
    }

    @RequestMapping(value = "/grid/functioncheck", method = RequestMethod.POST)
    public ResponseEntity<Resource> functionCheck(@RequestBody double tolerance) {
        String filename = format("functioncheck-(%f).csv", tolerance);

        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);
        Resource file = gridFunctionCheckCommand.execute(webSocketProgress, tolerance);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"").body(file);
    }

    @RequestMapping(value = "/grid/column", method = RequestMethod.POST)
    public ResponseEntity<Resource> analyseParameter(@RequestBody int columnNo) {
        String filename = format("analysedColNo-(%d).csv", columnNo);

        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);
        Resource file = analyseGridDataColumnCommand.execute(webSocketProgress, columnNo);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"").body(file);
    }

    @RequestMapping(value = "/dbscan/functioncheck", method = RequestMethod.POST)
    public ResponseEntity<Resource> dbscanFunctionCheck(@RequestParam("radius") double radius,
                                                        @RequestBody double outputTolerance) {
        String filename = format("scan-functioncheck-(%f)-(%f).csv", radius, outputTolerance);

        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);
        Resource file = dbScanFunctionalCheckCommand.execute(webSocketProgress,
                new DbScanFunctionalCheckCommand.Param(radius, outputTolerance));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"").body(file);
    }

    @RequestMapping(value = "/dbscan/column", method = RequestMethod.POST)
    public ResponseEntity<Resource> dbscanAnalyseParameter(@RequestParam("radius") double radius,
                                                           @RequestBody int columnNo) {
        String filename = format("analysedColNo-(%f)-(%d).csv", radius, columnNo);

        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);
        Resource file = dbScanAnalyseColumnCommand.execute(webSocketProgress,
                new DbScanAnalyseColumnCommand.Param(radius, columnNo));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"").body(file);
    }
}
