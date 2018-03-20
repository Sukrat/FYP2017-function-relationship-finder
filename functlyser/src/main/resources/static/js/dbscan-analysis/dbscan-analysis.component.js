(function () {
    var app = angular.module('app');
    app.component('dbscanAnalysis', {
        templateUrl: './js/dbscan-analysis/dbscan-analysis.html',
        controller: ['RootService', 'FileSaver', '$http', '_', 'BufferParser', 'ErrorMessageHandler',
            function (RootService, FileSaver, $http, _, BufferParser, ErrorMessageHandler) {
                var vm = this;
                vm.checkFunction = checkFunction;

                vm.radius = "";
                vm.outputTolerance = "";
                vm.analyseRadius = "";
                vm.columnNo = "";

                function checkFunction(radius, outputTolerance) {
                    RootService.loading(true);
                    var radiusTol = _.toNumber(radius);
                    var tolerance = _.toNumber(outputTolerance);
                    if (_.isNaN(tolerance) || _.isNaN(radiusTol)) {
                        return RootService.error('Please enter a valid double!');
                    } else {
                        $http.post('/analysis/dbscan/functioncheck', tolerance, {
                            responseType: 'arraybuffer',
                            params: {
                                radius: radiusTol
                            }
                        }).then((response) => {
                            FileSaver.saveResponseAsFile(response);
                            RootService.success("File successfully downloading!");
                        }).catch((error) => {
                            var err = BufferParser.parse(error.data);
                            console.log(err);
                            RootService.error(ErrorMessageHandler.getError(err));
                        })
                    }
                }


                // function analyseColumn(radius, columnNo) {
                //     RootService.loading(true);
                //     return DbscanAnalysisService.analyseColumn(radius, columnNo)
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
