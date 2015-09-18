(function() {
    angular.module('iteration')
        .controller('iterationCodeStatsCtrl', ['$scope', '$http', '$state', 'commitsTrend', 'commitsLocTrend', 'commitsPunchCard', 'url', 'user', 'iterationService', 'pieChart', 'commitStats',
            function($scope, $http, $state, commitsTrend, commitsLocTrend, commitsPunchCard, url, user, iterationService, pieChart, commitStats) {
                var LocChart = commitsLocTrend();
                var commitsChart = commitsTrend();
                var defaultName = '全体成员';
                $scope.user = {
                    name: defaultName
                };
                $scope.tab = 1;
                $scope.isSet = function(checkTab) {
                    return $scope.tab === checkTab;
                };
                $scope.setTab = function(activeTab, $event) {
                    if ($scope.tab !== activeTab) {
                        $scope.$broadcast('tabSwitch', activeTab);
                    }
                    $scope.tab = activeTab;
                    $event.stopPropagation();
                };

                $scope.graphInfo = function() {
                    if (this.tab === 1) {
                        return '各个整点的代码提交次数';
                    } else if (this.tab === 2) {
                        return '代码提交影响行数趋势图';
                    } else {
                        return '代码提交次数趋势图';
                    }
                };

                $scope.busy = false;
                var punchCardInitialized = false;

                $scope.ready = function() {
                    return ($scope.activeIteration !== undefined && $scope.activeIteration.id !== undefined);
                };

                // graphs
                var id = ['#commitsPunchCard', '#LocChart', '#commitsChart'];
                var $chart = $('#commitsPunchCard, #LocChart, #commitsChart');
                var height = parseInt($chart.first().css('height'), 10);
                var width = parseInt($chart.first().css('width'), 10);
                var pie_width = parseInt($('.pie-parallel').first().css('width'), 10);

                var addLoading = function() {
                    $chart.find('.loading').remove();
                    for (var key in id) {
                        d3.select(id[key]).select("svg")
                            .append("text")
                            .attr("class", "loading")
                            .text("Loading...")
                            .attr("x", function() {
                                return width / 2;
                            })
                            .attr("y", function() {
                                return height / 2;
                            });
                    }
                };

                var drawCommitsChartSetUp = function() {
                    commitsPunchCard.setDimension(height, width);
                    commitsPunchCard.setupCanvas('#commitsPunchCard');
                    LocChart.setDimension(height, width);
                    LocChart.setupCanvas('#LocChart');
                    commitsChart.setDimension(height, width);
                    commitsChart.setupCanvas('#commitsChart');
                };

                var reDrawCommitsChart = function() {
                    var spec = {};
                    if ($scope.user.name !== defaultName)
                        spec.userName = $scope.user.name;
                    $chart.children().remove();
                    commitsPunchCard.drawGraph(spec);
                    LocChart.drawGraph(spec);
                    commitsChart.drawGraph(spec);
                };

                //pie chart
                var drawCommitsPieChart = function(data) {
                    $('#pie-commitsChart').children().remove();
                    var result = pieChart.groupCommitsData(data, $scope.user);
                    var specs = {
                        id: '#pie-commitsChart',
                        title: '所有成员一共提交了' + result.total + '个commit',
                        name: '代码提交次数',
                        data: result.data
                    };
                    pieChart.draw(specs);
                };
                var drawLocPieChart = function(data) {
                    $('#pie-LocChart').children().remove();
                    $('#pie-LocMinusChart').children().remove();
                    // 增加行数
                    var result = pieChart.groupLocData(data, $scope.user);
                    var specs = {
                        id: '#pie-LocChart',
                        title: '所有成员一共增加了' + result.total + '行代码',
                        name: '代码行数',
                        data: result.data
                    };
                    pieChart.draw(specs);
                    // 删除行数
                    result = pieChart.groupLocMinusData(data, $scope.user);
                    specs = {
                        id: '#pie-LocMinusChart',
                        title: '所有成员一共删除了' + result.total + '行代码',
                        name: '代码行数',
                        data: result.data
                    };
                    pieChart.draw(specs);
                };

                var showData = function() {
                    var param = {
                        start: $scope.start,
                        end: $scope.end
                    };
                    $scope.busy = true;
                    addLoading();
                    commitStats.getCommitStatsData($scope.start - 0, $scope.end - 0).then(function(data) {
                        commitsPunchCard.setRawData(data);
                        LocChart.setRawData(data);
                        commitsChart.setRawData(data);
                        $scope.commits = data;
                        punchCardInitialized = true;
                        //console.log(data);
                        if (data.length !== 0) {
                            $scope.hasData = true;
                            reDrawCommitsChart();
                            $('#pie-LocChart').css('width', pie_width);
                            $('#pie-LocMinusChart').css('width', pie_width);
                            drawLocPieChart(data);
                            $('#pie-commitsChart').css('width', pie_width);
                            drawCommitsPieChart(data);
                        } else {
                            $scope.hasData = false;
                            reDrawCommitsChart();
                            addLoading();
                            $('.loading').text('没有数据^^');
                            $('#pie-LocChart').children().remove();
                            $('#pie-LocMinusChart').children().remove();
                            $('#pie-commitsChart').children().remove();
                        }
                        $scope.busy = false;
                    }, function() {
                        $scope.busy = false;
                        $('.loading').remove();
                        alert('获取数据超时！');
                    });
                };

                drawCommitsChartSetUp();
                commitsPunchCard.setGroupCommits();
                commitsChart.setGroupCommits();
                //LocChart.setGroupLoc();

                var initCompletedIteration = function() {
                    if ($scope.activeIteration != undefined && $scope.activeIteration.id != undefined) {
                        $scope.start = $scope.activeIteration.startTime;
                        $scope.end = $scope.activeIteration.endTime;
                        showData();
                    }
                };
                $scope.refresh = function() {
                    initCompletedIteration();
                };

                $scope.$watch("activeIteration", function() {
                    initCompletedIteration();
                });

                $scope.$on('sliced', function(event, userName) {
                    if ($scope.user.name === userName) {
                        $scope.user.name = defaultName;
                    } else {
                        $scope.user.name = userName;
                    }
                    if (punchCardInitialized) {
                        reDrawCommitsChart();
                        drawLocPieChart($scope.commits);
                        drawCommitsPieChart($scope.commits);
                        if (!$scope.$$phase) $scope.$apply(null);
                    }
                });
            }
        ]);

})();
