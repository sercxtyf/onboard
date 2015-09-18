angular.module('util')
    .service('burndownChart', ['filterFilter', function (filterFilter) {
        this.showBurnDownChart = function (iteration, container, filter) {
            if(iteration.id == undefined || iteration.boardables == undefined){
                return;
            }
            if(filter){
                var boardables = filterFilter(iteration.boardables, filter);
            }else{
                var boardables = iteration.boardables;
            }
            var startTime = new Date(iteration.startTime);
            var endTime = new Date(iteration.endTime);
            var todoCount = boardables.length;
            var closedboardables = filterFilter(boardables, {status: "closed"});
            var sorctedClosedboardables = [];
            if(closedboardables.length > 0){
                sorctedClosedboardables = getBurnDownChartData(closedboardables, startTime, endTime);
            }
            var chartDotData = [];
            //初始化起始点，起始点为任务总数
            chartDotData.push({
                "x": startTime,
                "y": todoCount
            });
            var leftTodoCount = boardables.length;
            //console.log(sorctedClosedboardables);
            if (sorctedClosedboardables.length > 0) {
                for (var i = 0; i < sorctedClosedboardables.length; i++) {
                    leftTodoCount = leftTodoCount - sorctedClosedboardables[i].length;
                    var time;
                    if (sorctedClosedboardables[i][0].completedTime - startTime < 0) {
                        time = startTime;
                    } else {
                        time = new Date(sorctedClosedboardables[i][0].completedTime);
                    }
                    chartDotData.push({
                        "x": time,
                        "y": leftTodoCount
                    });
                }
            }
            //初始化终止点，如果iteration尚未完成，终止时间为当天，任务数为当前剩余任务数
            var last = iteration.status == "completed" ? endTime : d3.time.day(new Date());
            if (chartDotData.length == 0 || !isOneDay(last, chartDotData[chartDotData.length - 1].x)) {
                chartDotData.push({
                    "x": last,
                    "y": todoCount - closedboardables.length
                });
            }
            var main = [];
            main.push({
                "className": ".expected",
                "data": [
                    {
                        "x": startTime,
                        "y": todoCount
                    },
                    {
                        "x": endTime,
                        "y": 0
                    }
                ]
            });
            if(chartDotData.length == 1){
                if(iteration.status != "completed"){
                    main.push({
                        "className": ".actual",
                        "data": chartDotData
                    })
                }
            }else{
                for(var i = 1; i < chartDotData.length; i++){
                    main.push({
                        "className": ".actual .fix" + i,
                        "data": [chartDotData[i - 1], chartDotData[i]]
                    })
                }
            }
            var data = {
                "xScale": "time",
                "yScale": "linear",
                "type": "line-dotted",
                "yMax": todoCount + 3,
                "tickHintY": todoCount + 3,
                "main": main
            };
            //console.log(data);
            var opts = {
                paddingLeft : 50,
                paddingTop : 20,
                paddingRight : 10,
                axisPaddingLeft : 25,
                yMin: 0,
                "dataFormatX": function (x) {
                    if(typeof(x) == "number"){
                        x = new Date(x);
                    }
                    return d3.time.format('%Y-%m-%d').parse(d3.time.format('%Y-%m-%d')(x));
                },
                "tickFormatX": function (x) {
                    if(typeof(x) == "number"){
                        x = new Date(x);
                    }
                    return d3.time.format('%Y-%m-%d')(x);
                }
            };
            new xChart('line-dotted', data, container, opts);
        };

        /**
         * 按完成时间比较任务，未完成的任务完成时间为无穷大
         */
        function campareTodoByCompleteTime(todoA, todoB) {
            if (!todoA.status == "closed" && !todoB.status == "closed") {
                return 0;
            } else if (!todoA.status == "closed" && todoB.status == "closed") {
                return Number.MAX_VALUE;
            } else if (todoA.status == "closed" && !todoB.status == "closed") {
                return -Number.MAX_VALUE;
            } else {
                return todoA.completedTime - todoB.completedTime;
            }
        }

        /**
         * 通过todo信息计算出燃尽图的数据信息
         */
        function getBurnDownChartData(boardables, startTime, endTime) {
            boardables = angular.copy(boardables);
            for(var i in boardables){
                if(boardables[i].completedTime - startTime < 0){
                    boardables[i].completedTime = startTime.getTime();
                }else if(boardables[i].completedTime - endTime > 0){
                    boardables[i].completedTime = endTime.getTime();
                }
            }
            boardables.sort(campareTodoByCompleteTime);
            var sorctedboardables = [[boardables[0]]];
            for (var i = 1; i < boardables.length; i++) {
                //已完成的任务按完成日期升序放进不同列表里
                if (isOneDay(sorctedboardables[sorctedboardables.length - 1][0].completedTime, boardables[i].completedTime)) {
                    if (boardables[i].completedTime - startTime < 0) {
                        sorctedboardables[0].push(boardables[i]);
                    } else {
                        sorctedboardables[sorctedboardables.length - 1].push(boardables[i]);
                    }
                } else {
                    sorctedboardables.push([boardables[i]]);
                }
            }
            return sorctedboardables;
        }

        /**
         * 判断是否为同一天
         */
        function isOneDay(dateA, dateB) {
            var formatter = d3.time.format('%Y-%m-%d');
            if(typeof(dateA) == "number"){
                dateA = new Date(dateA);
            }
            if(typeof(dateB) == "number"){
                dateB = new Date(dateB);
            }
            return formatter(dateA) == formatter(dateB);
        }
    }]
);