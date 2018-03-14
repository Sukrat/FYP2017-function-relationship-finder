(function () {
    var app = angular.module('app');
    app.component('gridAnalysis', {
        templateUrl: './js/grid-analysis/grid-analysis.html',
        controller: ['GridAnalysisService', 'RootService',
            function (GridAnalysisService, RootService) {
                var vm = this;
                vm.cluster = cluster;

                vm.tolerances = "";
                vm.outputTolerance = "";
                vm.columnNo = "";

                function cluster(tolerances) {
                    RootService.loading(true);

                    GridAnalysisService.cluster(tolerances)
                        .then((data) => {
                            RootService.success(data.messages);
                        }).catch((error) => {
                        RootService.error(error)
                    })
                }

                // function Check(outputTolerance) {
                //     this.loading = true;
                //     return this.gridAnalysisService.functionCheck(outputTolerance)
                //         .then((data) => {
                //             this.success(data)
                //         })
                //         .catch((error) => {
                //             this.error(error)
                //         })
                // }
                //
                // function analyseColumn(columnNo) {
                //     this.loading = true;
                //     return this.gridAnalysisService.analyseColumn(columnNo)
                //         .then((response) => {
                //             this.success(data)
                //         })
                //         .catch((error) => {
                //             this.error(error)
                //         })
                // }
            }]
    })
})();
