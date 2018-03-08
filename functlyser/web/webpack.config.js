const path = require('path');
const HtmllWebPackPlugin = require('html-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');


module.exports = {
    entry: {
        app: './src/app.js',
        css: './src/css.js'
    },
    output: {
        filename: '[name].bundle.js',
        path: path.resolve(__dirname, '../src/main/resources/static')
    },
    devtool: 'inline-source-map',
    devServer: {
        contentBase: './dist'
    },
    plugins: [
        new CleanWebpackPlugin([path.resolve(__dirname, '../src/main/resources/static')]),
        new HtmllWebPackPlugin({
            template: './index.html'
        })
    ],
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /(node_modules|bower_components)/,
                use: {
                    loader: 'babel-loader'
                }
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            },
            {
                test: /\.(png|svg|jpg|gif|ico)$/,
                use: ['file-loader']
            },
            {
                test: /\.(html)$/,
                exclude: /index.html/,
                use: {
                    // loader: 'file-loader?name=[path][name].[ext]!extract-loader!html-loader',
                    loader: 'raw-loader',
                    options: {
                        attrs: [':src']
                    }
                }
            }
        ]
    }
};