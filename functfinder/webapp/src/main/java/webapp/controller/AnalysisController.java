package webapp.controller;

import core.command.dbscan.DbScanAnalyseColumnCommand;
import core.command.dbscan.DbScanFunctionalCheckCommand;
import core.command.grid.AnalyseGridDataColumnCommand;
import core.command.grid.GridFunctionCheckCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webapp.service.WebSocketProgressService;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

@RestController
@RequestMapping(value = "/analysis")
public class AnalysisController {

    public static String REPLY = "/reply/data";

    private WebSocketProgressService webSocketProgressService;

    private GridFunctionCheckCommand gridFunctionCheckCommand;

    private AnalyseGridDataColumnCommand analyseGridDataColumnCommand;

    private DbScanFunctionalCheckCommand dbScanFunctionalCheckCommand;

    private DbScanAnalyseColumnCommand dbScanAnalyseColumnCommand;

    @Autowired
    public AnalysisController(WebSocketProgressService webSocketProgressService,
                              GridFunctionCheckCommand gridFunctionCheckCommand,
                              AnalyseGridDataColumnCommand analyseGridDataColumnCommand,
                              DbScanFunctionalCheckCommand dbScanFunctionalCheckCommand,
                              DbScanAnalyseColumnCommand dbScanAnalyseColumnCommand) {
        this.webSocketProgressService = webSocketProgressService;
        this.gridFunctionCheckCommand = gridFunctionCheckCommand;
        this.analyseGridDataColumnCommand = analyseGridDataColumnCommand;
        this.dbScanFunctionalCheckCommand = dbScanFunctionalCheckCommand;
        this.dbScanAnalyseColumnCommand = dbScanAnalyseColumnCommand;
    }

    @RequestMapping(value = "/grid/functioncheck", method = RequestMethod.POST)
    public ResponseEntity<Resource> functionCheck(
            @RequestParam("outputTolerance") double outputTolerance,
            @RequestBody List<Double> tolerances) {
        String filename = format("functioncheck-(%f).csv", outputTolerance);

        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);

        ByteArrayOutputStream file = gridFunctionCheckCommand.execute(webSocketProgress,
                new GridFunctionCheckCommand.Param(outputTolerance, tolerances));

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"")
                .body(new ByteArrayResource(file.toByteArray()));
    }

    @RequestMapping(value = "/grid/column", method = RequestMethod.POST)
    public ResponseEntity<Resource> analyseParameter(
            @RequestParam("columnNo") int columnNo,
            @RequestBody List<Double> tolerances) {
        String filename = format("analysedColNo-(%d).csv", columnNo);

        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);

        ByteArrayOutputStream file = analyseGridDataColumnCommand.execute(webSocketProgress,
                new AnalyseGridDataColumnCommand.Param(columnNo, tolerances));

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"")
                .body(new ByteArrayResource(file.toByteArray()));
    }

    @RequestMapping(value = "/dbscan/functioncheck", method = RequestMethod.POST)
    public ResponseEntity<Resource> dbscanFunctionCheck(@RequestParam("radius") double radius,
                                                        @RequestBody double outputTolerance) {
        String filename = format("scan-functioncheck-(%f)-(%f).csv", radius, outputTolerance);

        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);

        ByteArrayOutputStream file = dbScanFunctionalCheckCommand.execute(webSocketProgress,
                new DbScanFunctionalCheckCommand.Param(radius, outputTolerance));

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"")
                .body(new ByteArrayResource(file.toByteArray()));
    }

    @RequestMapping(value = "/dbscan/column", method = RequestMethod.POST)
    public ResponseEntity<Resource> dbscanAnalyseParameter(@RequestParam("radius") double radius,
                                                           @RequestBody int columnNo) {
        String filename = format("analysedColNo-(%f)-(%d).csv", radius, columnNo);

        WebSocketProgress webSocketProgress = webSocketProgressService.create(REPLY);

        ByteArrayOutputStream file = dbScanAnalyseColumnCommand.execute(webSocketProgress,
                new DbScanAnalyseColumnCommand.Param(radius, columnNo));

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + filename + "\"")
                .body(new ByteArrayResource(file.toByteArray()));
    }
}
