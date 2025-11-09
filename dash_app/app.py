import os
from datetime import datetime
import dash
from dash import Dash, dcc, html, Input, Output, State
import plotly.express as px
import pandas as pd

# Create sample dataframe
_df = px.data.iris()

# App initialization
app = Dash(__name__, title="ColorSplash Dashboard", update_title=None, suppress_callback_exceptions=True)
server = app.server  # WSGI entrypoint for gunicorn

# Layout
app.layout = html.Div(
    className="app-container",
    children=[
        html.Div(
            className="header",
            children=[
                html.Div(className="logo", children="🎨 ColorSplash"),
                html.Div(className="subtitle", children="Interactive Python App for Docker & Kubernetes"),
            ],
        ),
        html.Div(
            className="controls",
            children=[
                dcc.Dropdown(
                    id="species",
                    options=[{"label": s.title(), "value": s} for s in sorted(_df["species"].unique())],
                    value="setosa",
                    clearable=False,
                    className="dropdown",
                ),
                dcc.Slider(id="point-size", min=5, max=20, step=1, value=10,
                           marks={5: "5", 10: "10", 15: "15", 20: "20"}),
                html.Button("Shuffle Palette", id="shuffle", n_clicks=0, className="btn"),
            ],
        ),
        html.Div(
            className="content",
            children=[
                dcc.Graph(id="scatter"),
                html.Div(id="stats", className="stats"),
            ],
        ),
        html.Div(
            className="footer",
            children=[
                html.Span(id="time"),
                html.Span(" · "),
                html.A("Health", href="/healthz", className="health-link"),
            ],
        ),
        dcc.Interval(id="tick", interval=1000, n_intervals=0),
    ],
)

_palettes = [
    ["#4F46E5", "#22D3EE", "#F59E0B"],
    ["#EC4899", "#8B5CF6", "#10B981"],
    ["#FB7185", "#34D399", "#3B82F6"],
]

@app.callback(
    Output("scatter", "figure"),
    Output("stats", "children"),
    Input("species", "value"),
    Input("point-size", "value"),
    Input("shuffle", "n_clicks"),
    prevent_initial_call=False,
)
def update_plot(species: str, point_size: int, n_clicks: int):
    palette = _palettes[n_clicks % len(_palettes)] if n_clicks else _palettes[0]
    dff = _df[_df["species"] == species]
    fig = px.scatter(
        dff,
        x="sepal_width",
        y="sepal_length",
        color="species",
        size_max=point_size,
        color_discrete_sequence=palette,
        template="plotly_dark",
        title=f"Sepal Length vs Width — {species.title()}",
    )
    fig.update_traces(marker={"size": point_size})
    fig.update_layout(margin=dict(l=40, r=20, t=60, b=40))
    stats = html.Div([
        html.Div([html.Span("Min Length:"), html.B(f" {dff['sepal_length'].min():.2f}")]),
        html.Div([html.Span("Max Length:"), html.B(f" {dff['sepal_length'].max():.2f}")]),
        html.Div([html.Span("Avg Width:"), html.B(f" {dff['sepal_width'].mean():.2f}")]),
    ])
    return fig, stats

@app.callback(Output("time", "children"), Input("tick", "n_intervals"))
def tick(_n):
    return datetime.utcnow().strftime("%Y-%m-%d %H:%M:%S UTC")

# Simple health endpoint
@server.route("/healthz")
def healthz():
    return {"status": "ok", "app": "ColorSplash", "time": datetime.utcnow().isoformat()}, 200

if __name__ == "__main__":
    port = int(os.getenv("PORT", "5000"))
    app.run_server(host="0.0.0.0", port=port, debug=False)
