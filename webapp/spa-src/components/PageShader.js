import React, {PropTypes} from 'react'
import {displayCSS} from '../constants'

const PageShader = ({displaying, hide}) =>
  (
    <div style={{
      backgroundColor: "#aaaaaa", opacity: 0.5, zIndex: 1,
      position: 'fixed',
      top: 0,
      left: 0,
      width: '100%',
      height: '100%',
    }}
         className={displayCSS(displaying, 'Editor')}
         onClick={hide}/>);

PageShader.propTypes = {
  displaying: PropTypes.string.isRequired,
  hide: PropTypes.func.isRequired,
};

export default PageShader;