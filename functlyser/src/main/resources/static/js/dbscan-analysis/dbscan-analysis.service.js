(function () {
    var app = angular.module('app');
    app.service('DbscanAnalysisService',
        ['$http', '$q', '_', 'BufferParser', 'ErrorMessageHandler',
            function ($http, $q, _, BufferParser, ErrorMessageHandler) {
                var vm = this;
                vm.checkFunction = checkFunction;

                function checkFunction(radius, outputTolerance) {
                    var radiusTol = _.toNumber(radius);
                    var tolerance = _.toNumber(outputTolerance);
                    if (_.isNaN(tolerance) || _.isNaN(radiusTol)) {
                        return $q.reject(['Please enter a valid double!']);
                    }
                    return $http.post('/analysis/dbscan/functioncheck', undefined, {
                        responseType: 'arraybuffer',
                        params: {
                            radius: radiusTol,
                            outputTolerance: tolerance
                        }
                    })
                        .then((response) => {
                            return response.data;
                        }).catch((error) => {
                            var err = BufferParser.parse(error.data);
                            console.log(err);
                            return $q.reject(ErrorMessageHandler.getError(err));
                        })
                }


                function analyseColumn(columnNo) {
                    var colNo = _.toInteger(columnNo);
                    if (_.isNaN(colNo)) {
                        return $q.reject(['Please enter a valid integer!']);
                    }
                    return $http.post('/analysis/grid/column', colNo, {
                        responseType: 'arraybuffer'
                    })
                        .then((response) => {
                            return response.data;
                        })
                        .catch((error) => {
                            var err = BufferParser.parse(error.data);
                            console.log(err);
                            return $q.reject(ErrorMessageHandler.getError(err));
                        })
                }
            }])
})();
