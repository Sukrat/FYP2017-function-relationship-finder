package webapp.controller;

import com.arangodb.ArangoCursor;
import core.command.csv.CompiledRegressionToCsvCommand;
import core.command.csv.DataToCsvCommand;
import core.command.dbscan.DbScanAnalyseColumnsCommand;
import core.command.dbscan.DbScanFunctionalCommand;
import core.command.grid.GridAnalyseColumnsCommand;
import core.command.grid.GridFunctionCommand;
import core.model.CompiledRegression;
import core.model.Data;
import core.service.DataServiceCreator;
import core.service.ICsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import webapp.websocket.SyncCommandExecutor;
import webapp.websocket.WebSocketProgressService;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

@RestController
@RequestMapping("/analysis/{profile}")
public class AnalysisController {

    private WebSocketProgressService webSocketProgressService;
    private DataServiceCreator dataServiceCreator;
    private ICsvService csvService;

    @Autowired
    public AnalysisController(WebSocketProgressService webSocketProgressService, DataServiceCreator dataServiceCreator,
                              ICsvService csvService) {
        this.webSocketProgressService = webSocketProgressService;
        this.dataServiceCreator = dataServiceCreator;
        this.csvService = csvService;
    }

    @RequestMapping(value = "/grid/functioncheck", method = RequestMethod.POST)
    public ResponseEntity<Resource> functionCheck(
            @PathVariable("profile") String profile,
            @RequestParam("outputTolerance") double oTolerance,
            @RequestBody List<Double> ntolerances) {
        String filename = format("grid-fc-(%f).csv", oTolerance);

        ArangoCursor<Data> datas = webSocketProgressService.create(profile).execute(new GridFunctionCommand(
                dataServiceCreator.create(profile),
                ntolerances,
                oTolerance
        ));

        ByteArrayOutputStream file = webSocketProgressService.create(profile).execute(new DataToCsvCommand(
                csvService,
                datas.asListRemaining()
        ));
        return returnFile(filename, file);
    }

    @RequestMapping(value = "/grid/column", method = RequestMethod.POST)
    public ResponseEntity<Resource> analyseParameter(
            @PathVariable("profile") String profile,
            @RequestParam("columnNo") int columnNo,
            @RequestBody List<Double> n1tolerances) {
        String filename = format("grid-analyse-(%d).csv", columnNo);

        Collection<CompiledRegression> compiledRegressions = webSocketProgressService
                .create(profile).execute(new GridAnalyseColumnsCommand(
                        dataServiceCreator.create(profile),
                        n1tolerances,
                        columnNo
                ));

        ByteArrayOutputStream file = webSocketProgressService.create(profile)
                .execute(new CompiledRegressionToCsvCommand(
                        csvService,
                        compiledRegressions
                ));
        return returnFile(filename, file);
    }

    @RequestMapping(value = "/dbscan/functioncheck", method = RequestMethod.POST)
    public ResponseEntity<Resource> dbscanFunctionCheck(
            @PathVariable("profile") String profile,
            @RequestParam("radius") double nRadius,
            @RequestBody double oRadius) {
        String filename = format("dbscan-fc-(%f)-(%f).csv", nRadius, oRadius);

        ArangoCursor<Data> datas = webSocketProgressService.create(profile)
                .execute(new DbScanFunctionalCommand(
                        dataServiceCreator.create(profile),
                        nRadius,
                        oRadius
                ));

        ByteArrayOutputStream file = webSocketProgressService.create(profile)
                .execute(new DataToCsvCommand(
                        csvService,
                        datas.asListRemaining()
                ));
        return returnFile(filename, file);
    }

    @RequestMapping(value = "/dbscan/column", method = RequestMethod.POST)
    public ResponseEntity<Resource> dbscanAnalyseParameter(
            @PathVariable("profile") String profile,
            @RequestParam("radius") double n1radius,
            @RequestBody int columnNo) {
        String filename = format("dbscan-analyse-(%f)-(%d).csv", n1radius, columnNo);

        Collection<CompiledRegression> compiledRegressions = webSocketProgressService.create(profile)
                .execute(new DbScanAnalyseColumnsCommand(
                        dataServiceCreator.create(profile),
                        n1radius,
                        columnNo
                ));

        ByteArrayOutputStream file = webSocketProgressService.create(profile)
                .execute(new CompiledRegressionToCsvCommand(
                        csvService,
                        compiledRegressions
                ));
        return returnFile(filename, file);
    }

    private ResponseEntity<Resource> returnFile(String fileName, ByteArrayOutputStream file) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=\"" + fileName + "\"")
                .body(new ByteArrayResource(file.toByteArray()));
    }
}
