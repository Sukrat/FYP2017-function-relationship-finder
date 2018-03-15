(function () {
    var app = angular.module('app');
    app.service('GridAnalysisService',
        ['$http', '$q', '_',
            function ($http, $q, _) {
                var vm = this;
                vm.cluster = cluster;
                vm.checkFunction = checkFunction;

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
                                defer.reject(error.data.messages);
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
                            return response.data;
                        }).catch((error) => {
                            console.log(error);
                            return $q.reject(error.data.messages);
                        })
                }

                //
                // function analyseColumn(columnNo) {
                //     return this.$http.post('/analysis/grid/column', _.toNumber(columnNo))
                //         .then((response) => {
                //             return response.data;
                //         })
                //         .catch((error) => {
                //             console.log(error);
                //             return this.$q.reject(error.data.messages);
                //         })
                // }
            }])
})();
