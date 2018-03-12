export default class GridAnalysisService {

    constructor($http, $q, _) {
        this.$http = $http;
        this.$q = $q;
        this._ = _
    }

    cluster(tolerances) {
        let defer = this.$q.defer();
        let tolArr = _.split(tolerances, ',');
        tolArr = _.map(tolArr, _.toNumber);
        if (_.findIndex(tolArr, _.isNaN) >= 0) {
            defer.reject(["Please enter valid integers!"]);
        } else if (_.size(_.trim(tolerances)) == 0) {
            defer.reject(["Please enter tolerance!"]);
        } else {
            this.$http.post('/analysis/grid/cluster', tolArr)
                .then((response) => {
                    defer.resolve(response.data)
                })
                .catch((error) => {
                    defer.reject(error.data.messages);
                })
        }
        return defer.promise;
    }

    functionCheck(outputTolerance) {
        return this.$http.post('/analysis/grid/functioncheck', _.toNumber(outputTolerance))
            .then((response) => {
                return response.data;
            })
            .catch((error) => {
                console.log(error);
                return this.$q.reject(error.data.messages);
            })
    }

    analyseColumn(columnNo) {
        return this.$http.post('/analysis/grid/column', _.toNumber(columnNo))
            .then((response) => {
                return response.data;
            })
            .catch((error) => {
                console.log(error);
                return this.$q.reject(error.data.messages);
            })
    }
}
