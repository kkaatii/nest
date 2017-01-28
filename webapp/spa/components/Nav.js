import React, {PropTypes} from 'react'
import {VIEW_CHOICES} from '../constants'

const Nav = ({view, editorButton, graphButton, displaying, switchView}) => {
  let renderViewChoices = (view) => (
    <li key={view}><a onClick={switchView(view)}>{view}</a></li>
  );
  return (
    <div className="container-fluid tube-nav">
      <a /*onClick={this.toggleEditorDisplay}*/>
        <img src="/img/logo.png" alt="Artificy" height={38} style={{margin: "6px 0 6px"}}/>
      </a>
      <div className="btn btn-default pull-right nav-btn" onClick={graphButton}>
        <span className={`glyphicon ${displaying === '' ? 'glyphicon-refresh' : 'glyphicon-th'}`} aria-hidden="true"/>
      </div>
      <div className={`btn btn-default pull-right nav-btn`}
           onClick={editorButton}>
        <span className={`glyphicon ${displaying !== 'Viewer' ? 'glyphicon-plus' : 'glyphicon-pencil'}`}
              aria-hidden="true"/>
      </div>
      <div className="dropdown pull-right" style={{display: 'inline-block'}}>
        <button type="button" className="btn btn-default dropdown-toggle nav-btn"
                data-toggle="dropdown"
                aria-haspopup="true" aria-expanded="false"
                id="node-frame-select" style={{textAlign: "left"}}>
          {view + ' '}<span className="caret"/></button>
        <ul aria-labelledby="node-frame-select" className="dropdown-menu">{VIEW_CHOICES.map(renderViewChoices)}</ul>
      </div>
    </div>
  )
};

Nav.propTypes = {
  view: PropTypes.string.isRequired,
  switchView: PropTypes.func.isRequired,
  editorButton: PropTypes.func.isRequired,
  graphButton: PropTypes.func.isRequired,
  displaying: PropTypes.string.isRequired,
};

export default Nav;