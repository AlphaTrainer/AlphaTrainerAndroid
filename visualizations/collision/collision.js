// Generated by CoffeeScript 1.6.3
(function() {
  var Collision, gravity, height, log, minRadius, mouse, numColor, numNodes, width,
    __slice = [].slice;

  log = function() {
    var args;
    args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
    if (console.log != null) {
      return console.log.apply(console, args);
    }
  };

  Collision = (function() {
    function Collision(name) {
      this.name = name;
      log("Collision constructor called: " + this);
    }

    Collision.prototype.feedback = function(num) {
      if (num < 25) {
        this.force.alpha(.8);
        return;
      }
      if (num < 35) {
        this.force.alpha(.6);
      }
      if (num < 50) {
        this.force.alpha(.4);
        return;
      }
      if (num < 35) {
        this.force.alpha(.25);
        return;
      }
      if (num < 75) {
        this.force.alpha(.1);
        return;
      }
      this.force.start();
      return void 0;
    };

    /*      
      if num > 50
        @tweakNodes([Math.round(@width/2), Math.round(@height/2)])
        return
      if num > 50
        @tweakNodes([Math.round(@width/4), Math.round(@height/4)])
        return
      if num > 25
        @tweakNodes([Math.round(@width*3/4), Math.round(@height*3/4)])
        return
      undefined
    */


    Collision.prototype.tweakNodes = function(point) {
      this.root.px = point[0];
      this.root.py = point[1];
      return this.force.resume();
    };

    Collision.prototype.color = d3.scale.category20b();

    Collision.prototype.collide = function(node) {
      var nx1, nx2, ny1, ny2, r;
      r = node.radius + 16;
      nx1 = node.x - r;
      nx2 = node.x + r;
      ny1 = node.y - r;
      ny2 = node.y + r;
      return function(quad, x1, y1, x2, y2) {
        var l, x, y;
        if (quad.point && (quad.point !== node)) {
          x = node.x - quad.point.x;
          y = node.y - quad.point.y;
          l = Math.sqrt(x * x + y * y);
          r = node.radius + quad.point.radius;
          if (l < r) {
            l = (l - r) / l * 0.5;
            node.x -= x *= l;
            node.y -= y *= l;
            quad.point.x += x;
            quad.point.y += y;
          }
        }
        return x1 > nx2 || x2 < nx1 || y1 > ny2 || y2 < ny1;
      };
    };

    void 0;

    Collision.prototype.setupNodes = function(nodes, svg, color, numColor) {
      svg.selectAll("circle").data(nodes.slice(1)).enter().append("svg:circle").attr("r", function(d) {
        return d.radius - 2;
      }).style("fill", function(d, i) {
        return color(i % numColor);
      });
      return void 0;
    };

    Collision.prototype.setupForce = function(nodes, svg, force, collide) {
      force.on("tick", function(e) {
        var i, n, q;
        q = d3.geom.quadtree(nodes);
        i = 0;
        n = nodes.length;
        while (++i < n) {
          q.visit(collide(nodes[i]));
        }
        return svg.selectAll("circle").attr("cx", function(d) {
          return d.x;
        }).attr("cy", function(d) {
          return d.y;
        });
      });
      return void 0;
    };

    Collision.prototype.setupMouseEvent = function(root, svg, force) {
      svg.on("mousemove", function() {
        var p1;
        p1 = d3.mouse(this);
        root.px = p1[0];
        root.py = p1[1];
        return force.resume();
      });
      return void 0;
    };

    Collision.prototype.init = function(width, height, numNodes, gravity, minRadius, mouse, numColor) {
      var nodes;
      if (gravity == null) {
        gravity = 0.05;
      }
      if (minRadius == null) {
        minRadius = 4;
      }
      if (mouse == null) {
        mouse = false;
      }
      if (numColor == null) {
        numColor = 4;
      }
      log("init called");
      log("width: " + width + " height: " + height);
      nodes = d3.range(numNodes).map(function() {
        return {
          radius: Math.random() * 12 + minRadius
        };
      });
      this.width = width;
      this.height = height;
      this.numColor = numColor;
      this.force = d3.layout.force().gravity(gravity).charge(function(d, i) {
        if (i) {
          return 0;
        } else {
          return -2000;
        }
      }).nodes(nodes).size([this.width, this.height]);
      this.root = nodes[0];
      this.root.radius = 0;
      this.root.fixed = false;
      this.force.start();
      this.svg = d3.select("svg");
      if (this.svg[0][0] === null) {
        this.svg = d3.select("body").append("svg:svg").attr("width", this.width).attr("height", this.height);
      }
      this.setupNodes(nodes, this.svg, this.color, this.numColor);
      this.setupForce(nodes, this.svg, this.force, this.collide);
      if (mouse) {
        return this.setupMouseEvent(this.root, this.svg, this.force);
      }
    };

    return Collision;

  })();

  window.collision = new Collision("Brain collision");

  if (navigator.userAgent.indexOf('Chrome') !== -1 && parseFloat(navigator.userAgent.substring(navigator.userAgent.indexOf('Chrome') + 7).split(' ')[0]) >= 15) {
    collision.init(width = 600, height = 600, numNodes = 200, gravity = 0.05, minRadius = 4, mouse = true, numColor = 4);
    collision.feedback(30);
    collision.feedback(100);
  }

}).call(this);
