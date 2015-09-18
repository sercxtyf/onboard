(function() {
    angular.module('projectApp')
        .controller('commitsPunchCardCtrl', ['$scope', '$http', 'commitsPunchCard', 'commitStats',

            function($scope, $http, commitsPunchCard, commitStats) {
                $scope.sync = false;
                $scope.selfTab = 1;
                //$scope.commits = [];

                var dataRetrieved = function() {
                    $scope.$emit('dataRetrieved');
                };

                var requestSent = function() {
                    $scope.$emit('requestSent');
                };

                // punchCard graph
                var id = '#commitsPunchCard';
                var $chart = $('#commitsPunchCard');
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

                var drawCommitsPunchCardSetUp = function() {
                    commitsPunchCard.setDimension(height, width);
                    commitsPunchCard.setupCanvas(id);
                };

                var reDrawCommitsPunchCard = function() {
                    var spec = {};
                    if ($scope.user.id !== 0)
                        spec.userName = $scope.user.name;
                    $chart.children().remove();
                    commitsPunchCard.drawGraph(spec);
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
                            window.c = data;
                            commitsPunchCard.setRawData(data);
                            //console.log(data);
                            if (data.length !== 0) {
                                reDrawCommitsPunchCard();
                            } else {
                                reDrawCommitsPunchCard();
                                addLoading();
                                $('.loading').text('没有数据^^');

                            }
                            dataRetrieved();
                        },
                        function() {
                            dataRetrieved();
                            $('.loading').remove();
                            alert('获取数据失败！');
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
                            reDrawCommitsPunchCard();
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
                    $event.preventDefault();
                };
                drawCommitsPunchCardSetUp();
                commitsPunchCard.setGroupCommits();
                getData();
            }
        ]);

})();
