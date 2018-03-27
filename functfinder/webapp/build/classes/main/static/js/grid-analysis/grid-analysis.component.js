(function () {
    var app = angular.module('app');
    app.component('gridAnalysis', {
        templateUrl: './js/grid-analysis/grid-analysis.html',
        controller: ['RootService', 'FileSaver', '$http', '_', 'ErrorMessageHandler', 'BufferParser',
            function (RootService, FileSaver, $http, _, ErrorMessageHandler, BufferParser) {
                var vm = this;
                vm.cluster = cluster;
                vm.checkFunction = checkFunction;
                vm.analyseColumn = analyseColumn;

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

                function checkFunction(outputTolerance) {
                    RootService.loading(true);
                    var tolerance = _.toNumber(outputTolerance);
                    if (_.isNaN(tolerance)) {
                        return RootService.error('Please enter a valid double!');
                    } else {
                        $http.post('/analysis/grid/functioncheck', tolerance, {
                            responseType: 'arraybuffer'
                        }).then((response) => {
                            FileSaver.saveResponseAsFile(response);
                            RootService.success("File successfully downloading!");
                        }).catch((error) => {
                            var err = BufferParser.parse(error.data);
                            console.error(err);
                            RootService.error(ErrorMessageHandler.getError(err));
                        });
                    }
                }

                function analyseColumn(columnNo) {
                    RootService.loading(true);
                    var colNo = _.toInteger(columnNo);
                    if (_.isNaN(colNo)) {
                        RootService.error('Please enter a valid integer!');
                    } else {
                        $http.post('/analysis/grid/column', colNo, {
                            responseType: 'arraybuffer'
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
            }]
    })
})();
