(function () {
    var app = angular.module('app');
    app.component('gridAnalysis', {
        templateUrl: './js/grid-analysis/grid-analysis.html',
        controller: ['GridAnalysisService', 'RootService', 'FileSaver',
            function (GridAnalysisService, RootService, FileSaver) {
                var vm = this;
                vm.cluster = cluster;
                vm.checkFunction = checkFunction;

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

                function checkFunction(outputTolerance) {
                    RootService.loading(true);
                    GridAnalysisService.checkFunction(outputTolerance)
                        .then((data) => {
                            var blob = new Blob([data], {
                                type: "text/plain;charset=utf-8"
                            });
                            FileSaver.saveAs(blob, "functionCheck.csv");
                            RootService.success(["File successfully downloading!"]);
                        })
                        .catch((error) => {
                            RootService.error(error)
                        })
                }

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
