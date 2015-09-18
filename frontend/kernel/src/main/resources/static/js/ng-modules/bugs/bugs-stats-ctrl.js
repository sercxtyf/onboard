angular.module('bugs')
    .controller('bugsStatsCtrl', ['$scope', '$http', 'url', 'pieChart', 'bugService',
        function($scope, $http, url, pieChart, bugService) {
    		$scope.$parent.memberFilterHide = true;
   	
            var allBugs;
            var closedBugs;
            
            bugService.getAllBugList().then(function(data) {
                allBugs = data;
                drawAssignment(allBugs);
            });

            bugService.getClosedBugList().then(function(data) {
                closedBugs = data;
                drawCompletition(closedBugs);
            });

            var drawAssignment = function(bugs){
                var result = pieChart.groupActivityData(bugs);
                var specs= {};
                var winner = findMaxValue(result.data);
                specs.text = '至今为止所有成员共找到了' + result.total + '个Bug';
                if(winner === null)
                    specs.subtext = '';
                else
                    specs.subtext = '恭喜' + winner.name + '成为捕捉Bug的冠军！TA一共找到了' + winner.value + '个Bug，占总数的' + (winner.y * 100).toFixed(2) + '%';
                specs.names = [];
                specs.data = result.data;
                specs.data.forEach(function(ele){
                    specs.names.push(ele.name);
                });
                specs.tag = '创建Bug的数目';
                specs.id = 'assignment';
                drawPieChart(specs);
            };

            var drawCompletition = function(bugs){
                var result = pieChart.groupTodoData(bugs);
                var specs= {};
                var winner = findMaxValue(result.data);
                specs.text = '至今为止所有成员共修复了' + result.total + '个Bug';
                if(winner === null)
                    specs.subtext = '';
                else
                    specs.subtext = '恭喜' + winner.name + '成为修复Bug的冠军！TA一共修复了' + winner.value + '个Bug，占总数的' + (winner.y * 100).toFixed(2) + '%';
                specs.names = [];
                specs.data = result.data;
                specs.data.forEach(function(ele){
                    specs.names.push(ele.name);
                });
                specs.tag = '修复Bug的数目';
                specs.id = 'completion';
                drawPieChart(specs);
            };

            var findMaxValue = function(arr){
                if(arr.length === 0)
                    return null;
                else {
                    var pos = 0;
                    var value = arr[0].value;
                    for(var i = 1; i < arr.length; i++){
                        if(arr[i].value > value){
                            value = arr[i];
                            pos = i;
                        }
                    }
                    return arr[pos];
                }
            };

            var drawPieChart = function(specs) {
                var id = specs.id;
                var myChart = echarts.init(document.getElementById(id));

                var option = {
                    title: {
                        text: specs.text,
                        subtext: specs.subtext,
                        x: 'center'
                    },
                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    legend: {
                        orient: 'vertical',
                        x: 'left',
                        data: specs.names
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            mark: {
                                show: true
                            },
                            dataView: {
                                show: true,
                                readOnly: false
                            },
                            magicType: {
                                show: true,
                                type: ['pie', 'funnel'],
                                option: {
                                    funnel: {
                                        x: '25%',
                                        width: '50%',
                                        funnelAlign: 'left',
                                        max: 1548
                                    }
                                }
                            },
                            restore: {
                                show: true
                            },
                            saveAsImage: {
                                show: true
                            }
                        }
                    },
                    calculable: true,
                    series: [{
                        name: specs.tag,
                        type: 'pie',
                        radius: '55%',
                        center: ['50%', '60%'],
                        data: specs.data
                    }]
                };

                // 为echarts对象加载数据 
                myChart.setOption(option);
            };
        }
    ]);
