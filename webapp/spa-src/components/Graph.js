import React, {PropTypes} from 'react';

class Graph extends React.Component {
  constructor(props) {
    super(props);
    this.displayEditorFor = this.displayEditorFor.bind(this);
    this.renderSinglePoint = this.renderSinglePoint.bind(this);
  }

  displayEditorFor(id) {
    return () => this.props.chooseNodeForEdit(id);
  }

  renderSinglePoint(point) {
    return <div key={`KEY${point.id}${point.digest}`} className="col-lg-4">
      <a className="point-wrapper-a" onClick={this.displayEditorFor(point.id)}>
        <div className="point-wrapper">
          <h4>{point.name}</h4>
          <div className="node-digest-text">{point.digest}</div>
        </div>
      </a>
    </div>;
  }

  render() {
    const {pointMap} = this.props.graph;
    return <div className="container-fluid">
      <div className="row">
          {Object.keys(pointMap).map(key => pointMap[key]).map(this.renderSinglePoint)}
      </div>
    </div>;
  }
}

Graph.propTypes = {
  graph: PropTypes.shape({
    pointMap: PropTypes.object.isRequired,
  }),
  chooseNodeForEdit: PropTypes.func.isRequired,
};

export default Graph;
