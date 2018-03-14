(function () {
    var app = angular.module('app');
    app.component('fileUploader', {
        templateUrl: '/js/file-uploader/file-uploader.html',
        controller: ['FileUploaderService', function (FileUploaderService) {
            var vm = this;
            vm.upload = upload;
            vm.list = list;
            vm.deleteFile = deleteFile;

            function upload() {
                vm.loading = true;
                var file = document.getElementById('fileInput').files[0];
                if (file == undefined) {
                    vm.error(['Please select a file to be uploaded!']);
                    return;
                }

                FileUploaderService.uploadFile(file)
                    .then((data) => {
                        vm.success(data.messages);
                        vm.list();
                    }, (error) => {
                        vm.error(error);
                    }, (progressPercentage) => {
                        vm.percentage = progressPercentage
                    });
            }

            function list() {
                this.loading = true;
                FileUploaderService.getFileNames()
                    .then((data) => {
                        this.loading = false;
                        this.data = data;
                    }).catch((error) => {
                    vm.error(error);
                })
            }

            function deleteFile(filename)
            {
                vm.loading = true;
                FileUploaderService.deleteFile(filename)
                    .then((data) => {
                        vm.success(data.messages);
                        vm.list();
                    }).catch((error) => {
                    vm.error(error);
                })
            }

        }]
    })
})();
