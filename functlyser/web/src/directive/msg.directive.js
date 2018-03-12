import template from './msg.html';
export default () => {
    return {
        restrict: 'E',
        template: template,
        scope: {
            msg: '=',
            loading: '='
        }
    }
}
