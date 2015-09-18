angular.module('statistics')
    .factory('activityPunchCard', ['punchCard', function (punchCard) {
        var that = punchCard();
        var spec = that.getSpec();

        spec.text = "次活动";

        var processData = function (data) {
            data.forEach(function (d) {
                d.date = d3.time.second(d.created);
            });
        };

        spec.groupData = function (data) {
            processData(data);
            var nestData = d3.nest().key(function (d) {
                return that.timeFormat(d.date);
            }).entries(data);

            spec.rfunc = function (d) {
                return d.values.length;
            };

            return nestData;
        };

        that.drawGraph = function (specs) {
            var data = that.getRawData();
            if (spec.groupData !== undefined)
                data = spec.groupData(that.filterData(data, specs));
            else {
                console.error("Group data undefined!");
                return;
            }
            that.draw(data);
        };

        return that;
    }]);
