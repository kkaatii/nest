import 'babel-polyfill'

import React from 'react'
import {render} from 'react-dom'
import {Provider} from 'react-redux'
import App from './containers/App'
import configureStore from './configureStore'
import 'bootstrap/dist/js/bootstrap.min'
import {INIT_STORE} from './constants'
require('../public/css/tube.css');

let store = configureStore(INIT_STORE);

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
);