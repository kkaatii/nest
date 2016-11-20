import 'babel-polyfill'

import React from 'react'
import {render} from 'react-dom'
import {Provider} from 'react-redux'
import App from './containers/App'
import configureStore from './configureStore'
require('../public/css/tube.css');

let store = configureStore({
  graph: {pointMap: {}},
  editor: {
    fetching: false,
    frameChoices: ["\<Private\>", "BeTrue@Dun", "Ohters@Dun", "Nonsense@John Doe"],
    target: null,
  }
});

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
);