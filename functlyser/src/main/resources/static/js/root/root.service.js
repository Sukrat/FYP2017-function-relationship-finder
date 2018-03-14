(function () {
    var app = angular.module('app');
    app.service('RootService', ['rx', '_', function (rx, _) {
        var vm = this;
        vm.messageSubject = new rx.Subject();
        vm.loadingSubject = new rx.Subject();
        vm.subscribeMessage = subscribeMessage;
        vm.subscribeLoading = subscribeLoading;
        vm.error = error;
        vm.success = success;
        vm.loading = loading;

        function subscribeMessage(onNext) {
            return vm.messageSubject.subscribe({next: onNext});
        }

        function subscribeLoading(onNext) {
            return vm.loadingSubject.subscribe({next: onNext});
        }

        function error(errors) {
            vm.loading(false);
            _.forEach(vm.messageSubject.observers, function (observer) {
                observer.next({
                    error: errors
                });
            });
        }

        function success(messages) {
            vm.loading(false);
            _.forEach(vm.messageSubject.observers, function (observer) {
                observer.next({
                    success: messages
                });
            });
        }

        function loading(isOn) {
            _.forEach(vm.loadingSubject.observers, function (observer) {
                observer.next(isOn);
            });
        }
    }])
})();