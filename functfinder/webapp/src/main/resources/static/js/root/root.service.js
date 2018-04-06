(function () {
    var app = angular.module('app');
    app.service('RootService', ['rx', '_', '$routeParams',
        function (rx, _, $routeParams) {
            var vm = this;
            vm.messageSubject = new rx.Subject();
            vm.loadingSubject = new rx.Subject();
            vm.loadingTextSubject = new rx.Subject();

            vm.subscribeMessage = subscribeMessage;
            vm.subscribeLoading = subscribeLoading;
            vm.subscribeLoadingText = subscribeLoadingText;

            vm.error = error;
            vm.success = success;
            vm.loading = loading;
            vm.loadingText = loadingText;

            vm.profile = profile;
            vm.textDecoder = new TextDecoder();

            function subscribeMessage(onNext) {
                return vm.messageSubject.subscribe({next: onNext});
            }

            function subscribeLoading(onNext) {
                return vm.loadingSubject.subscribe({next: onNext});
            }

            function subscribeLoadingText(onNext) {
                return vm.loadingTextSubject.subscribe({next: onNext});
            }

            function error(error) {
                vm.loading(false);
                var errorMsg = "";
                if (error && (error.data instanceof ArrayBuffer)) {
                    var decoded = vm.textDecoder.decode(error.data);
                    error = JSON.parse(decoded);
                }
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

            function loadingText(msg) {
                _.forEach(vm.loadingTextSubject.observers, function (observer) {
                    observer.next(msg);
                });
            }

            function profile() {
                return $routeParams.profile;
            }
        }])
})();