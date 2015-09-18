angular.module('statistics')
    .service('workModel', ['$q', 'companyActivityStats', 'companyTodoStats', 'companyCommitStats', 'companyStepStats', 'companyBugStats', 'barChart', 'pieChart',
        function($q, companyActivityStats, companyTodoStats, companyCommitStats, companyStepStats, companyBugStats, barChart, pieChart) {
            this.getWorkModelData = function(start, end) {
                var ret = {};
                var workData = [];
                $q.all([companyActivityStats.getActivityStats(start, end), companyCommitStats.getCommitStatsData(start, end), companyTodoStats.getTodoStatsData(start, end), companyStepStats.getStepStatsData(start, end), companyBugStats.getBugStatsData(start, end)])
                    .then(function(result) {
                        var commits = result[1].filter(function(ele) {
                            return ele.userId !== 0;
                        });
                        var activities = result[0];
                        var todos = [].concat(result[2]).concat(result[3]).concat(result[4]).filter(function(ele) {
                            return ele.assigneeId !== undefined;
                        });

                        // commit
                        var data = pieChart.groupLocData(commits).data;
                        var ruler = convertScore(data);
                        data.forEach(function(d) {
                            if (ret[d.name] === undefined) {
                                ret[d.name] = getDefaultWorkLoad();
                            }
                            ret[d.name].loc = ruler(d.value);
                        });

                        // activity
                        data = pieChart.groupActivityData(activities).data;
                        ruler = convertScore(data);
                        data.forEach(function(d) {
                            if (ret[d.name] === undefined) {
                                ret[d.name] = getDefaultWorkLoad();
                            }
                            ret[d.name].activities = ruler(d.value);
                        });

                        // todo
                        var data = pieChart.groupTodoData(todos).data;
                        var ruler = convertScore(data);
                        data.forEach(function(d) {
                            if (ret[d.name] === undefined) {
                                ret[d.name] = getDefaultWorkLoad();
                            }
                            ret[d.name].todos = ruler(d.value);
                        });

                        // workload
                        for (var key in ret) {
                            var ele = ret[key];
                            workData.push({
                                name: key,
                                value: 0.6 * ele.loc + 0.3 * ele.todos + 0.1 * ele.activities
                            });
                        }
                        var specs = {
                            id: '#contribution',
                            title: '团队成员贡献评估',
                            ytitle: '评分',
                            dataList: workData.sort(function(a, b) {
                                return b.value - a.value;
                            }),
                            name: '贡献度评分'
                        };
                        barChart.draw(specs);
                    });
            };
            var getDefaultWorkLoad = function() {
                return {
                    loc: 0,
                    todos: 0,
                    activities: 0
                };
            };
            /*            var processData = function(data) {
                            return d3.nest().key(function(d) {
                                if (d.userId === 0)
                                    return d.userId;
                                else
                                    return d.userId || d.creatorId;
                            }).rollup(function(leave) {
                                return {
                                    count: leave.length,
                                    name: leave[0].userName || leave[0].creatorName
                                };
                            }).entries(data);
                        }*/
            var convertScore = function(data) {
                var mid = 85;
                var arr = [];
                data.forEach(function(d) {
                    if (d.value !== 0)
                        arr.push(d.value);
                });
                arr = arr.filter(function(ele) {
                    return ele !== 0;
                });
                arr = $.unique(arr);
                var fun;
                if (arr.length <= 1) {
                    fun = function(d) {
                        if (d === 0)
                            return 0;
                        else
                            return mid;
                    };
                } else {
                    var scale = d3.scale.linear().domain([d3.min(arr), d3.max(arr)]).range([70, 100]);
                    fun = function(d) {
                        if (d === 0)
                            return 0;
                        else
                            return scale(d);
                    };
                }
                return fun;
            }
        }
    ]);
