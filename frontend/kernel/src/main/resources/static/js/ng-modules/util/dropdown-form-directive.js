angular.module('util')
    .directive('dropdownForm', ["$document", function($document) {
        return {
            scope: {
                isOpen: '=?'
            },
            link: function($scope, element) {
                function resetIsOpen(event){
                    if ( event && element && element[0].contains(event.target) || event.target.hasAttribute("dropdown-form-toggle")) {
                        return;
                    }
                    $(element).hide();
                    $document.unbind('click', resetIsOpen);
                }
                $scope.$watch('isOpen', function(value) {
                    if($(element).css("display") =="block"){
                        $(element).hide();
                        $document.unbind('click', resetIsOpen);
                    }else{
                        $(element).show();
                        $document.bind('click', resetIsOpen);
                    }
                });
            }
        };
    }])