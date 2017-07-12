/**
 * Created by caspar on 04.07.17.
 */
// Define the namespace

var myGraph = myGraph || {};

myGraph.LineChart = function (element) {

    var data;

    var parseTime = d3.timeParse("%d-%b-%y");

    this.setData = function (dataString) {
        var dat = JSON.parse(dataString)
        dat.forEach(function (d) {
            d.usersteps = +d.steps;
            d.averagesteps = +d.average[0];
            d.millisstart = +d.startMillis;
        });

        data=dat;
        console.log(data);
        drawChart(data);
    };

    var margin = {top: 20, right: 20, bottom: 30, left: 50},
        width = 1000 - margin.left - margin.right,
        height = 800 - margin.top - margin.bottom;


    var svg = d3.select(element).append("svg:svg").attr("width", 1200).attr("height", 800),
        g  = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");


    // set the ranges
    var x = d3.scaleLinear().range([0, width]);
    var y = d3.scaleLinear().range([height, 0]);

    // define the 1st line
    var valueline = d3.line()
        .x(function(d) { return x(d.millisstart); })
        .y(function(d) { return y(d.usersteps); });

    // define the 2nd line
    var valueline2 = d3.line()
        .x(function(d) { return x(d.millisstart); })
        .y(function(d) { return y(d.averagesteps); });


    function drawChart(dat) {
        console.log(dat);
        // Scale the range of the data
        x.domain(d3.extent(dat, function(d) { return d.millisstart; }));
        y.domain([0, d3.max(dat, function(d) {return Math.max(d.usersteps, d.averagesteps); })]);

        // Add the valueline path.
        g.append("path")
            .data([dat])
            .attr("d", valueline)
            .attr("stroke", "steelblue")
            .attr("fill", "none")
            .attr("stroke-width", "3px");

        // Add the valueline2 path.
        g.append("path")
            .data([dat])
            .attr("d", valueline2)
            .attr("stroke-width", "3px")
            .attr("fill", "none")
            .attr("stroke", "red");

        // Add the X Axis
        g.append("g")
            .attr("transform", "translate(0," + height + ")")
            .call(d3.axisBottom(x)
                .tickFormat(d3.timeFormat("%Y-%m-%d")));

        // Add the Y Axis
        g.append("g")
            .call(d3.axisLeft(y));

        // Add legend
        svg.append("text")
            .attr("transform", "translate(" + (width+1) + "," + y(data[0].averagesteps) + ")")
            .attr("dy", "1.5em")
            .attr("text-anchor", "start")
            .style("fill", "red")
            .text("averagesteps");

        svg.append("text")
            .attr("transform", "translate(" + (width+1) + "," + y(data[0].usersteps) + ")")
            .attr("dy", "10em")
            .attr("text-anchor", "start")
            .style("fill", "steelblue")
            .text("usersteps");


    }

};