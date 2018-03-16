(function () {
    var app = angular.module('app');
    app.service('DbscanAnalysisService',
        ['$http', '$q', '_', 'BufferParser', 'ErrorMessageHandler',
            function ($http, $q, _, BufferParser, ErrorMessageHandler) {
                var vm = this;
                vm.checkFunction = checkFunction;
                vm.analyseColumn = analyseColumn;

                function checkFunction(radius, outputTolerance) {
                    var radiusTol = _.toNumber(radius);
                    var tolerance = _.toNumber(outputTolerance);
                    if (_.isNaN(tolerance) || _.isNaN(radiusTol)) {
                        return $q.reject(['Please enter a valid double!']);
                    }
                    return $http.post('/analysis/dbscan/functioncheck', tolerance, {
                        responseType: 'arraybuffer',
                        params: {
                            radius: radiusTol
                        }
                    })
                        .then((response) => {
                            return response;
                        }).catch((error) => {
                            var err = BufferParser.parse(error.data);
                            console.log(err);
                            return $q.reject(ErrorMessageHandler.getError(err));
                        })
                }


                function analyseColumn(radius, columnNo) {
                    var radiusTol = _.toNumber(radius);
                    var colNo = _.toInteger(columnNo);
                    if (_.isNaN(colNo) || _.isNaN(radiusTol)) {
                        return $q.reject(['Please enter a valid double for radius and integer for Col Number!']);
                    }
                    return $http.post('/analysis/dbscan/column', colNo, {
                        responseType: 'arraybuffer',
                        params: {
                            radius: radiusTol
                        }
                    })
                        .then((response) => {
                            return response;
                        })
                        .catch((error) => {
                            var err = BufferParser.parse(error.data);
                            console.log(err);
                            return $q.reject(ErrorMessageHandler.getError(err));
                        })
                }
            }])
})();
