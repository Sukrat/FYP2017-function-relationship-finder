
export default class FileUploaderService {

    constructor($http, $q, fileupload) {
        this.$http = $http;
        this.$q = $q;
        this.fileupload = fileupload;
    }

    uploadFile(file) {
        var deferred = this.$q.defer();
        this.fileupload.upload({
            url: '/data/upload',
            file: file
        }).then((response) => {
            console.log(response);
            deferred.resolve(response.data);
        }, (error) => {
            console.log(error);
            deferred.reject(error.data.messages);
        }, (evt) => {
            var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            deferred.notify(progressPercentage);
        });
        return deferred.promise;        
    }

    deleteFile(filename) {
        return this.$http.delete("/data/delete?filename=" + filename)
            .then((response) => {
                return response.data;
            })
            .catch((error) => {
                console.log(error);
                return this.$q.reject(error.data.messages);
            })
    }

    listExcelFile() {
        return this.$http.get("/data/listexcel")
            .then((response) => {
                return response.data;
            }).catch((error) => {
                console.log(error);
                return this.$q.reject(error.data.messages);
            })
    }
}
