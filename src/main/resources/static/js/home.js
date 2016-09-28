var TodoList = React.createClass({
    render: function () {
        var createItem = function (item) {
            return <li key={item.id}>{item.sourceId} {item.type.toLowerCase()} {item.targetId}</li>;
        };
        return <ul>{this.props.graph.arrows.map(createItem)}</ul>
    }
});

var Body = React.createClass({
    getInitialState: function () {
        return {
            graph: {
                points: [],
                arrows: [],
                extensions: []
            },
            query: ""
        }
    },

    handleChange: function (event) {
        this.setState({ query: event.target.value })
    },

    queryForGraph: function () {
        $.ajax({
            url: this.props.source + "chain/" + this.state.query,
            data: {
                arrowType: "TAGGING",
                sliceMode: "depth",
                leftLimit: 0,
                rightLimit: 1,
                leftInclusive: false
            },
            success: function (data) {
                this.setState({graph: data})
            }.bind(this)
        });
    },

    render: function () {
        return (
            <div className="container">
                <div className="row">
                    <TodoList graph={this.state.graph}/>
                </div>
                <div className="row">
                    <textarea className="form-control" onChange={this.handleChange}></textarea>
                    <br />
                    <button className="btn btn-primary pull-right" onClick={this.queryForGraph}>Submit</button>
                </div>
            </div>
        )
    }
});

ReactDOM.render(
    <Body source="http://localhost:8080/api/graph/"/>,
    document.getElementById("content")
);