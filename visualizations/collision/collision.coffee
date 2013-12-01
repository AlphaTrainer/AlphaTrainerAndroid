# Just a log helper
log = (args...) ->
    console.log.apply console, args if console.log?

# ---------------------------------------- CLASS

class Collision

  constructor: (@name) ->
    log "Collision constructor called: "+ @

  # TODO: make it more fine graded
  # should take a number between 0 and 100
  # TODO: look into https://github.com/mbostock/d3/wiki/Force-Layout#wiki-force 
  feedback: (num) ->
    if num < 25
      @force.alpha(.8)
      return
    if num < 35
      @force.alpha(.6)      
    if num < 50
      @force.alpha(.4)
      return
    if num < 35
      @force.alpha(.25)
      return
    if num < 75
      @force.alpha(.1)
      return

    @force.start()  
    undefined
  ###      
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
  ###

  # point have the value: [374, 277]
  tweakNodes: (point) ->
    @root.px = point[0]
    @root.py = point[1]
    @force.resume()


  # more on color scales https://github.com/mbostock/d3/wiki/Ordinal-Scales
  color: d3.scale.category20b()
    
  collide: (node) ->
      r = node.radius + 16
      nx1 = node.x - r
      nx2 = node.x + r
      ny1 = node.y - r
      ny2 = node.y + r
      (quad, x1, y1, x2, y2) ->
        if quad.point and (quad.point isnt node)
          x = node.x - quad.point.x
          y = node.y - quad.point.y
          l = Math.sqrt(x * x + y * y)
          r = node.radius + quad.point.radius
          if l < r
            l = (l - r) / l * 0.5
            node.x -= x *= l
            node.y -= y *= l
            quad.point.x += x
            quad.point.y += y
        x1 > nx2 or x2 < nx1 or y1 > ny2 or y2 < ny1
    undefined
        
  setupNodes: (nodes, svg, color, numColor) ->
    svg.selectAll("circle")
       .data(nodes.slice(1))
       .enter()
       .append("svg:circle")
       .attr("r", (d) ->
         d.radius - 2
       ).style "fill", (d, i) ->
         color i % numColor
    undefined
    
  setupForce: (nodes, svg, force, collide) ->
    force.on "tick", (e) ->
      q = d3.geom.quadtree(nodes)
      i = 0
      n = nodes.length
      q.visit collide(nodes[i])  while ++i < n
      svg.selectAll("circle").attr("cx", (d) ->
        d.x
      ).attr "cy", (d) ->
        d.y
    undefined
    
    
  setupMouseEvent: (root, svg, force) ->
    svg.on "mousemove", ->
      p1 = d3.mouse(this)
      root.px = p1[0]
      root.py = p1[1]
      force.resume()
    undefined      
    

  init: (width, height, numNodes, gravity=0.05, minRadius=4, mouse=false, numColor=4) ->

    log "init called"
    log "width: "+width+" height: "+height

    nodes = d3.range(numNodes).map(->radius: Math.random() * 12 + minRadius)
    
    @width=width
    @height=height
    @numColor=numColor
    
    @force = d3.layout
               .force()
               .gravity(gravity)
               .charge((d, i) -> (if i then 0 else -2000))
               .nodes(nodes)
               .size([@width, @height])
    @root = nodes[0]
    @root.radius = 0
    @root.fixed = false
    @force.start()
    
    # ensure we only append one svg 
    # TODO: figure out a way svg if its already loaded.
    @svg = d3.select("svg")

    if @svg[0][0] == null 
      @svg = d3.select("body")
               .append("svg:svg")
               .attr("width", @width)
               .attr("height", @height)

    @setupNodes(nodes, @svg, @color, @numColor)

    @setupForce(nodes, @svg, @force, @collide)

    @setupMouseEvent(@root, @svg, @force) if mouse

# ---------------------------------------- INIT A CLASS

window.collision = new Collision("Brain collision")

# Chrome - do only for desktop
# TODO: do it reverse not for android browser perhaps do as http://stackoverflow.com/questions/11381673/javascript-solution-to-detect-mobile-browser
if (navigator.userAgent.indexOf('Chrome') != -1 && parseFloat(navigator.userAgent.substring(navigator.userAgent.indexOf('Chrome') + 7).split(' ')[0]) >= 15)
  collision.init(width=600, height=600, numNodes=200, gravity=0.05, minRadius=4, mouse=true, numColor=4)
  # or
  # collision.init(600, 600, 200)
  # then perhaps do some tweaks
  collision.feedback(30)
  collision.feedback(100)