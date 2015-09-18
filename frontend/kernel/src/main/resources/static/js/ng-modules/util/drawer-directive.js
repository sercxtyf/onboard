/**
 * Created by harttle on 12/2/14.
 */

angular.module('util')
    .directive('drawer', ['$http', '$templateCache', '$compile', '$document', 'drawer', '$timeout', '$location', 'collectionService',
        function ($http, $templateCache, $compile, $document, drawer, $timeout, $location, collectionService) {
            return {
                restrict: 'A',
                link: function (scope, ele) {

                    var $drawer = ele;
                    var $wrapper = ele.find('.drawer-wrapper');

                    $document.find('#main-content, #main-nav').bind('click', function () {
                        if ($drawer) $drawer.drawer('hide');
                    });

                    scope.open = function (option) {
                        $.extend(scope, option.params, option.data);
                        scope.type = option.type;
                        if (scope.id != undefined) {
	                        collectionService.isCollected(scope.type, scope.id).then(function(data) {
	                            if (data.length > 0) {
	                                scope.isCollected = true;
	                                scope.colleInfo = data[0];
	                            }   else {
	                                scope.isCollected = false;
	                                scope.colleInfo = null;
	                            }
	                        });
                        }
                        $drawer.attr('class', 'modal drawer '+ option.sizeClass);
                        $drawer.drawer('show');

                        $http.get(option.templateUrl, {cache: $templateCache})
                            .success(function (tplContent) {

                                //// compile template
                                //$wrapper.html($compile(tplContent)(scope));

                                // compile template
                                //$wrapper.html($compile(tplContent)(scope));
                                //顺序,html和controller
                                $wrapper.html(tplContent);
                                $compile($wrapper)(scope);

                                $drawer.on('show.bs.drawer', function () {
                                    if (!option.data) { // set access url
                                        for (var i in option.params)
                                            $location.search(i, option.params[i]);
                                        $location.search('type', option.type);
                                    }
                                }).on('hide.bs.drawer', function () {
                                    // jquery triggered event should manually $apply to angular framework
                                    $timeout(function () {
                                        $wrapper.html('');
                                        for (var i in option.params) $location.search(i, null);
                                        $location.search('type', null);
                                    });
                                });
                            });
                    };

                    scope.close = function () {
                        if ($drawer) $drawer.drawer('hide');
                    };

                    scope.collect = function() {
                        //console.log(scope.isCollected);
                        if (scope.isCollected) {
                            collectionService.delCollection(scope.colleInfo.id).then(function (data) {
                                scope.isCollected = !scope.isCollected;
                                //console.log(data);
                            });
                        }
                        else {
                            collectionService.addCollection(scope.type, scope.id).then( function(data) {
                                scope.isCollected = !scope.isCollected;
                                scope.colleInfo = data;
                                //console.log(data);
                            });
                        }
                    };
                    drawer.setDelegate(scope);
                }
            }
        }]);