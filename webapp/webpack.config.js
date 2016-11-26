/**
 * Created by dan on 19/11/2016.
 */
var ProgressPlugin = require('webpack/lib/ProgressPlugin');

var path = require('path');
var fs = require('fs');
var webpack = require('webpack');

var appDirectory = fs.realpathSync(process.cwd());
function resolveApp(relativePath) {
  return path.resolve(appDirectory, relativePath);
}

var nodePaths = (process.env.NODE_PATH || '')
  .split(process.platform === 'win32' ? ';' : ':')
  .filter(Boolean)
  .map(resolveApp);

var paths = {
  appBuild: resolveApp('dist'),
  appPublic: resolveApp('public'),
  appIndexJs: resolveApp('spa-src/entry.js'),
  appPackageJson: resolveApp('package.json'),
  appSrc: resolveApp('spa-src'),
  appNodeModules: resolveApp('node_modules'),
  ownNodeModules: resolveApp('node_modules'),
  nodePaths: nodePaths
};

module.exports = {
  entry: ['babel-polyfill', paths.appIndexJs],
  output: {
    path: 'public',
    pathinfo: true,
    filename: 'js/tube.js',
  },
  externals: { 'tinymce':'tinymce' },
  module: {
    // First, run the linter.
    // It's important to do this before Babel processes the JS.
    preLoaders: [
      {
        test: /\.(js|jsx)$/,
        loader: 'eslint',
        include: paths.appSrc
      }
    ],
    loaders: [
      // Process JS with Babel.
      {
        test: /\.(js|jsx)$/,
        include: paths.appSrc,
        loader: 'babel-loader'
      },
      {
        test: /\.css$/,
        include: paths.appPublic,
        loader: 'style!css?importLoaders=1'
      },
      {
        test: require.resolve('tinymce/tinymce'),
        loaders: [
          'imports?this=>window',
          'exports?window.tinymce'
        ]
      },
      {
        test: /tinymce\/(themes|plugins)\//,
        loaders: [
          'imports?this=>window'
        ]
      }
    ]
  },
  eslint: {
    configFile: path.join(__dirname, '.eslintrc'),
    useEslintrc: false
  },
  resolve: {
    extensions: ['', '.js', '.json']
  },

  //devServer: { inline: true },
  plugins: [
    /*new ProgressPlugin(function (percentage, msg) {
      console.log((percentage * 100) + '%', msg);
    }),*/
    new webpack.ProvidePlugin({
      $: "jquery",
      jQuery: "jquery",
      Promise: 'promise', // Thanks Aaron (https://gist.github.com/Couto/b29676dd1ab8714a818f#gistcomment-1584602)
      fetch: 'imports?this=>global!exports?global.fetch!whatwg-fetch'
    }),
    function () {
      this.plugin("done", function (stats) {
        if (stats.compilation.errors && stats.compilation.errors.length) {
          console.log(stats.compilation.errors);
          process.exit(1);
        }
      });
    }
  ]
};