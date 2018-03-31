(function () {
    var app = angular.module('app');
    app.component('profile', {
        templateUrl: './js/profile/profile.html',
        controller: ['RootService', '$http',
            function (RootService, $http) {
                var vm = this;
                vm.deleteProfile = deleteProfile;
                vm.listProfile = listProfile;
                vm.createProfile = createProfile;

                vm.data = [];
                vm.profileName = "";

                vm.listProfile();

                function listProfile() {
                    RootService.loading(true);
                    $http.get('/profile/list')
                        .then(function (response) {
                            vm.data = response.data;
                            RootService.loading(false);
                        })
                        .catch(RootService.error)
                }

                function deleteProfile(profile) {
                    RootService.loading(true);
                    if (!confirm("Are you sure you want to delete the profile '" + profile + "'?")) {
                        return;
                    }
                    $http.delete('/profile/delete', {data: profile})
                        .then(function (response) {
                            RootService.success(response);
                            vm.listProfile();
                        }).catch(RootService.error)
                }

                function createProfile(profile) {
                    RootService.loading(true);
                    if (profile.length == 0) {
                        RootService.error("Please enter valid profile!");
                        return;
                    }
                    $http.post('/profile/create', profile)
                        .then(function (response) {
                            RootService.success(response);
                            vm.listProfile();
                        }).catch(RootService.error)
                }
            }]
    })
})();
