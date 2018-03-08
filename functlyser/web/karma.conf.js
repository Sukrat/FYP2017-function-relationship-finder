module.exports = (config) => {
    config.set({
        frameworks: ['jasmine'],
        reporters: ['progress'],
        files: [
            './src/test.webpack.js'
        ],

        preprocessors: {
            './src/test.webpack.js': ['webpack', 'sourcemap']
        },

        browsers: ['Chrome'],

        webpack: require('./webpack.config'),

        webpackMiddleware: {
            noInfo: 'errors-only'
        }
    })
}