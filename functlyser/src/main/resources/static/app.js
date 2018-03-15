(function () {
    var app = angular.module('app', ['ngRoute', 'ngFileUpload', 'rx']);

    app.constant('$', window.$);
    app.constant('_', window._);
    app.constant('FileSaver', {saveAs: window.saveAs});
    app.constant('BufferParser', new (function () {
        var vm = this;
        var textDecoder = new TextDecoder();
        vm.parse = function (buffer) {
            var decoded = textDecoder.decode(buffer);
            return JSON.parse(decoded);
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