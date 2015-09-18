/**
 * Created by Nettle on 2015/4/19.
 */
/**
 * Created by Nettle on 2014/12/9.
 */

angular.module('angularBrick', [])
    .directive('brick', ['$rootScope', function($rootScope) {
        function link($scope, element, attrs, controller) {
            //console.log($scope.values);
            $scope.max = 0;
            $scope.brackData = new Array();
            for (var i = 0; i < $scope.values.length; ++i) {
                $scope.brackData[i] = new Array();
                for (var j = 0; j < $scope.values[i].length; ++j)
                    $scope.brackData[i][j] = {
                        day: i,
                        hour: j,
                        opc: 0,
                        val: ''
                    }
            }

            $scope.$on('punchCardChanged', function() {
                $scope.getMax();
                // console.log($scope.max);
                for (var i = 0; i < $scope.values.length; ++i) {
                    for (var j = 0; j < $scope.values[i].length; ++j) {
                        $scope.brackData[i][j].opc = ($scope.max == 0) ? 0.1 : 0.1 + 0.9 * ($scope.values[i][j] / $scope.max);
                        $scope.brackData[i][j].val = $scope.labels[i] + ' ' + j + ':00 ~ ' + (j + 1) + ':00 数目:' + $scope.values[i][j];

                    }
                }
                // console.log($scope.brackData);
                if (!$rootScope.$$phase) $rootScope.$apply(null);
            });

            $scope.getMax = function() {
                $scope.max = 0;
                for (var i = 0; i < $scope.values.length; ++i)
                    for (var j = 0; j < $scope.values[i].length; ++j)
                        if ($scope.max < $scope.values[i][j])
                            $scope.max = $scope.values[i][j];
            }
            $scope.showPunchcardDetail = function(item) {
                $scope.$emit('brickClicked', item);
            };
        }
        return {
            restrict: 'E',
            link: link,
            scope: {
                labels: '=labels',
                values: '=values',
                info: '=info',
                options: '=options'
            },
            template: '\
            <div class="angular-brick"> \
                <div class="brick-line" ng-repeat="day in brackData">\
                    <div class="label-text">{{labels[$index]}}</div>\
                    <div class="brick-group">\
                        <div class="brick" ng-repeat="item in day track by $index" style="opacity: {{item.opc}}" title="{{item.val}}" ng-click="showPunchcardDetail(item)">\
                          </div>\
                    </div>\
                </div>\
            </div>'
        }
    }]);
