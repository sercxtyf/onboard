angular.module('discussions')
    .config(['$stateProvider',
        function($stateProvider) {
            $stateProvider
                .state('company.project.discussions', {
                    url        : '/discussions',
                    templateUrl: 'discussions.html'
                })
                // 去掉手动url，换用通用的抽屉路由
                .state('company.project.discussion_detail', {
                    url        : '/discussions/{discussionId:[0-9]+}',
                    templateUrl: 'discussions.html'
                });
        }
    ])
    .controller('discussionsCtrl', ['$scope', '$http', '$location', '$rootScope', '$state', 'url', 'discussionService', 'drawer',
        function($scope, $http, $location, $rootScope, $state, url, discussionService, drawer) {
            $scope.location = $location;
            $scope.nextPage = 1;
            $scope.totalPage = 0;
            $scope.busy = false;
            $scope.refType = "";
            //get topics
            $scope.getTopics = function(successfun) {
                discussionService.getTopics($scope.nextPage).then(successfun, function() {
                    $scope.busy = false;
                });
            };
            //create new discussion
            $scope.createNewDiscussion = function($event) {
                $event.stopPropagation();
                drawer.open({
                    type: 'discussion-edit',
                    data: {
                        edit: false
                    }
                });
            };

            $scope.addMoreTopics = function() {
                if($scope.totalPage >= $scope.nextPage && $scope.busy === false) {
                    //console.log("getting data of page " + $scope.nextPage);
                    $scope.busy = true;
                    $scope.getTopics(function(data) {
                        $scope.nextPage += 1;
                        $scope.busy = false;
                        pagination();
                    });
                }
            };

            $scope.noMorePages = function() {
                return ($scope.totalPage !== 0 && $scope.totalPage < $scope.nextPage);
            };

            $scope.displayNoMorePage = function() {
                return ($scope.topics && $scope.topics.length > 20 && $scope.noMorePages());
            };

            var pagination = function() {
                // 自己实现的分页
                var $page = $("#topics");
                var $pageContainer = $page.scrollParent();
                var margin = 20;
                var scrolltrigger = 0.95;
                var scrollFun = function() {

                    var wintop = $pageContainer.scrollTop(),
                        docheight = $page.height(),
                        winheight = $pageContainer.height();

                    if((wintop / (docheight - winheight + margin)) > scrolltrigger) {
                        if($scope.busy === false) {
                            $scope.addMoreTopics();
                            $pageContainer.unbind('scroll', scrollFun);
                        }
                    }
                };
                $pageContainer.scroll(scrollFun);
            };

            //初始化，获取第一页数据
            $scope.getTopics(function(data) {
                $scope.topics = data.topics;
                $scope.totalPage = data.totalPage;
                $scope.nextPage += 1;
                pagination();
            });


            //router
            if($state.is('company.project.discussion_detail')) {
                drawer.open({
                    type: 'discussion',
                    data: {
                        id: $state.params.discussionId
                    }
                });
            }

            $scope.gotoMePage = function(id) {
                $state.go('company.me', {
                    userId: id
                });
            };

            // 置顶功能
            var stickApi = url.projectApiUrl() + '/topics/';
            var maxStick = 10;
            $scope.top = function(topic){
                $http.get(url.projectApiUrl() + '/topics-stick-count').success(function(data){
                    if(data >= maxStick){
                        alert('最多置顶10个话题，置顶数目已满！');
                        return false;
                    }
                    else {
                        $http.put(stickApi + topic.id + '/stick').success(function(data){
                            topic.stick = true;
                        });
                    }
                });
            };
            $scope.cancelTop = function(topic){
                $http.put(stickApi + topic.id + '/unstick').success(function(){
                    topic.stick = false;
                });
            };
        }
    ]);
