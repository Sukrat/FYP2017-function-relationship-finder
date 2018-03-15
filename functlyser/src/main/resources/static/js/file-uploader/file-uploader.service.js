(function () {
    var app = angular.module('app');
    app.service('FileUploaderService',
        ['$http', '$q', 'Upload','ErrorMessageHandler',
            function ($http, $q, Upload, ErrorMessageHandler) {
                var vm = this;
                vm.uploadFile = uploadFile;
                vm.deleteFile = deleteFile;
                vm.getFileNames = getFileNames;

                function uploadFile(file) {
                    var defer = $q.defer();
                    Upload.upload({
                        url: '/data/upload',
                        file: file
                    }).then(function (response) {
                        defer.resolve(response.data);
                    }, function (error) {
                        console.log(error);
                        defer.reject(ErrorMessageHandler.getError(error.data));
                    }, function (notify) {
                        var progressPercentage = parseInt(100.0 * notify.loaded / notify.total);
                        defer.notify(progressPercentage);
                    });
                    return defer.promise;
                }

                function deleteFile(filename) {
                    return $http.delete("/data/delete?filename=" + filename)
                        .then(function (response) {
                            return response.data;
                        })
                        .catch(function (error) {
                            console.log(error);
                            return $q.reject(ErrorMessageHandler.getError(error.data));
                        })
                }

                function getFileNames() {
                    return $http.get("/data/filenames")
                        .then(function (response) {
                            return response.data;
                        }).catch(function (error) {
                            console.log(error);
                            return $q.reject(ErrorMessageHandler.getError(error.data));
                        })
                }
            }])
})();