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

        function error(error) {
            vm.loading(false);
            var errorMsg = "";
            if (error && error.data && error.data.message) {
                errorMsg = error.data.message;
            } else if (error && error.message) {
                errorMsg = error.message;
            } else {
                errorMsg = error;
            }
            _.forEach(vm.messageSubject.observers, function (observer) {
                observer.next({
                    error: errorMsg
                });
            });
        }

        function success(response) {
            vm.loading(false);
            var message;
            if (response && response.data && response.data.message) {
                message = response.data.message;
            } else if (response && response.message) {
                message = response.message;
            } else {
                message = response;
            }
            _.forEach(vm.messageSubject.observers, function (observer) {
                observer.next({
                    success: message
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