import $ from 'jquery';
import angular from 'angular';
import angularRoute from 'angular-route';
import 'ng-file-upload';

import example from './components/example/example.component';
import ExampleService from './components/example/example.service';

import fileUploader from './components/file-uploader/file-uploader.controller';
import FileUploaderService from './components/file-uploader/file-uploader.service';

export const MODULE_NAME = 'app';

let app = angular
    .module(MODULE_NAME, ['ngRoute', 'ngFileUpload']);

app.constant('jquery', $);

// similar way for services
app.service('ExampleService', ExampleService);
app.service('FileUploaderService', ['$http', '$q','Upload', FileUploaderService]);

// add components here
app.component('example', example);
app.component('fileUploader', fileUploader);



// routes config
app.config(($locationProvider, $routeProvider) => {
    $locationProvider.hashPrefix('!');

    $routeProvider
        .when('/example', {
            template: '<example></example>'
        })
        .when('/file', {
            template: '<file-uploader></file-uploader>'
        })
        .otherwise('/file');
});
