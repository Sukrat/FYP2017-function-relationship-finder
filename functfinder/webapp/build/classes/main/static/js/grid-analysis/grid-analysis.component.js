(function () {
    var app = angular.module('app');
    app.component('gridAnalysis', {
        templateUrl: './js/grid-analysis/grid-analysis.html',
        controller: ['RootService', 'FileSaver', '$http', '_', 'BufferParser',
            function (RootService, FileSaver, $http, _, BufferParser) {
                var vm = this;
                vm.checkFunction = checkFunction;
                vm.analyseColumn = analyseColumn;

                vm.tolerances = "";
                vm.outputTolerance = "";
                vm.columnNo = "";

                function checkFunction(outputTolerance, tolerances) {
                    RootService.loading(true);
                    var tolerance = _.toNumber(outputTolerance);
                    var tolArr = _.map(_.split(tolerances, ','), _.toNumber);
                    if (_.isNaN(tolerance)) {
                        return RootService.error('Please enter a valid double for output tolerance!');
                    } else if (_.findIndex(tolArr, _.isNaN) >= 0) {
                        RootService.error("Please enter valid integers for tolerances!");
                    } else if (_.size(_.trim(tolerances)) == 0) {
                        RootService.error("Please enter tolerance for tolerances!");
                    } else {
                        $http.post('/analysis/grid/functioncheck', tolArr, {
                            responseType: 'arraybuffer',
                            params: {
                                outputTolerance: tolerance
                            }
                        }).then(function (response) {
                            FileSaver.saveResponseAsFile(response);
                            RootService.success("File successfully downloading!");
                        }).catch(function (error) {
                            var err = BufferParser.parse(error.data);
                            console.error(err);
                            RootService.error(err);
                        })
                        ;
                    }
                }

                function analyseColumn(columnNo, tolerances) {
                    RootService.loading(true);
                    var colNo = _.toInteger(columnNo);
                    var tolArr = _.map(_.split(tolerances, ','), _.toNumber);
                    if (_.isNaN(colNo)) {
                        RootService.error('Please enter a valid integer for column no!');
                    } else if (_.findIndex(tolArr, _.isNaN) >= 0) {
                        RootService.error("Please enter valid integers for tolerances!");
                    } else if (_.size(_.trim(tolerances)) == 0) {
                        RootService.error("Please enter tolerance for tolerances!");
                    } else {
                        $http.post('/analysis/grid/column', tolArr, {
                            responseType: 'arraybuffer',
                            params: {
                                columnNo: colNo
                            }
                        }).then(function (response) {
                            FileSaver.saveResponseAsFile(response);
                            RootService.success("File successfully downloading!");
                        }).catch(function (error) {
                            var err = BufferParser.parse(error.data);
                            console.log(err);
                            RootService.error(err);
                        })
                    }
                }
            }]
    })
})();
