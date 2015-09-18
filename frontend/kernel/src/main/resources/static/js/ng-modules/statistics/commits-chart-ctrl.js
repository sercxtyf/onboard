angular.module('statistics')
    .controller('commitsChartCtrl', ['$scope', '$http', 'commitsTrend', 'commitStats', 'pieChart',

        function($scope, $http, init, commitStats, pieChart) {
            var commitsTrend = init();
            $scope.sync = false;
            $scope.selfTab = 3;

            var dataRetrieved = function() {
                $scope.$emit('dataRetrieved');
            };

            var requestSent = function() {
                $scope.$emit('requestSent');
            };

            // commitsChart graph
            var id = '#commitsChart';
            var $chart = $('#commitsChart');
            var height = parseInt($chart.css('height'), 10);
            var width = parseInt($chart.css('width'), 10);

            var addLoading = function() {
                $chart.find('.loading').remove();
                d3.select(id).select("svg")
                    .append("text")
                    .attr("class", "loading")
                    .text("Loading...")
                    .attr("x", function() {
                        return width / 2;
                    })
                    .attr("y", function() {
                        return height / 2;
                    });
            };

            var drawCommitsChartSetUp = function() {
                commitsTrend.setDimension(height, width);
                commitsTrend.setupCanvas(id);
            };

            var reDrawCommitsChart = function() {
                var spec = {};
                if ($scope.user.id !== 0)
                    spec.userName = $scope.user.name;
                $chart.children().remove();
                commitsTrend.drawGraph(spec);
            };

            var getData = function() {
                if ($scope.busy === true)
                    return;
                $scope.sync = true;
                requestSent();
                addLoading();
                commitStats.getCommitStatsData($scope.start - 0, $scope.end - 0).then(
                    function(data) {
                        //$scope.commits = data;
                        commitsTrend.setRawData(data);
                        //console.log(data);
                        if (data.length !== 0) {
                            $scope.hasData = true;
                            reDrawCommitsChart();
                            $('#pie-commitsChart').css('width', width);
                            drawPieChart(data);
                        } else {
                            $scope.hasData = false;
                            reDrawCommitsChart();
                            addLoading();
                            $('.loading').text('没有数据^^');
                            $('#pie-commitsChart').children().remove();
                        }
                        dataRetrieved();
                    },
                    function() {
                        dataRetrieved();
                        $('.loading').remove();
                        alert('获取数据超时！');
                    });
            };

            $scope.$on('datesChanged', function(event, tab) {
                if ($scope.selfTab === tab) {
                    getData();

                } else {
                    $scope.sync = false;
                }
            });

            $scope.$on('userChanged', function(event, tab) {
                if ($scope.selfTab === tab) {
                    if ($scope.sync) {
                        reDrawCommitsChart();
                    } else {
                        getData();
                    }

                } else {
                    $scope.sync = false;
                }
            });

            $scope.$on('tabSwitch', function(event, tab) {
                if ($scope.selfTab === tab && !$scope.sync) {
                    getData();
                }
            });

            $scope.gotoCommit = function($event) {
                //console.log('goto commit...');
                $event.preventDefault();
            };
            drawCommitsChartSetUp();
            commitsTrend.setGroupCommits();
            getData();

            // Pie
            var drawPieChart = function(data) {
                $('#pie-commitsChart').children().remove();
                var result = pieChart.groupCommitsData(data, $scope.currentUser);
                var specs = {
                    id: '#pie-commitsChart',
                    title: '所有成员一共提交了' + result.total + '个commit',
                    name: '代码提交次数',                    
                    data: result.data
                };
                pieChart.draw(specs);
            };
        }
    ]);
