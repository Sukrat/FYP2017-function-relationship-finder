(function () {
    var app = angular.module('app');
    app.component('work', {
        templateUrl: './js/work.html',
        controller: ['RootService',
            function (RootService) {
                var vm = this;
                vm.profile = RootService.profile();
            }
        ]
    })
})();
