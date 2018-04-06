(function () {
    var app = angular.module('app');
    app.component('work', {
        templateUrl: './js/work.html',
        controller: ['RootService', 'StompClient', '$scope',
            function (RootService, StompClient, $scope) {
                var vm = this;
                vm.profile = RootService.profile();
                vm.time = 1;

                vm.$onInit = function () {
                    subscribe();
                }

                vm.$onDestroy = function () {
                    StompClient.unsubscribe('/reply/' + vm.profile);
                }

                function subscribe() {
                    var url = '/reply/' + vm.profile;
                    if (!StompClient.connected) {
                        console.log("Websocket not connected yet!");
                        vm.time *= 2;
                        if (vm.time >= 32) {
                            vm.time = 1;
                        }
                        console.log("Trying in " + vm.time + "seconds!");
                        setTimeout(subscribe, vm.time * 1000);
                        return;
                    }
                    StompClient.subscribe(url, function (reply) {
                        var msg = JSON.parse(reply.body);
                        $scope.$apply(function () {
                            var text = "".concat(msg.done, '/', msg.totalWork, ': ', msg.message);
                            RootService.loadingText(text);
                        });
                    });
                    console.log("Subscribed to '" + url + "' successfully!");
                }
            }
        ]
    })
})();
