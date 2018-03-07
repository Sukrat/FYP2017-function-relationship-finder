//package functlyser.controller;
//
//import functlyser.controller.messages.Message;
//import functlyser.service.AnalysisService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Arrays;
//
//import static java.lang.String.format;
//
//@RestController
//public class AnalysisController extends Controller {
//
//    private AnalysisService analysisService;
//
//    @Autowired
//    public AnalysisController(AnalysisService analysisService) {
//        this.analysisService = analysisService;
//    }
//
//
//    @RequestMapping(value = "/analysis/grid", method = RequestMethod.GET)
//    public ResponseEntity<Message> grid(@RequestParam("profileId") String profileId) {
//        int count = analysisService.divideIntoGrids(profileId);
//
//        Message message = new Message(Arrays.asList(format("%d records evaluated!", count),
//                "Grid analysis successfully completed!"));
//        return ResponseEntity.ok(message);
//    }
//}
