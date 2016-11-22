import React, {PropTypes} from 'react'
import {displayCSS} from '../constants'

const Viewer = ({target, displaying, hide, chooseForEdit}) =>
  <div id="node-viewer" className={`container ${displayCSS(displaying, 'Viewer')}`}>
    <div className="viewer-topline">
      <div className="viewer-button-group">
        <span className="viewer-button pull-right glyphicon glyphicon-th" aria-hidden="true" onClick={hide}/>
        <span className="viewer-button pull-right glyphicon glyphicon-pencil" aria-hidden="true"
              style={{marginLeft: 12}}
              onClick={chooseForEdit}/>
      </div>
      <h3 className="viewer-node-name">{target.name}</h3>
    </div>
    <div dangerouslySetInnerHTML={{__html: target.content}}/>
  </div>;

Viewer.propTypes = {
  target: PropTypes.shape({
    id: PropTypes.number,
    content: PropTypes.string.isRequired,
    frame: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    updated: PropTypes.number,
    created: PropTypes.number,
    owner: PropTypes.string,
    digest: PropTypes.string,
    active: PropTypes.bool
  }),
  displaying: PropTypes.string.isRequired,
  hide: PropTypes.func.isRequired,
  chooseForEdit: PropTypes.func.isRequired,
};

export default Viewer;
