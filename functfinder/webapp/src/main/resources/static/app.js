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

    app.constant('StompClient', (function () {
        var socket = new SockJS('/functfinder-websocket');
        return Stomp.over(socket);
    })());


    app.config(function ($locationProvider, $routeProvider) {
        $locationProvider.hashPrefix('!');
        $routeProvider
            .when('/profile', {
                template: '<profile></profile>'
            })
            .when('/work/:profile', {
                template: '<work></work>'
            }).otherwise('/profile');
    });
})();