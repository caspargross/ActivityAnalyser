/**
 * Created by caspar on 04.07.17.
 */
// Define the namespace

var myGraph = myGraph || {};

myGraph.LineChart = function (element) {


    var data;

    this.setData = function (data) {
        this.data = data;
        console.log(data);
    };

    var margin = {top: 20, right: 20, bottom: 30, left: 50},
        width = 960 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom;


    var svg = d3.select(element).append("svg:svg").attr("width", '100%').attr("height", '500');
    svg.append("svg:circle").attr("cx", 250).attr("cy", 250).attr("r", 20).attr("fill", "red");

    /*var svg = d3.select(element.getElementById("contentArea"))
        .append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
        .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
        .append("circle")
            .attr("cx", 25)
            .attr("cy", 25)
            .attr("r", 25)
            .style("fill", "purple"); */

    /*var svg = d3.select("contentArea")
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
        .append("circle")
        .attr("cx", 25)
        .attr("cy", 25)
        .attr("r", 25)
        .style("fill", "purple");

        */
    this.drawChart = function() {
        // Scale the range of the data
        x.domain(d3.extent(data, function(d) { return d.millisstart; }));
        y.domain([0, d3.max(data, function(d) {
            return Math.max(d.usersteps, d.averagesteps); })]);

        // Add the valueline path.
        svg.append("path")
            .data([data])
            .attr("class", "line")
            .attr("d", valueline);

        // Add the valueline2 path.
        svg.append("path")
            .data([data])
            .attr("class", "line")
            .style("stroke", "red")
            .attr("d", valueline2);
        // Add the X Axis
        svg.append("g")
            .attr("transform", "translate(0," + height + ")")
            .call(d3.axisBottom(x));

        // Add the Y Axis
        svg.append("g")
            .call(d3.axisLeft(y));
    }



};