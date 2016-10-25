var ArticleList = React.createClass({
  render: function () {
    var self = this;
    var createItem = function (item) {
      if (item === null) return;
      var h = [];
      var picStyle = {
        margin: "0 auto",
        padding: "0.2em"
      };
      for (var i = 0; i < item.displayedPos.length; i++)
        h.push(<div className="row" title={item.content.Destination}><a
          href={self.complementURL(item.content.ArticleId)}
          target="_blank"><img
          src={item.content.ImageUrls[item.displayedPos[i]]} className="img-responsive"
          style={picStyle}/></a></div>);
      return <div className="col-md-6 col-xs-12" key={item.content.ArticleId}
                  style={{marginTop: "-0.2em"}}>{h}</div>;
    };
    var createPair = function (panelpair) {
      return <div className="col-md-6 col-xs-6"><div className="row">{createItem(panelpair[0])}{createItem(panelpair[1])}</div></div>;
    };

    return <div className="row" style={{marginTop: 50}}>{this.props.panels.map(createPair)}</div>
  },

  complementURL: function (articleId) {
    return "http://www.mafengwo.cn/i/" + articleId + ".html";
  }
});

var Body = React.createClass({
  getInitialState: function () {
    return {
      refreshing: false,
      batchSize: 4,
      panels: []
    }
  },

  handleChange: function (event) {
    this.setState({query: event.target.value})
  },

  componentDidMount: function () {
    var self = this;
    var xhr = new XMLHttpRequest();
    xhr.open('GET', this.props.source + 'init', true);
    xhr.onload = function () {
      if (xhr.status === 200) {
        console.log("Successful init");
        self.nextBatch();
      }
    };
    xhr.send();
  },

  nextBatch: function () {
    this.setState({refreshing: true});
    var self = this;
    var xhr = new XMLHttpRequest();
    var batchSize = this.state.batchSize;
    xhr.open('GET', this.props.source + '?batchSize=' + batchSize, true);
    xhr.onload = function () {
      if (xhr.status === 200) {
        var panels_raw, panelpairs = [];
        panels_raw = JSON.parse(xhr.responseText);
        console.log(panels_raw);
        var content;
        for (var i = 0; i < (batchSize - 1) / 2; i++) {
          panelpairs[i] = [];
          content = JSON.parse(panels_raw[2 * i].content);
          panelpairs[i][0] = {content: content, displayedPos: self.randomPos(content.ImageUrls.length, 4)};
          content = JSON.parse(panels_raw[2 * i + 1].content);
          panelpairs[i][1] = {content: content, displayedPos: self.randomPos(content.ImageUrls.length, 4)};
        }
        if (batchSize > i * 2) {
          panelpairs[i] = [];
          content = JSON.parse(panels_raw[2 * i].content);
          panelpairs[i][0] = {content: content, displayedPos: self.randomPos(content.ImageUrls.length, 4)};
          panelpairs[i][1] = null;
        }
        self.setState({panels: panelpairs, refreshing: false});
      }
    };
    xhr.send();
  },

  randomPos: function (size, displayedCount) {
    var arr = [];
    while (arr.length < displayedCount) {
      var r = Math.floor(Math.random() * size);
      var found = false;
      for (var i = 0; i < arr.length; i++) {
        if (arr[i] == r) {
          found = true;
          break
        }
      }
      if (!found) arr.push(r);
    }
    return arr;
  },

  render: function () {
    return (
      <div className="container-fluid" style={{marginLeft: "1em", marginRight: "1em"}}>
        <div className="row" style={{
          position: 'fixed',
          zIndex: 1,
          height: 50,
          backgroundColor: '#fff',
          width: '100%',
          paddingLeft: "0.2em"
        }}>
          <button className="btn btn-primary" onClick={this.nextBatch} disabled={this.state.refreshing}
                  style={{position: "absolute", top: "50%", transform: "translateY(-50%)"}}>
            { this.state.refreshing ? 'Refreshing...' : 'Refresh'}
          </button>
        </div>
        <ArticleList panels={this.state.panels}/>
      </div>
    )
  }
});

ReactDOM.render(
  <Body source="https://www.artificy.com/api/mfw/"/>,
  document.getElementById("content")
);