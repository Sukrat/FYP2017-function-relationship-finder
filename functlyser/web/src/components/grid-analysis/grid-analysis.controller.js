import template from './grid-analysis.html';

class GridAnalysisController {

    constructor(gridAnalysisService) {
        this.gridAnalysisService = gridAnalysisService;

        this.msg = {};
        this.loading = false;
        this.tolerances = "";
    }

    group(tolerances) {
        this.loading = true;

        this.gridAnalysisService.groupByN(tolerances)
            .then((data) => {
                this.success(data.messages);
            }).catch((error) => { this.error(error) })
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
