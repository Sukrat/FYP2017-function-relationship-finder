package functlyser.controller;

import functlyser.controller.messages.Message;
import functlyser.model.GroupedData;
import functlyser.model.Regression;
import functlyser.service.GridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

@RestController
public class AnalysisController extends Controller {

    private GridService gridService;

    @Autowired
    public AnalysisController(GridService gridService) {
        this.gridService = gridService;
    }


    @RequestMapping(value = "/analysis/grid/cluster", method = RequestMethod.POST)
    public ResponseEntity<Message> isafunctionByGrid(@RequestBody ArrayList<Double> tolerances) {
        long count = gridService.groupByNdimensionAndInsert(tolerances);
        Message message = new Message(Arrays.asList(format("%d groups created!", count),
                "Grid analysis successfully completed!"));
        return ResponseEntity.ok(message);
    }

    @RequestMapping(value = "/analysis/grid/functioncheck", method = RequestMethod.POST)
    public ResponseEntity<List<GroupedData>> isafunctionByGrid(@RequestBody double tolerance) {
        List<GroupedData> result = gridService.getFunctionTerminator(tolerance);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/analysis/grid/column", method = RequestMethod.POST)
    public ResponseEntity<List<Regression>> analyseColumn(@RequestBody int columnNo) {
        List<Regression> result = gridService.analyseColumn(columnNo);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/analysis/grid/additive", method = RequestMethod.POST)
    public ResponseEntity additive(@RequestBody int numberOfVectors) {
        return ResponseEntity.ok(null);
    }
}
