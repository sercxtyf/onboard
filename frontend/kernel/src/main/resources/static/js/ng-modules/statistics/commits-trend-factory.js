angular.module('statistics')
    .factory('commitsTrend', ['trend', '$rootScope', 'drawer', function(trend, $rootScope, drawer) {
        var init = function() {
            var that = trend();
            var spec = that.getSpec();

            var parse = d3.time.format('%Y-%m-%d').parse;

            that.setX(function(d) {
                return parse(d.key);
            });

            var processData = function(data) {
                data.forEach(function(d) {
                    d.date = d.date.split(' ')[0];
                });
            };

            var groupCommitsData = function(data) {
                processData(data);
                var nestData = d3.nest().key(function(d) {
                    return d.date;
                }).entries(data);

                spec.yfunc = function(d) {
                    d.action="";
                    return d.values.length;
                };

                return nestData;
            };

            var groupLocData = function(data) {
                processData(data);
                var nestData = d3.nest().key(function(d) {
                    return d.date;
                }).entries(data);

                spec.yfunc = function(d) {
                    d.action="";
                    var count = 0;
                    d.values.forEach(function(e) {
                        count += e.loc;
                    });
                    return count;
                };

                return nestData;
            };

            that.drawGraph = function(specs) {
                var data = that.getRawData();
                if (spec.groupData !== undefined) {
                    data = spec.groupData(that.filterData(data, specs));
                    that.draw(data, {
                        addMissingDate: false
                    });
                }
            };
            that.setGroupLoc = function() {
                spec.groupData = groupLocData;
                spec.text = '行代码';
            };
            that.setGroupCommits = function() {
                spec.groupData = groupCommitsData;
                spec.text = '次提交';
            };

            var bindingTooltips = function() {
                $(spec.id).find('.tooltip a').bind('click', function() {
                    var commitId = $(this).attr('value');
                    //console.log(spec.id);
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
                    var div = d3.select(spec.id).append('div')
                        .attr('class', 'tooltip')
                        .style('opacity', 0);
                    var $div = $('.tooltip');
                    //$div.css('pointer-events', 'auto');
                    spec.svg.selectAll('circle')
                        .on('mouseover', function(d) {
                            div.transition()
                                .duration(200)
                                .style({
                                    'opacity': 0.9,
                                    'z-index': 1024
                                });
                            div.html('<p>' + d.action + Math.abs(spec.yfunc(d)) + ' ' + spec.text + '<ul></ul>' + '</p>');
                            d.values.forEach(function(e) {
                                var $item = $('<li></li>').html(e.date + ' ' + e.userName + ' ');
                                var $link = $('<a></a>');
                                //var targetUrl = '/{0}/projects/{1}/repos/{2}/commits/{3}'.format(spec.companyId, spec.projectId, e.repoId, e.id);
                                $link.text(e.id.slice(0, 7))
                                    .attr('href', '')
                                    .attr('value', e.id);
                                //$link.text(e.id.slice(0, 7)).attr('href', targetUrl).attr('target', '_blank');
                                $item.append($link).appendTo($div.find('ul'));
                            });
                            $('.tooltip').offset({
                                top: d3.event.pageY - 28,
                                left: d3.event.pageX - 135
                            });
                            bindingTooltips();
                        })
                        .on('mouseout', function() {
                            div.transition().delay(2000).duration(500).style({
                                'opacity': 0,
                                'z-index': -1
                            });
                        });
                };
            };

            setTooltip();

            return that;
        };
        return init;
    }]);
