angular.module('statistics')
    .factory('settings', function () {
        var init = function () {
            var spec = {
                canvasWidth: 800,
                canvasHeight: 400,
                margin: {
                    top: 30,
                    right: 40,
                    bottom: 70,
                    left: 60
                }
            };

            var that = {};
            that.filterData = function (data, spec) {
                var result = data;
                if (spec !== undefined && spec !== {}) {
                    for (var property in spec) {
                        if (spec.hasOwnProperty(property)) {
                            result = result.filter(function (d) {
                                return d[property] === spec[property];
                            });
                        }
                    }
                }
                return result;
            };

            that.jsonCopy = function (data) {
                return JSON.parse(JSON.stringify(data));
            };

            that.getSpec = function () {
                return spec;
            };

            that.setDimension = function (height, width) {
                spec.canvasHeight = height;
                spec.canvasWidth = width;
            };

            that.setRawData = function (data) {
                spec.data = that.jsonCopy(data);
            };

            that.getRawData = function () {
                return that.jsonCopy(spec.data);
            };
            return that;
        };
        return init;

    });
