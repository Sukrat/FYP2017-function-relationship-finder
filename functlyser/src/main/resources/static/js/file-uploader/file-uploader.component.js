(function () {
    var app = angular.module('app');
    app.component('fileUploader', {
        templateUrl: './js/file-uploader/file-uploader.html',
        controller: ['FileUploaderService', 'RootService',
            function (FileUploaderService, RootService) {
                var vm = this;
                vm.upload = upload;
                vm.deleteFile = deleteFile;
                vm.list = list;
                list();

                vm.percentage = 0;
                vm.data = [];

                function upload() {
                    RootService.loading(true);
                    var file = document.getElementById('fileInput').files[0];
                    if (file == undefined) {
                        RootService.error(['Please select a file to be uploaded!']);
                        return;
                    }

                    FileUploaderService.uploadFile(file)
                        .then((data) => {
                            RootService.success(data.messages);
                            list();
                        }, (error) => {
                            RootService.error(error);
                        }, (progressPercentage) => {
                            vm.percentage = progressPercentage
                        });
                }

                function list() {
                    RootService.loading(true);
                    FileUploaderService.getFileNames()
                        .then((data) => {
                            RootService.loading(false);
                            vm.data = data;
                        }).catch((error) => {
                        RootService.error(error);
                    });
                }

                function deleteFile(filename) {
                    if (!confirm("Are you sure you want to delete the file" + filename + "?")) {
                        return;
                    }
                    RootService.loading(true);
                    FileUploaderService.deleteFile(filename)
                        .then((data) => {
                            RootService.success(data.messages);
                            list();
                        }).catch((error) => {
                        RootService.error(error);
                    })
                }
            }]
    })
})();
