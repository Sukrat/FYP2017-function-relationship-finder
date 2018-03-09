export default class GridAnalysisService {

    constructor($http, $q, _) {
        this.$http = $http;
        this.$q = $q;
        this._ = _
    }

    groupByN(tolerances) {
        let defer = this.$q.defer();
        let tolArr = _.split(tolerances, ',');
        tolArr = _.map(tolArr, _.toNumber);
        if (_.findIndex(tolArr, _.isNaN) >= 0) {
            defer.reject(["Please enter valid integers!"]);
        } else {
            this.$http.post('/analysis/grid/group', tolArr)
                .then((response) => { defer.resolve(response.data) })
                .catch((error) => {
                    defer.reject(error.data.messages);
                })
        }
        return defer.promise;
    }

}
