(function () {
    var app = angular.module('app');
    app.service('GridAnalysisService',
        ['$http', '$q', '_', 'BufferParser', 'ErrorMessageHandler',
            function ($http, $q, _, BufferParser, ErrorMessageHandler) {
                var vm = this;
                vm.cluster = cluster;
                vm.checkFunction = checkFunction;
                vm.analyseColumn = analyseColumn;

                function cluster(tolerances) {
                    let defer = $q.defer();
                    let tolArr = _.split(tolerances, ',');
                    tolArr = _.map(tolArr, _.toNumber);
                    if (_.findIndex(tolArr, _.isNaN) >= 0) {
                        defer.reject(["Please enter valid integers!"]);
                    } else if (_.size(_.trim(tolerances)) == 0) {
                        defer.reject(["Please enter tolerance!"]);
                    } else {
                        $http.post('/analysis/grid/cluster', tolArr)
                            .then((response) => {
                                defer.resolve(response.data)
                            })
                            .catch((error) => {
                                defer.reject(ErrorMessageHandler.getError(error.data));
                            })
                    }
                    return defer.promise;
                }

                function checkFunction(outputTolerance) {
                    var tolerance = _.toNumber(outputTolerance);
                    if (_.isNaN(tolerance)) {
                        return $q.reject(['Please enter a valid double!']);
                    }
                    return $http.post('/analysis/grid/functioncheck', tolerance, {
                        responseType: 'arraybuffer'
                    })
                        .then((response) => {
                            return response;
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
