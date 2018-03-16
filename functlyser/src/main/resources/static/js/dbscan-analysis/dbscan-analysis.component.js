(function () {
    var app = angular.module('app');
    app.component('dbscanAnalysis', {
        templateUrl: './js/dbscan-analysis/dbscan-analysis.html',
        controller: ['DbscanAnalysisService', 'RootService', 'FileSaver',
            function (DbscanAnalysisService, RootService, FileSaver) {
                var vm = this;
                vm.checkFunction = checkFunction;
                vm.analyseColumn = analyseColumn;

                vm.radius = "";
                vm.outputTolerance = "";
                vm.analyseRadius = "";
                vm.columnNo = "";

                function checkFunction(radius, outputTolerance) {
                    RootService.loading(true);
                    DbscanAnalysisService.checkFunction(radius, outputTolerance)
                        .then((response) => {
                            FileSaver.saveResponseAsFile(response);
                            RootService.success(["File successfully downloading!"]);
                        })
                        .catch((error) => {
                            RootService.error(error)
                        })
                }


                function analyseColumn(radius, columnNo) {
                    RootService.loading(true);
                    return DbscanAnalysisService.analyseColumn(radius, columnNo)
                        .then((response) => {
                            FileSaver.saveResponseAsFile(response);
                            RootService.success(["File successfully downloading!"]);
                        })
                        .catch((error) => {
                            RootService.error(error)
                        })
                }
            }]
    })
})();
