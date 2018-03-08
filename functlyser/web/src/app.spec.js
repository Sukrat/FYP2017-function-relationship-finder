import { MODULE_NAME } from './app';

describe('app', () => {

    let route,
        location,
        rootScope;

    beforeEach(angular.mock.module(MODULE_NAME));
    beforeEach(angular.mock.inject(($route, $location, $rootScope) => {
        route = $route;
        location = $location;
        rootScope = $rootScope;
    }));

    it('default routing at the start', () => {
        location.path('/');
        rootScope.$digest();

        expect(location.path()).toBe('/')
    });

    it('routing to random value should go back to home', () => {
        location.path('/randomvalue');
        rootScope.$digest();

        expect(location.path()).toBe('/')
    });
});