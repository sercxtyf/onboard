/**
 * Created by SourceDark on 2015/05/31.
 */

angular.module('iteration')
    .controller('completingIterationCtrl', ['$rootScope', '$scope', '$http', '$state', '$location',
        'iterationService', 'todoService', 'url', 'dateService', 'filterFilter', 'drawer', 'richtexteditor', '$timeout', 'pieChart',
        function($rootScope, $scope, $http, $state, $location, iterationService, todoService, url, dateService,
            filterFilter, drawer, richtexteditor, $timeout, pieChart) {
            // 初值设置
            var iterationSummaryExample = '<p>关于这次迭代，</p><p>我们做的好的地方有：</p><ul><li><br></li></ul>' +
                '<p>我们做的不好的地方有：</p><ul><li><br></li></ul>' +
                '<p>下次迭代中我们应当这样改进：</p><ul><li><br></li></ul>';


            // 富文本编辑器
            var iterationSummaryEditor = $('#iterationSummaryEditor');
            $scope.initialIterationSummaryEditor = function() {};

            // 获取当前迭代信息
            iterationService.getiterationById(true, url.iterationId()).then(function(iteration) {
                $scope.activeIteration = iteration;
                $scope.startDateStr = new Date(iteration.startTime).Format("yyyy-MM-dd");
                $scope.endDateStr = new Date(iteration.endTime).Format("yyyy-MM-dd");
                $scope.allStories = filterFilter(iteration.iterables, {
                    type: "story"
                });
                $scope.stories = $scope.allStories;
                $scope.allBugs = filterFilter(iteration.iterables, {
                    type: "bug"
                });
                $scope.bugs = $scope.allBugs;
                $scope.allSteps = filterFilter(iteration.iterables, {
                    type: "step"
                });
                $scope.steps = $scope.allSteps;

                $scope.updateStoriesAndStepsAndBugs();
                // 统计完成的需求数
                $scope.completedStoryCount = 0;
                for (var story in $scope.stories) {
                    if ($scope.stories[story].completed) {
                        $scope.completedStoryCount++;
                    }
                }
                if ($scope.completedStoryCount < $scope.stories.length * 0.6) {
                    $scope.storyStatus = 0;
                } else if ($scope.completedStoryCount < $scope.stories.length) {
                    $scope.storyStatus = 1;
                } else {
                    $scope.storyStatus = 2;
                }

                // 统计完成的Bug数
                // console.log($scope.bugs);
                $scope.completedBugCount = 0;
                for (var bug in $scope.bugs) {
                    if ($scope.bugs[bug].iterationStatus == 'closed') {
                        // console.log($scope.bugs[bug].iterationStatus);
                        $scope.completedBugCount++;
                    }
                }
                if ($scope.completedBugCount < $scope.bugs.length * 0.6) {
                    $scope.bugStatus = 0;
                } else if ($scope.completedBugCount < $scope.bugs.length) {
                    $scope.bugStatus = 1;
                } else {
                    $scope.bugStatus = 2;
                }

                $scope.closedBugs = $scope.bugs.filter(function(ele) {
                    if (ele.iterationStatus == 'closed') {
                        ele.assigneeName = ele.assignee.name;
                        return true;
                    } else
                        return false;
                });

                $scope.steps = $scope.steps.map(function(ele) {
                    ele.assigneeName = ele.assigneeDTO.name;
                    return ele;
                });
                // update steps
                $scope.closedSteps = $scope.steps.filter(function(ele) {
                    return ele.status == 'closed';
                });
                $scope.openSteps = $scope.steps.filter(function(ele) {
                    return ele.status != 'closed';
                });
                drawBugsPieChart($scope.closedBugs);
                drawClosedStepsPieChart($scope.closedSteps);
                drawOpenStepsPieChart($scope.openSteps);

                // Summary
                iterationSummaryEditor.summernote();
                $(iterationSummaryEditor).code($scope.activeIteration.summary);
            });


            $scope.updateStoriesAndStepsAndBugs = function() {
                // 更新story
                $scope.stories.sort(function(a, b) {
                    if (a.completed != b.completed) return a.completed < b.completed;
                    return a.id > b.id;
                });
                for (var story in $scope.stories) {
                    if (!$scope.stories[story].description) {
                        $scope.stories[story].description = '<p>未设置详细描述</p>';
                    }
                    if (!$scope.stories[story].acceptanceLevel) {
                        $scope.stories[story].acceptanceLevel = '<p>未设置验收标准</p>';
                    }
                }

                // 更新Bugs
                $scope.bugs.sort(function(a, b) {
                    if (a.completed != b.completed) return a.completed < b.completed;
                    return a.id > b.id;
                });
            };


            // pie chart
            var defaultName = '全体成员';
            $scope.statUser = {
                name: defaultName
            };
            $scope.defaultuser = {
                name: defaultName
            };
            var drawClosedStepsPieChart = function(data) {
                $('#pie-closedStepsChart').children().remove();
                var result = pieChart.groupTodoData(data, $scope.statUser);
                var specs = {
                    id: '#pie-closedStepsChart',
                    title: '所有成员一共完成了' + result.total + '个迭代中的任务',
                    name: '完成的任务数目',
                    data: result.data
                };
                pieChart.draw(specs);
            };

            var drawOpenStepsPieChart = function(data) {
                $('#pie-openStepsChart').children().remove();
                var result = pieChart.groupTodoData(data, $scope.statUser);
                var specs = {
                    id: '#pie-openStepsChart',
                    title: '所有成员一共遗留了' + result.total + '个迭代中的任务',
                    name: '遗留的任务数目',
                    data: result.data
                };
                pieChart.draw(specs);
            };

            var drawBugsPieChart = function(data) {
                $('#pie-bugsChart').children().remove();
                var result = pieChart.groupTodoData(data, $scope.statUser);
                var specs = {
                    id: '#pie-bugsChart',
                    title: '所有成员一共修复了' + result.total + '个Bug',
                    name: '修复的Bug数目',
                    data: result.data
                };
                pieChart.draw(specs);
            };

            $scope.$on('sliced', function(event, userName) {
                if ($scope.statUser.name === userName) {
                    $scope.statUser.name = defaultName;
                } else {
                    $scope.statUser.name = userName;
                }
                drawClosedStepsPieChart($scope.closedSteps);
                drawOpenStepsPieChart($scope.openSteps);
                drawBugsPieChart($scope.closedBugs);
                if (!$scope.$$phase) $scope.$apply(null);
            });

            // Callbacks
            $scope.showDetail = function($event) {
                $($event.currentTarget).next().slideToggle(200);
            };

            $scope.openStory = function($event, $storyId) {
                $event.stopPropagation();
                drawer.open({
                    type: 'story',
                    params: {
                        id: $storyId
                    }
                });
            }
            $scope.openBug = function($event, $bugId) {
                $event.stopPropagation();
                drawer.open({
                    type: 'bug',
                    params: {
                        id: $bugId
                    }
                });
            };


            $scope.user = {
                name: defaultName
            };
            $scope.notListen = true;

            $scope.$on('chooseUser', function(event, user) {
                if (user === undefined) {
                    $scope.user = $scope.defaultuser;
                    $scope.stories = $scope.allStories;
                    $scope.bugs = $scope.allBugs;
                    $scope.steps = $scope.allSteps;
                } else {
                    $scope.user = user;
                    $scope.bugs = $scope.allBugs.filter(function(ele) {
                        return ele.assigneeId === user.id;
                    });
                    $scope.steps = $scope.allSteps.filter(function(ele) {
                        return ele.assigneeId === user.id;
                    });
                    $scope.stories = $scope.allStories.filter(function(story) {
                        for (var i = 0; i < $scope.steps.length; i++) {
                            if ($scope.steps[i].attachId === story.id)
                                return true;
                        }
                        return false;
                    });
                }
                $scope.updateStoriesAndStepsAndBugs();
            });


            $scope.completeIteration = function() {
                if (confirm("确定结束当前迭代？")) {
                	$scope.activeIteration.summary = iterationSummaryEditor.code();
                    $scope.activeIteration.complete().then(function() {
                        $state.go('company.project.iteration.activeIteration.todos');
                    });
                }
            };

            $scope.cancelOperation = function() {
                $scope.activeIteration.summary = iterationSummaryEditor.code();
                $scope.activeIteration.update().then(
                    function() {
                        $state.go('company.project.iteration.activeIteration');
                    }
                ).catch(
                    function() {
                        alert('保存迭代总结失败！');
                    }
                );
            };
            $scope.saveOperation = function() {
                $scope.activeIteration.summary = iterationSummaryEditor.code();
                $scope.activeIteration.update().then(
                    function() {
                        $state.go('company.project.iteration.complecoedIterations');
                    }
                ).catch(
                    function() {
                        alert('保存迭代总结失败！');
                    }
                );
            };
            $scope.getSteps = function(story) {
                var steps = [];
                for (var i = 0; i < $scope.activeIteration.iterables.length; i++) {
                    if ($scope.activeIteration.iterables[i].type === "step" && $scope.activeIteration.iterables[i].attachId === story.id) {
                        if ($scope.user.name == defaultName || $scope.activeIteration.iterables[i].assigneeId == $scope.user.id)
                            steps.push($scope.activeIteration.iterables[i]);
                    }
                }
                //console.log(story);
                //console.log($scope.activeIteration.iterables);
                //console.log(steps);
                return steps;
            }
        }
    ]);
