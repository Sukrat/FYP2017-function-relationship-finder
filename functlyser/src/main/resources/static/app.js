(function () {
    var app = angular.module('app', ['ngRoute', 'ngFileUpload', 'rx']);

    app.constant('$', window.$);
    app.constant('_', window._);
    app.constant('FileSaver', {saveAs: window.saveAs});

    app.config(($locationProvider, $routeProvider) => {
        $locationProvider.hashPrefix('!');
        $routeProvider
            .when('/root', {
                template: '<root></root>'
            })
            .otherwise('/root');
    });
})();