(function () {
    var app = angular.module('app');
    app.component('fileUploader', {
        templateUrl: './js/file-uploader/file-uploader.html',
        controller: ['RootService', '$http', 'Upload',
            function (RootService, $http, Upload) {
                var vm = this;
                vm.upload = upload;
                vm.listFileNames = listFileNames;
                vm.deleteFile = deleteFile;
                vm.normalize = normalize;
                vm.unNormalize = unNormalize;

                vm.data = [];

                listFileNames();

                function upload() {
                    var file = document.getElementById('fileInput').files[0];
                    if (file == undefined) {
                        RootService.error('Please select a file to be uploaded!');
                        return;
                    }
                    RootService.loading(true);
                    Upload.upload({
                        url: '/data/upload',
                        file: file
                    }).then(function (response) {
                        RootService.success(response.data.message);
                        vm.listFileNames();
                    }, function (error) {
                        console.error(error);
                        RootService.error(error.data);
                    }, function (notify) {
                        vm.percentage = parseInt(100.0 * notify.loaded / notify.total);
                    });
                }

                function listFileNames() {
                    RootService.loading(true);
                    $http.get("/data/filenames")
                        .then(function (response) {
                            vm.data = response.data;
                            RootService.loading(false);
                        }).catch(function (error) {
                        console.error(error);
                        RootService.error(error.data);
                    });
                }

                function deleteFile(filename) {
                    if (!confirm("Are you sure you want to delete the file" + filename + "?")) {
                        return;
                    }
                    RootService.loading(true);
                    $http.delete('/data/delete?fileName=' + filename)
                        .then(function (response) {
                            RootService.success(response.data.message);
                            vm.listFileNames();
                        }).catch(function (error) {
                        console.error(error);
                        RootService.error(error.data);
                    })
                }

                function normalize() {
                    RootService.loading(true);
                    $http.post('/data/normalize')
                        .then(function (response) {
                            RootService.success(response.data.message);
                            vm.listFileNames();
                        }).catch(function (error) {
                        console.error(error);
                        RootService.error(error.data);
                    })
                }

                function unNormalize() {
                    RootService.loading(true);
                    $http.post('/data/normalize/undo')
                        .then(function (response) {
                            RootService.success(response.data.message);
                            vm.listFileNames();
                        }).catch(function (error) {
                        console.error(error);
                        RootService.error(error.data);
                    })
                }
            }]
    })
})();
