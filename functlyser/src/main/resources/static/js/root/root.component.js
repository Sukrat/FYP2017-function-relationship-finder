(function () {
    var app = angular.module('app');
    app.component('root', {
        templateUrl: './js/root/root.html',
        controller: ['RootService', '$scope', 'observeOnScope', function (RootService, $scope, observeOnScope) {
            var vm = this;
            vm.heading = 'hello';
            vm.message = {};
            vm.loading = false;

            RootService.subscribeMessage(function (message) {
                $scope.$apply(function () {
                    vm.message = message;
                });
            });

            RootService.subscribeLoading(function (loading) {
                $scope.$apply(function () {
                    vm.loading = loading;
                });
            });
        }]
    })
})();