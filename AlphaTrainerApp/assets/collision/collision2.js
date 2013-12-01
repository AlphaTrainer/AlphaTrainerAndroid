var chrome = navigator.userAgent.indexOf('Chrome') !== -1 && parseFloat(navigator.userAgent.substring(navigator.userAgent.indexOf('Chrome') + 7).split(' ')[0]) >= 15;

var loopActivated = false;
var loopFrameDelay = 30;
var cx; // center of x-axis
var cy; // center of y-axis
var r = 1; // radius of circle in loop

// screen width and height
var width;
var height;

// 
var nodes;
var root;
var color;
var force;
var svg;

function collide(node) {
  var r = node.radius + 16,
      nx1 = node.x - r,
      nx2 = node.x + r,
      ny1 = node.y - r,
      ny2 = node.y + r;
  return function(quad, x1, y1, x2, y2) {
    if (quad.point && (quad.point !== node)) {
      var x = node.x - quad.point.x,
          y = node.y - quad.point.y,
          l = Math.sqrt(x * x + y * y),
          r = node.radius + quad.point.radius;
      if (l < r) {
        l = (l - r) / l * .5;
        node.x -= x *= l;
        node.y -= y *= l;
        quad.point.x += x;
        quad.point.y += y;
      }
    }
    return x1 > nx2 || x2 < nx1 || y1 > ny2 || y2 < ny1;
  };
}



function startLoop() {

  // r = Math.min(cx, cy);
  loopActivated = true;

  drawPointInLoop(0);
  
}


function drawPointInLoop(a) {

  // if (chrome && console) console.log("drawPointInLoop() - angle: " + a);

  if (loopActivated) {

    var aRadian = a * Math.PI / 180.0;
    var x = cx + r * Math.cos(aRadian);
    var y = cy + r * Math.sin(aRadian)
    // r = radius, (cx,cy) = center, a = angle from 0..2PI radians
    // aRadian = aDegree * Math.PI / 180F

    setRootPos(x,y);

    setTimeout(function() { drawPointInLoop((a + 10) % 360) }, loopFrameDelay);

  }

}


function setRootPos(x,y) {

  // if (chrome && console) console.log("setRootPos() - x: " + x + " y: " + y);

  root.px = x;
  root.py = y;
  force.resume();
}


function drawFeedback(i) {

  var newR = Math.min(cx, cy) * (100-i) / 200;
  setR(newR);

}


function setR(newR) {

  /*
   * Elaborate on this if in need for a smooth transition between r values. For now we set it hard.
   * 
   *
   * var transitionSteps = 10;
   * var stepDuration = 30;

   * var oldR = r;
   * var difference = Math.abs(newR-oldR);

  
  for (i=0; i<transitionSteps; i++) {

    // r = newR > oldR ? oldR + i * difference / transitionSteps : oldR - i * difference / transitionSteps;
    // if (chrome && console) console.log("setR() - r: " + r);

    setTimeout(function() { 
      r = newR > oldR ? oldR + i * difference / transitionSteps : oldR - i * difference / transitionSteps; 
      if (chrome && console) console.log("setR() - r: " + r);
    },
    stepDuration*i);
    
  }
  */

  r = newR;
  if (chrome && console) console.log("setR() - r: " + r);

}



function init(w, h, numNodes, gravity, minRadius) { // e.g. init(960,500,50,0.05,10)  init(w=960,h=500,numNodes=50,gravity=0.05,minRadius=10)

  width = w;
  height = h;

  cx = width/2;
  cy = height/2;

  nodes = d3.range(numNodes).map(function() { return {radius: Math.random() * 12 + minRadius}; });
  root = nodes[0];
  color = d3.scale.category20b();

  root.radius = 0;
  root.fixed = true;

  force = d3.layout.force()
      .gravity(gravity)
      .charge(function(d, i) { return i ? 0 : -2000; })
      .nodes(nodes)
      .size([width, height]);

  force.start();

  svg = d3.select("body").append("svg:svg")
      .attr("width", width)
      .attr("height", height);

  svg.selectAll("circle")
      .data(nodes.slice(1))
      .enter().append("circle")
      .attr("r", function(d) { return d.radius; })
      .style("fill", function(d, i) { return color(i % 3); });

  force.on("tick", function(e) {
    var q = d3.geom.quadtree(nodes),
        i = 0,
        n = nodes.length;

    while (++i < n) q.visit(collide(nodes[i]));

    svg.selectAll("circle")
        .attr("cx", function(d) { return d.x; })
        .attr("cy", function(d) { return d.y; });
  });

  svg.on("mousemove", function() {
    var p1 = d3.mouse(this);
    root.px = p1[0];
    root.py = p1[1];
    force.resume();

    // if (chrome && console) console.log("mouseover - x: " + p1[0] + ", y: " + p1[1]);
  });

  // start circle loop
  startLoop();

}
