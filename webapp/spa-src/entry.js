import 'babel-polyfill'
import 'bootstrap/dist/js/bootstrap.min'
import '../public/css/style.css'
import '../public/css/tube.css'

import React from 'react'
import {render} from 'react-dom'
import {Provider} from 'react-redux'
import App from './containers/App'
import configureStore from './configureStore'
import {INIT_STORE} from './constants'

let store = configureStore(INIT_STORE);

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
);