(function () {
    var app = angular.module('app');
    app.component('gridAnalysis', {
        templateUrl: './js/grid-analysis/grid-analysis.html',
        controller: ['GridAnalysisService', 'RootService', 'FileSaver',
            function (GridAnalysisService, RootService, FileSaver) {
                var vm = this;
                vm.cluster = cluster;
                vm.checkFunction = checkFunction;
                vm.analyseColumn = analyseColumn;

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


                function analyseColumn(columnNo) {
                    RootService.loading(true);
                    return GridAnalysisService.analyseColumn(columnNo)
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
            }]
    })
})();
