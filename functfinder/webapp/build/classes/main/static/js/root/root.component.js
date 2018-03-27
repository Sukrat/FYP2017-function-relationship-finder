(function () {
    var app = angular.module('app');
    app.component('root', {
        templateUrl: './js/root/root.html',
        controller: ['RootService', '$scope', 'StompClient',
            function (RootService, $scope, StompClient) {
                var vm = this;
                vm.message = {};
                vm.loading = false;
                vm.connected = false;

                StompClient.connect({}, function (frame) {
                    console.log('Connected: ' + frame);
                    $scope.$apply(function () {
                        vm.connected = true;
                    });
                    StompClient.subscribe('/reply/data', function (reply) {
                        var msg = JSON.parse(reply.body);
                        console.log(msg);
                        $scope.$apply(function () {
                            vm.loadingText = "".concat(msg.done, '/', msg.totalWork, ': ', msg.message);
                        });
                    });
                }, function (message) {
                    console.log('Connection Lost: ' + message);
                    $scope.$apply(function () {
                        vm.connected = false;
                    });
                });

                RootService.subscribeMessage(function (message) {
                    vm.message = message;
                });

                RootService.subscribeLoading(function (loading) {
                    if (loading) {
                        vm.loadingText = "Loading...";
                    }
                    vm.loading = loading;
                });
            }]
    })
})();