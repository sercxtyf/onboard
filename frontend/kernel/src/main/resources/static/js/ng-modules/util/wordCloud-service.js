angular.module('util')
    .service('wordCloud', function() {

        this.drawWordCloud = function(specs) {
            var fill = d3.scale.category20();
            var r;
            if(specs.words.length <= 50)
                r = [25, 50];
            else
                r = [20, 50];
            var min = d3.min(specs.words, function(d){return d.values});
            var max = d3.max(specs.words, function(d){return d.values});
            var d = [min, max];
            var fontSize = d3.scale.linear().domain(d).range(r);

            d3.layout.cloud()
                .size([specs.width, specs.height])
                .text(function(d) {
                    return d.key;
                })
                .font("Impact")
                .fontSize(function(d) {
                    return fontSize(d.values);
                })
                .rotate(function(d) {
                    return ~~(Math.random() * 5) * 30 - 60;
                })
                .padding(1)
                .on("end", draw)
                .words(specs.words)
                .start();

            function draw(words) {
                d3.select(specs.id).append("svg")
                    .attr("width", specs.width)
                    .attr("height", specs.height)
                    .append("g")
                    .attr("transform", "translate(" + specs.width / 2 + ",150)")
                    .selectAll("text")
                    .data(words)
                    .enter().append("text")
                    .style("font-size", function(d) {
                        return d.size + "px";
                    })
                    .style("font-family", "Impact")
                    .style("fill", function(d, i) {
                        return fill(i);
                    })
                    .attr("text-anchor", "middle")
                    .attr("transform", function(d) {
                        return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
                    })
                    .text(function(d) {
                        return d.text;
                    });
            }
        };

    });
