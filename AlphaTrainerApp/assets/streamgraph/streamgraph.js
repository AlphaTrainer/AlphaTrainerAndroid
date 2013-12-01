// Generated by CoffeeScript 1.6.3
(function() {
  var area, bumpLayer, buttonActions, color, height, layers0, layers1, log, m, n, stack, svg, transition, width, x, y,
    __slice = [].slice;

  log = function() {
    var args;
    args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
    if (console.log != null) {
      return console.log.apply(console, args);
    }
  };

  layers1 = null;

  layers0 = null;

  transition = function(time) {
    return d3.selectAll("path").data(function() {
      var d;
      d = layers1;
      layers1 = layers0;
      return layers0 = d;
    }).transition().duration(time).attr("d", area);
  };

  bumpLayer = function(n) {
    var a, bump, i;
    bump = function(a) {
      var i, w, x, y, z, _results;
      x = 1 / (.1 + Math.random());
      y = 2 * Math.random() - .5;
      z = 10 / (.1 + Math.random());
      i = 0;
      _results = [];
      while (i < n) {
        w = (i / n - y) * z;
        a[i] += x * Math.exp(-w * w);
        _results.push(i++);
      }
      return _results;
    };
    a = [];
    i = void 0;
    i = 0;
    while (i < n) {
      a[i] = 0;
      ++i;
    }
    i = 0;
    while (i < 5) {
      bump(a);
      ++i;
    }
    return a.map(function(d, i) {
      return {
        x: i,
        y: Math.max(0, d)
      };
    });
  };

  n = 20;

  m = 200;

  stack = d3.layout.stack().offset("wiggle");

  layers0 = stack(d3.range(n).map(function() {
    return bumpLayer(m);
  }));

  layers1 = stack(d3.range(n).map(function() {
    return bumpLayer(m);
  }));

  width = 960;

  height = 500;

  x = d3.scale.linear().domain([0, m - 1]).range([0, width]);

  y = d3.scale.linear().domain([
    0, d3.max(layers0.concat(layers1), function(layer) {
      return d3.max(layer, function(d) {
        return d.y0 + d.y;
      });
    })
  ]).range([height, 0]);

  color = d3.scale.linear().range(["#aad", "#556"]);

  area = d3.svg.area().x(function(d) {
    return x(d.x);
  }).y0(function(d) {
    return y(d.y0);
  }).y1(function(d) {
    return y(d.y0 + d.y);
  });

  svg = d3.select("body").append("svg").attr("width", width).attr("height", height);

  svg.selectAll("path").data(layers0).enter().append("path").attr("d", area).style("fill", function() {
    return color(Math.random());
  });

  buttonActions = d3.select("button").on("click", function() {
    return transition(2500);
  });

}).call(this);