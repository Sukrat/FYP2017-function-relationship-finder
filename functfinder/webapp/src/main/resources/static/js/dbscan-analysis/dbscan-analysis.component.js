(function () {
    var app = angular.module('app');
    app.component('dbscanAnalysis', {
        templateUrl: './js/dbscan-analysis/dbscan-analysis.html',
        controller: ['RootService', 'FileSaver', '$http', '_', 'BufferParser',
            function (RootService, FileSaver, $http, _, BufferParser) {
                var vm = this;
                vm.checkFunction = checkFunction;
                vm.analyseColumn = analyseColumn;

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
                        $http.post(url('/dbscan/functioncheck'), tolerance, {
                            responseType: 'arraybuffer',
                            params: {
                                radius: radiusTol
                            }
                        }).then(function (response) {
                            FileSaver.saveResponseAsFile(response);
                            RootService.success("File successfully downloading!");
                        }).catch(RootService.error)
                    }
                }


                function analyseColumn(radius, columnNo) {
                    RootService.loading(true);
                    var radiusTol = _.toNumber(radius);
                    var colNo = _.toInteger(columnNo);
                    if (_.isNaN(colNo) || _.isNaN(radiusTol)) {
                        return RootService.error('Please enter a valid double for radius and integer for Col Number!');
                    } else {
                        $http.post(url('/dbscan/column'), colNo, {
                            responseType: 'arraybuffer',
                            params: {
                                radius: radiusTol
                            }
                        }).then(function (response) {
                            FileSaver.saveResponseAsFile(response);
                            RootService.success("File successfully downloading!");
                        }).catch(RootService.error)
                    }
                }

                function url(task) {
                    return '/analysis/' + RootService.profile() + task;
                }
            }
        ]
    })
})();
