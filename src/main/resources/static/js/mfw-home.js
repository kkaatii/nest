var ArticleList = React.createClass({
    render: function () {
        var createItem = function (item) {
            var displayed = 8;
            var denominator = Math.floor(item.content.ImageUrls.length / displayed), h = [];
            var picStyle = {
                maxWidth: '94%',
                marginTop: 8
            };
            for (var i = 0; i < displayed; i++)
                h.push(<div className="row" title={item.content.Destination}><a href={item.content.ArticleUrl}
                                               target="_blank"><img
                    src={item.content.ImageUrls[denominator * i]} className="img-responsive"
                    style={picStyle}/></a></div>);
            return <div className="col-md-3" key={item.content.Created}>{h}</div>;
        };
        return <div className="row" style={{marginTop: 50}}>{this.props.panels.map(createItem)}</div>
    }
});

var Body = React.createClass({
    getInitialState: function () {
        return {
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

    nextBatch1: function () {
        this.setState({panels: JSON.parse('[{"content" : "{\\"Destination\\":\\"东京\\"}"}, {"content" : "{\\"Destination\\":\\"北京\\"}"}]')});
    },
    nextBatch: function () {
        var self = this;
        var xhr = new XMLHttpRequest();
        var batchSize = this.state.batchSize;
        xhr.open('GET', this.props.source + '?batchSize=' + batchSize, true);
        xhr.onload = function () {
            if (xhr.status === 200) {
                var panels_raw, panels = [];
                panels_raw = JSON.parse(xhr.responseText);
                for (var i = 0; i < batchSize; i++)
                    panels[i] = {content: JSON.parse(panels_raw[i].content)};
                self.setState({panels: panels});
            }
        };
        xhr.send();
    },

    render: function () {
        return (
            <div className="container">
                <div className="row" style={{
                    position: 'fixed',
                    zIndex: 1,
                    height: 50,
                    backgroundColor: '#fff',
                    width: '100%',
                    paddingTop: 8
                }}>
                    <button className="btn btn-primary" onClick={this.nextBatch}>Refresh</button>
                </div>
                <ArticleList panels={this.state.panels}/>
            </div>
        )
    }
});

ReactDOM.render(
    <Body source="http://52.8.162.98:8080/api/mfw/"/>,
    document.getElementById("content")
);