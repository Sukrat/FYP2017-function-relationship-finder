(function () {
    var app = angular.module('app');
    app.component('gridAnalysis', {
        templateUrl: './js/grid-analysis/grid-analysis.html',
        controller: ['RootService', 'FileSaver', '$http', '_', 'ErrorMessageHandler',
            function (RootService, FileSaver, $http, _, ErrorMessageHandler) {
                var vm = this;
                vm.cluster = cluster;

                vm.tolerances = "";
                vm.outputTolerance = "";
                vm.columnNo = "";

                function cluster(tolerances) {
                    RootService.loading(true);
                    let tolArr = _.split(tolerances, ',');
                    tolArr = _.map(tolArr, _.toNumber);
                    if (_.findIndex(tolArr, _.isNaN) >= 0) {
                        RootService.error("Please enter valid integers!");
                    } else if (_.size(_.trim(tolerances)) == 0) {
                        RootService.error("Please enter tolerance!");
                    } else {
                        $http.post('/analysis/grid/cluster', tolArr)
                            .then((response) => {
                                RootService.success(response.data.message);
                            })
                            .catch((error) => {
                                console.error(error)
                                RootService.error(ErrorMessageHandler.getError(error.data));
                            })
                    }
                }

                // function checkFunction(outputTolerance) {
                //     RootService.loading(true);
                //     GridAnalysisService.checkFunction(outputTolerance)
                //         .then((response) => {
                //             FileSaver.saveResponseAsFile(response);
                //             RootService.success(["File successfully downloading!"]);
                //         })
                //         .catch((error) => {
                //             RootService.error(error)
                //         })
                // }
                //
                //
                // function analyseColumn(columnNo) {
                //     RootService.loading(true);
                //     return GridAnalysisService.analyseColumn(columnNo)
                //         .then((response) => {
                //             FileSaver.saveResponseAsFile(response);
                //             RootService.success(["File successfully downloading!"]);
                //         })
                //         .catch((error) => {
                //             RootService.error(error)
                //         })
                // }
            }]
    })
})();
