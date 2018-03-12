import template from './file-uploader.html';

class FileUploadController {

    constructor(fileUploaderService) {
        this.fileUploaderService = fileUploaderService;

        this.msg = {};
        this.loading = false;

        this.data = [];
        this.percentage = 0;

        this.list();
    }

    upload() {
        this.loading = true;
        let file = document.getElementById('fileInput').files[0];
        if(file == undefined){
            this.error(['Please select a file to be uploaded!']);
            return;
        }

        this.fileUploaderService.uploadFile(file)
            .then((data) => {
                this.success(data.messages);
                this.list();
            }, (error) => {
                this.error(error);
            }, (progressPercentage) => {
                this.percentage = progressPercentage
            });
    }

    delete(filename) {
        this.loading = true;
        this.fileUploaderService.deleteFile(filename)
            .then((data) => {
                this.success(data.messages);
                this.list();
            }).catch((error) => {
                this.error(error);
            })
    }

    list() {
        this.loading = true;
        this.fileUploaderService.getFileNames()
            .then((data) => {
                this.loading = false;
                this.data = data;
            }).catch((error) => {
                this.error(error);
            })
    }

    error(error) {
        this.loading = false;
        this.msg = {
            error: error
        }
    }

    success(success) {
        this.loading = false;
        this.msg = {
            success: success
        }
    }
}

export default {
    template: template,
    controller: ['FileUploaderService',
        FileUploadController]
};
