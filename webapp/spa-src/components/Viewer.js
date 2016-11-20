import React, {PropTypes} from 'react'

const Viewer = ({target, displaying, hide, chooseForEdit}) => (
  displaying
    ?
    <div id="node-viewer" className="container-fluid">
      <div style={{display: 'block', marginBottom: 30}}>
        <span className="viewer-button pull-right glyphicon glyphicon-remove" aria-hidden="true" onClick={hide}/>
        <span className="viewer-button pull-left glyphicon glyphicon-pencil" aria-hidden="true"
              onClick={chooseForEdit}/>
      </div>
      <h3>{target.name}</h3>
      <br/>
      <div dangerouslySetInnerHTML={{__html: target.content}}/>
    </div>
    :
    null
);

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
  displaying: PropTypes.bool.isRequired,
  hide: PropTypes.func.isRequired,
  chooseForEdit: PropTypes.func.isRequired,
};

export default Viewer;
