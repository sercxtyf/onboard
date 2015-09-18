angular.module('statistics')
    .factory('commitsPunchCard', ['punchCard', '$rootScope', 'drawer', function(punchCard, $rootScope, drawer) {
        var that = punchCard();
        var spec = that.getSpec();

        var processData = function(data) {
            data.forEach(function(d) {
                d.date = d3.time.format("%Y-%m-%d %X").parse(d.date);
            });
        };

        var groupCommitsData = function(data) {
            processData(data);
            var nestData = d3.nest().key(function(d) {
                return that.timeFormat(d.date);
            }).entries(data);

            spec.rfunc = function(d) {
                return d.values.length;
            };

            setTooltip();

            return nestData;
        };

        var groupLocData = function(data) {
            processData(data);
            return d3.nest().key(function(d) {
                return d.date;
            }).rollup(function(leave) {
                var count = 0;
                leave.forEach(function(d) {
                    count += d.Loc;
                });
                return count;
            }).entries(data);
        };

        that.drawGraph = function(specs) {
            var data = that.getRawData();
            if (spec.groupData !== undefined)
                data = spec.groupData(that.filterData(data, specs));
            else {
                console.error("Group data undefined!");
                return;
            }
            that.draw(data);
        };
        that.setGroupLoc = function() {
            spec.groupData = groupLocData;
            spec.text = "行代码";
        };
        that.setGroupCommits = function() {
            spec.groupData = groupCommitsData;
            spec.text = "次提交";
        };

        var bindingTooltips = function() {
            $(spec.id).find('.tooltip a').bind('click', function() {
                var commitId = $(this).attr('value');
                //console.log(id);
                // $rootScope.$broadcast('showDiffDrawer', {
                //     commitId: id
                // });
                drawer.open({
                    type: 'commit',
                    params: {
                        id: commitId
                    }
                });
                return false;
            });
        };

        var setTooltip = function() {
            spec.addTooltip = function() {
                //console.log(spec.companyId);
                var div = d3.select(spec.id).append("div")
                    .attr("class", "tooltip")
                    .style("opacity", 0);
                var $div = $(spec.id + ' .tooltip');

                spec.svg.selectAll("circle")
                    .on("mouseover", function(d) {
                        div.transition()
                            .duration(200)
                            .style({
                                "opacity": 0.9,
                                "z-index": 1024
                            });
                        div.html("<p>" + spec.rfunc(d) + " " + spec.text + "<ul></ul>" + "</p>");
                        d.values.forEach(function(e) {
                            var $item = $("<li></li>").html(d3.time.format("%Y-%m-%d")(e.date) + ' ' + e.userName + ' ');
                            var $link = $("<a></a>");
                            //var targetUrl = '/{0}/projects/{1}/repos/{2}/commits/{3}'.format(spec.companyId, spec.projectId, e.repoId, e.id);
                            $link.text(e.id.slice(0, 7))
                                .attr("href", "")
                                .attr("value", e.id);
                            $item.append($link).appendTo($div.find('ul'));
                        });
                        $('.tooltip').offset({
                            top: d3.event.pageY - 28,
                            left: d3.event.pageX - 135
                        });
                        bindingTooltips();
                    })
                    .on("mouseout", function() {
                        div.transition().delay(2000).duration(500).style({
                            "opacity": 0,
                            "z-index": -1
                        });
                    });
            };
        };
        return that;
    }]);
