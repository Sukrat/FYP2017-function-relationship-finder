import 'lodash';
import $ from 'jquery';
import angular from 'angular';
import angularRoute from 'angular-route';
import 'ng-file-upload';

import root from './components/root/root.controller';
import RootService from './components/root/root.service';

import fileUploader from './components/file-uploader/file-uploader.controller';
import FileUploaderService from './components/file-uploader/file-uploader.service';

import gridAnalysis from './components/grid-analysis/grid-analysis.controller';
import GridAnalysisService from './components/grid-analysis/grid-analysis.service';

import msgDirective from './directive/msg.directive';

export const MODULE_NAME = 'app';

let app = angular
    .module(MODULE_NAME, ['ngRoute', 'ngFileUpload']);

app.constant('jquery', $);
app.constant('lodash', _);

// similar way for services
app.service('RootService', [RootService]);
app.service('FileUploaderService', ['$http', '$q', 'Upload', FileUploaderService]);
app.service('GridAnalysisService', ['$http', '$q', 'lodash', GridAnalysisService]);

// add components here
app.component('root', root);
app.component('fileUploader', fileUploader);
app.component('gridAnalysis', gridAnalysis);

// directives
app.directive('msg', msgDirective);


// routes config
app.config(($locationProvider, $routeProvider) => {
    $locationProvider.hashPrefix('!');

    $routeProvider
        .when('/root', {
            template: '<root></root>'
        })
        .otherwise('/root');
});
