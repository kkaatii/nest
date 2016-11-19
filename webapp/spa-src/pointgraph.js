import React from 'react';

class PointGraph extends React.Component {
  constructor(props) {
    super(props);
    this.API_URL = this.props.apiUrl;
    this.displayEditorFor = this.displayEditorFor.bind(this);
    this.renderSinglePoint = this.renderSinglePoint.bind(this);
  }

  displayEditorFor(id) {
    return () => this.props.chooseNodeForEdit(id);
  }

  renderSinglePoint(point) {
    return <a className="point-wrapper-a" onClick={this.displayEditorFor(point.id)} key={point.id} ><div className="point-wrapper">
      <h4>{point.name}</h4>
      <div className="node-digest-text">{point.digest}</div>
    </div></a>
  }

  render() {
    return <div className="container-fluid">
      <div className="row">
        <div className="col-lg-4">
          {this.props.points.map(this.renderSinglePoint)}
        </div>
      </div>
    </div>;
  }
}

export default PointGraph;
