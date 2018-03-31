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
                        url: url('/upload'),
                        file: file
                    }).then(function (response) {
                        RootService.success(response.data.message);
                        vm.listFileNames();
                    }, RootService.error, function (notify) {
                        vm.percentage = parseInt(100.0 * notify.loaded / notify.total);
                    });
                }

                function listFileNames() {
                    RootService.loading(true);
                    $http.get(url("/filenames"))
                        .then(function (response) {
                            vm.data = response.data;
                            RootService.loading(false);
                        }).catch(RootService.error);
                }

                function deleteFile(filename) {
                    if (!confirm("Are you sure you want to delete the file" + filename + "?")) {
                        return;
                    }
                    RootService.loading(true);
                    $http.delete(url('/delete?fileName=' + filename))
                        .then(function (response) {
                            RootService.success(response.data.message);
                            vm.listFileNames();
                        }).catch(RootService.error)
                }

                function normalize() {
                    RootService.loading(true);
                    $http.post(url('/normalize'))
                        .then(function (response) {
                            RootService.success(response.data.message);
                            vm.listFileNames();
                        }).catch(RootService.error)
                }

                function unNormalize() {
                    RootService.loading(true);
                    $http.post(url('/normalize/undo'))
                        .then(function (response) {
                            RootService.success(response.data.message);
                            vm.listFileNames();
                        }).catch(RootService.error)
                }

                function url(task) {
                    return '/data/' + RootService.profile() + task;
                }
            }
        ]
    })
})();
