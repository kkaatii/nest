import React, {PropTypes} from 'react';

class Graph extends React.Component {
  constructor(props) {
    super(props);
    this.displayViewerFor = this.displayViewerFor.bind(this);
    this.renderSinglePoint = this.renderSinglePoint.bind(this);
  }

  displayViewerFor(id) {
    return () => this.props.chooseNodeForView(id);
  }

  renderSinglePoint(point) {
    return <div key={`KEY${point.id}${point.digest}`} className="col-lg-3 col-md-4 col-sm-6">
      <a className="point-wrapper-a" onClick={this.displayViewerFor(point.id)}>
        <div className="point-wrapper">
          <h4>{point.name}</h4>
          <p className="point-digest-text">{point.digest}</p>
        </div>
      </a>
    </div>;
  }

  render() {
    const {pointMap} = this.props.graph;
    return (
      <div className="container-fluid graph" style={{visibility: this.props.displaying ? 'visible' : 'hidden'}}>
        <div className="row">
          {Object.keys(pointMap).map(key => pointMap[key]).map(this.renderSinglePoint)}
        </div>
      </div>
    );
  }
}

Graph.propTypes = {
  graph: PropTypes.shape({
    pointMap: PropTypes.object.isRequired,
  }),
  chooseNodeForView: PropTypes.func.isRequired,
  displaying: PropTypes.bool.isRequired,
};

export default Graph;
