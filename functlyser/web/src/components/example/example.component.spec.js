import { MODULE_NAME } from '../../app';

describe('example.component', () => {

    let ctrl;

    beforeEach(angular.mock.module(MODULE_NAME));
    beforeEach(angular.mock.inject(($componentController) => {
        let bindings = {};
        ctrl = $componentController('example');
    }));

    it('on create should have a message', () => {
        expect(ctrl.heading).toEqual('Welcome to AngularJS ES6 Starter-Kit');
    });
});