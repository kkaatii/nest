var REMOTE_SERVER = document.getElementById('api').getAttribute('server');
var MFW_API_URL = REMOTE_SERVER + '/api/mfw';

var ArticleList = React.createClass({
  getInitialState: function () {
    return {
      starred: {},
      noshowed: {}
    }
  },

  render: function () {
    var self = this;
    var createItem = function (item) {
      if (item === null) return;
      var h = [];
      var picStyle = {
        margin: "0 auto",
        padding: "0.2em"
      };
      var glyphStyle = {
        star: {
          color: self.state.starred[item.content.ArticleId] ? "#ffbb00" : "#666666"
        },
        remove: {
          color: self.state.noshowed[item.content.ArticleId] ? "#bd0000" : "#666666"
        }
      };
      for (var i = 0; i < item.displayedPos.length; i++)
        h.push(<div className="row" title={item.content.Destination}><a
          href={self.complementURL(item.content.ArticleId)}
          target="_blank"><img
          src={item.content.ImageUrls[item.displayedPos[i]]} className="img-responsive"
          style={picStyle}/></a></div>);
      h.push(
        <div className="row" style={{
          fontSize: "2.1em",
          marginTop: "0.25em",
          marginBottom: "0.6em",
          paddingLeft: "20%",
          paddingRight: "20%"
        }}>
          <span
            className={"pull-left glyphicon glyphicon-star" + (self.state.starred[item.content.ArticleId] ? "" : "-empty") }
            aria-hidden="true" onClick={self.star(item.content.ArticleId)} style={glyphStyle.star}/>
          <span className="pull-right glyphicon glyphicon-remove"
                aria-hidden="true" onClick={self.noshow(item.content.ArticleId)}
                style={glyphStyle.remove}/>
        </div>
      );
      return <div className="col-md-6 col-xs-12" key={item.content.ArticleId}>{h}</div>;
    };
    var createPair = function (panelpair) {
      return <div className="col-md-6 col-xs-6">
        <div className="row">{createItem(panelpair[0])}{createItem(panelpair[1])}</div>
      </div>;
    };

    return <div className="row" style={{marginBottom: "3.6em"}}>{this.props.panels.map(createPair)}</div>
  },

  complementURL: function (articleId) {
    return "http://www.mafengwo.cn/i/" + articleId + ".html";
  },
  noshow: function (articleId) {
    var self = this;
    return function () {
      var xhr = new XMLHttpRequest();
      xhr.open('POST', MFW_API_URL + '/noshow?articleId=' + articleId, true);
      xhr.send();
      var noshowed = self.state.noshowed;
      noshowed[articleId] = true;
      self.setState({noshowed: noshowed});
    }
  },
  star: function (articleId) {
    var self = this;
    return function () {
      var xhr = new XMLHttpRequest();
      xhr.open('POST', MFW_API_URL + '/star?articleId=' + articleId, true);
      xhr.send();
      var starred = self.state.starred;
      starred[articleId] = true;
      self.setState({starred: starred});
    }
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

  componentDidMount: function () {
    /*var self = this;
     var xhr = new XMLHttpRequest();
     xhr.open('GET', this.props.source + 'init', true);
     xhr.onload = function () {
     if (xhr.status === 200) {
     console.log("Successful init");
     self.nextBatch();
     }
     };
     xhr.send();*/
    this.nextBatch();
  },

  nextBatch: function () {
    this.setState({refreshing: true});
    var self = this;
    var xhr = new XMLHttpRequest();
    var batchSize = this.state.batchSize;
    xhr.open('GET', MFW_API_URL + '/?batchSize=' + batchSize, true);
    xhr.onload = function () {
      if (xhr.status === 200) {
        if (xhr.responseText.startsWith('<')) window.location = REMOTE_SERVER + '/login';
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
      <div>
        <nav className="navbar navbar-inverse navbar-fixed-bottom" role="navigation">
          <div className="container-fluid" style={{marginLeft: "1em", marginRight: "1em"}}>
            <div className="text-center">
              <button className="btn btn-danger navbar-btn" onClick={this.nextBatch}
                      disabled={this.state.refreshing} style={{width: "12em"}}>
                { this.state.refreshing ? 'Refreshing...' : 'Refresh'}
              </button>
            </div>
          </div>
        </nav>
        <div className="container-fluid" style={{marginLeft: "1em", marginRight: "1em"}}>
          <ArticleList panels={this.state.panels}/>
        </div>
      </div>
    )
  }
});

ReactDOM.render(
  <Body />,
  document.getElementById("content")
);