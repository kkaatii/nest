var ArticleList = React.createClass({
    render: function () {
        var createItem = function (item) {
            var displayed = 8;
            var totalSize = item.content.ImageUrls.length;
            var arr = [];
            while (arr.length < displayed) {
                var r = Math.floor(Math.random() * totalSize);
                var found = false;
                for (var i = 0; i < arr.length; i++) {
                    if (arr[i] == r) {
                        found = true;
                        break
                    }
                }
                if (!found) arr.push(r);
            }
            var h = [];
            var picStyle = {
                margin: "0 auto",
                padding: "0.2em"
            };
            for (var i = 0; i < displayed; i++)
                h.push(<div className="row" title={item.content.Destination}><a href={item.content.ArticleUrl}
                                                                                target="_blank"><img
                    src={item.content.ImageUrls[arr[i]]} className="img-responsive"
                    style={picStyle}/></a></div>);
            return <div className="col-md-3 col-xs-6" key={item.content.Date} style={{marginTop: "-0.2em"}}>{h}</div>;
        };

        return <div className="row" style={{marginTop: 50}}>{this.props.panels.map(createItem)}</div>
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
                var panels_raw, panels = [];
                panels_raw = JSON.parse(xhr.responseText);
                console.log(panels_raw);
                for (var i = 0; i < batchSize; i++)
                    panels[i] = {content: JSON.parse(panels_raw[i].content)};
                self.setState({panels: panels, refreshing: false});
            }
        };
        xhr.send();
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
    <Body source="http://52.8.191.155/api/mfw/"/>,
    document.getElementById("content")
);