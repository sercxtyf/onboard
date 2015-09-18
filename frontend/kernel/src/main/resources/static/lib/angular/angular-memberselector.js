/**
 * Created by Nettle on 2014/12/9.
 */

angular.module('angularMemberselector', [])
    .directive('memberselector', [function() {
        function link(scope, element, attrs, controller) {
            element.addClass('memberSelector');

            var handleInputMemberList = function() {
                if(scope.members != undefined && scope.members.length > 0) {
                    scope.tags = $.unique(scope.members.map(function(member) {
                        if(member.tag != undefined) return member.tag;
                        else {
                            scope.useTag = false;
                            return '未使用Tag';
                        }
                    }));

                    if(scope.useTag == undefined) scope.useTag = true;

                    scope.tags = $.merge([{ name: '所有人', value: '__ALL__' }], scope.tags.map(function(tag) {
                        return {
                            name : tag,
                            value: tag
                        }
                    }));

                    scope.members = scope.members.map(function(user) {
                        if(user.lock == undefined) user.lock = false;
                        if(user.lock == 'false') user.lock = false;
                        if(user.lock == 'true' || user.lock == true) user.lock = true;

                        if(user.selected == undefined) user.selected = false;
                        if(user.selected == 'false') user.selected = false;
                        if(user.selected == 'true' || user.selected == true) user.selected = true;
                        return user;
                    });

                    scope.ret = scope.members.filter(function(user) {
                        return user.selected
                    }).map(function(user) {
                        return user.id
                    });
                }
            };

            scope.$on('updateMemberSelectorInputUserList', function(event, args) {
                handleInputMemberList();
            });

            scope.$watch('members.length', function() {
                handleInputMemberList();
            });

            scope.$watch('option', function() {
                if(scope.option != undefined) {
                    if(scope.option.type == 'image') scope.useImage = true;
                    else scope.useImage = false;

                    if(scope.option.imgHeight == undefined) scope.option.imgHeight = '40px';
                    if(scope.option.imgWidth == undefined) scope.option.imgWidth = '40px';
                    if(scope.option.column == undefined) scope.option.column = 1;
                    if(scope.useImage == false) scope.checkBoxWidth = parseFloat(100 / scope.option.column) + '%';
                    else scope.checkBoxWidth = 'auto';
                }
            });

            scope.getStatus = function(idx) {
                var ipt = 'input';
                if(scope.tags[idx].value != '__ALL__') ipt = ipt + '[data-tag=' + scope.tags[idx].value + ']';
                if(element.find(ipt + ':checked').length == element.find(ipt).length) {
                    return 'ALL' + scope.tags[idx].name;
                } else if(element.find(ipt + ':checked').length == 0) {
                    return scope.tags[idx].name;
                } else {
                    return 'PART' + scope.tags[idx].name;
                }
            }

            scope.memberclick = function(idx) {
                if(!scope.members[idx].lock) scope.members[idx].selected = !scope.members[idx].selected;
                scope.ret = scope.members.filter(function(user) {
                    return user.selected
                }).map(function(user) {
                    return user.id
                });
            }

            scope.tagclick = function(value) {
                var ipt = 'input';
                if(value != '__ALL__') ipt = ipt + '[data-tag=' + value + ']';
                if(element.find(ipt + ':checked').length < element.find(ipt).length) {
                    scope.members.forEach(function(user) {
                        if((value == '__ALL__' || user.tag == value) && !user.lock) user.selected = true;
                    })
                } else {
                    scope.members.forEach(function(user) {
                        if((value == '__ALL__' || user.tag == value) && !user.lock) user.selected = false;
                    })
                }
                scope.ret = scope.members.filter(function(user) {
                    return user.selected
                }).map(function(user) {
                    return user.id
                });
            }

            scope.updateRet = function() {
                scope.ret = scope.members.filter(function(user) {
                    return user.selected
                }).map(function(user) {
                    return user.id
                });
            }
        }

        return {
            restrict: 'E',
            link    : link,
            scope   : {
                members: '=memberselectorUserList',
                option : '=memberselectorOption',
                ret    : '=memberselectorReturn'
            },
            template: '\
            <div class="memberSelector-tags-row" ng-show="useTag" ng-cloak="">\
                <div class="memberSelector-tag" ng-repeat="tag in tags" data-value="{{tag.value}}" ng-click="tagclick(tag.value)">{{ getStatus($index) }}</div>\
            </div>\
            <div class="memberSelector-member" ng-repeat="user in members track by $index" ng-class="{\'locked\': user.lock && option.type == \'image\', \'selected\': user.selected && option.type == \'image\'}" ng-style="{width: checkBoxWidth}">\
                <input ng-hide="option.type == \'image\'" type="checkbox" ng-model="user.selected" ng-click="updateRet()" data-tag="{{user.tag}}" data-id="{{ $index }}" ng-disabled="user.lock">\
                <label ng-hide="option.type == \'image\'" ng-click="memberclick($index)" >{{ user.title }}</label>\
                <div ng-show="option.type == \'image\'" ng-click="memberclick($index)" class="memberSelector-img" title="{{user.title}}">\
                    <img ng-src="{{ user.img }}"\
                        ng-attr-style="height: {{option.imgHeight}};\
                        width: {{option.imgWidth}};\
                        background-size: {{option.imgHeight}} {{option.imgWidth}};\
                        -webkit-background-size: {{option.imgHeight}} {{option.imgWidth}};\
                        "/>\
                </div>\
            </div> '
        }
    }]);
