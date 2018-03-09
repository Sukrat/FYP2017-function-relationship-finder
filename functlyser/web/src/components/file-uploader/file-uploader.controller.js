import template from './file-uploader.html';

class FileUploadController {

    constructor(fileUploaderService) {
        this.fileUploaderService = fileUploaderService;

        this.msg = {};
        this.loading = false;
        this.data = [];
        this.percentage = 0;

        this.loadExcelTable();
    }

    upload() {
        this.loading = true;
        let file = document.getElementById('fileInput').files[0];

        this.fileUploaderService.uploadFile(file)
            .then((data) => {
                this.success(data.messages);
                this.loadExcelTable();
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
                this.loadExcelTable();
            }).catch((error) => {
                this.error(error);
            })
    }

    loadExcelTable() {
        this.loading = true;
        this.fileUploaderService.listExcelFile()
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
