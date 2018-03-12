import template from './grid-analysis.html';

class GridAnalysisController {

    constructor(gridAnalysisService) {
        this.gridAnalysisService = gridAnalysisService;

        this.msg = {};
        this.loading = false;

        this.tolerances = "";
        this.outputTolerance = "";
        this.columnNo = "";
    }

    cluster(tolerances) {
        this.loading = true;

        this.gridAnalysisService.cluster(tolerances)
            .then((data) => {
                this.success(data.messages);
            }).catch((error) => { this.error(error) })
    }

    functionCheck(outputTolerance) {
        this.loading = true;
        return this.gridAnalysisService.functionCheck(outputTolerance)
            .then((data) => { this.success(data) })
            .catch((error) => { this.error(error) })
    }

    analyseColumn(columnNo) {
        this.loading = true;
        return this.gridAnalysisService.analyseColumn(columnNo)
            .then((response) => { this.success(data) })
            .catch((error) => { this.error(error) })
    }

    error(error) {
        this.loading = false;
        this.msg = {
            error: error
        }
    }

    success(success) {
        this.loading = false;
        this.msg = {
            success: success
        }
    }
}

export default {
    template: template,
    controller: ['GridAnalysisService', GridAnalysisController]
};
