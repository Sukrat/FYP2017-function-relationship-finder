(function () {
    var app = angular.module('app', ['ngRoute', 'ngFileUpload', 'rx']);

    app.constant('$', window.$);
    app.constant('_', window._);
    app.constant('FileSaver', {
        saveAs: window.saveAs,
        saveResponseAsFile: function (response) {
            var blob = new Blob([response.data], {
                type: "text/plain;charset=utf-8"
            });
            var fileName = response.headers()['content-disposition'].split(';')[1].replace('filename="', '').replace('"', '');
            window.saveAs(blob, fileName);
        }
    });
    app.constant('BufferParser', new (function () {
        var vm = this;
        var textDecoder = new TextDecoder();
        vm.parse = function (buffer) {
            var decoded = textDecoder.decode(buffer);
            return JSON.parse(decoded);
        }
    })());

    app.constant('ErrorMessageHandler', new (function () {
        var vm = this;
        vm.getError = function (data) {
            if (data.messages) {
                return data.messages;
            } else if (data.message) {
                return [data.message];
            } else {
                return [data];
            }
        }
    })());

    app.config(($locationProvider, $routeProvider) => {
        $locationProvider.hashPrefix('!');
        $routeProvider
            .when('/root', {
                template: '<root></root>'
            })
            .otherwise('/root');
    });
})();