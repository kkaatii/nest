import React, {PropTypes} from 'react'

const PageShader = ({displaying, hide}) => (displaying ?
  (<div style={{
    backgroundColor: "#aaaaaa", opacity: 0.5, zIndex: 1,
    position: 'fixed',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%'
  }} onClick={hide}/>)
  : null);

PageShader.propTypes = {
  displaying: PropTypes.bool.isRequired,
  hide: PropTypes.func.isRequired,
};

export default PageShader;