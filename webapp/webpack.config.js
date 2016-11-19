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
  entry: paths.appIndexJs,
  output: {
    path: 'public',
    pathinfo: true,
    filename: 'js/tube.js',
  },
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

  plugins: [
    new ProgressPlugin(function (percentage, msg) {
      console.log((percentage * 100) + '%', msg);
    }),
    new webpack.ProvidePlugin({
      $: "jquery",
      jQuery: "jquery"
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