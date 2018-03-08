import template from './example.html';

class ExampleController {

    constructor(exampleService) {
        this.heading = 'Welcome to AngularJS ES6 Starter-Kit';
        this.exampleService = exampleService;
    }

    submit() {
        this.heading = this.exampleService.getHeading();
    }
}

export default {
    template: template,
    controller: ['ExampleService', ExampleController],
    controllerAs: 'exampleCtrl'
};
