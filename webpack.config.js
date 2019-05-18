const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const HardSourceWebpackPlugin = require('hard-source-webpack-plugin');
const IgnoreNotFoundExportPlugin = require('ignore-not-found-export-plugin');

module.exports = {
    devtool: 'source-map',
    entry: [
        //'react-hot-loader/patch',
        './src/main/webapp/app/index'
    ],
    module: {
        rules: [
            {
                test: /\.(t|j)sx?$/,
                exclude: /node_modules/,
                use: {loader: 'babel-loader'},
            },
            {
                test: /\.html$/,
                exclude: /node_modules/,
                use: {loader: 'html-loader'},
            },
            {
                test: /\.png$/,
                loader: "url-loader?mimetype=image/png"
            }
        ],
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: './src/main/webapp/index.html',
        }),
        new HardSourceWebpackPlugin(),
        // required because of https://github.com/babel/babel/issues/7640
        new IgnoreNotFoundExportPlugin([
            'CallbackSideEffect',
            'NotificationSideEffect',
            'RedirectionSideEffect',
            'RefreshSideEffect',
        ]),
    ],
    resolve: {
        extensions: ['.ts', '.js', '.tsx', '.json'],
        /*alias: {
            'ra-core': path.join(
                __dirname,
                '..',
                '..',
                'packages',
                'ra-core',
                'src'
            ),
            'ra-ui-materialui': path.join(
                __dirname,
                '..',
                '..',
                'packages',
                'ra-ui-materialui',
                'src'
            ),
            'react-admin': path.join(
                __dirname,
                '..',
                '..',
                'packages',
                'react-admin',
                'src'
            ),
            'ra-data-fakerest': path.join(
                __dirname,
                '..',
                '..',
                'packages',
                'ra-data-fakerest',
                'src'
            ),
            'ra-input-rich-text': path.join(
                __dirname,
                '..',
                '..',
                'packages',
                'ra-input-rich-text',
                'src'
            ),
            'ra-tree-core': path.join(
                __dirname,
                '..',
                '..',
                'packages',
                'ra-tree-core',
                'src'
            ),
            'ra-tree-ui-materialui': path.join(
                __dirname,
                '..',
                '..',
                'packages',
                'ra-tree-ui-materialui',
                'src'
            ),
            'ra-tree-language-english': path.join(
                __dirname,
                '..',
                '..',
                'packages',
                'ra-tree-language-english'
            ),
            'ra-tree-language-french': path.join(
                __dirname,
                '..',
                '..',
                'packages',
                'ra-tree-language-french'
            ),
        },*/
    },
    devServer: {
        port: 8080,
        historyApiFallback: true,
        proxy: {
            '/libby/api': {
                target: 'http://localhost:8090/',
                pathRewrite: {'^/libby/api': ''}
            },
            '/oauth2': {
                target: 'http://localhost:8090/'
            }
        },
        stats: {
            children: false,
            chunks: false,
            modules: false,
        },
    },
};
