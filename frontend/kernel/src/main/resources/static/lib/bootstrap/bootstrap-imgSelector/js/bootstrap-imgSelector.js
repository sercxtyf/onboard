(function($) {
    var imgData = [];
    $.fn.setImgSelector = function (option) {
        var defaultOption = {
            imgHeight: '20px',
            imgWidth: '20px'
        };
        //console.log(option.data);
        imgData = option.data;
        $.extend(defaultOption, option);

        // init div
        $(this).html(''); // clear
        // insert div.tags
        var tags = new Object;
        var allTot = imgData.length, allSelected = 0;
        for (var key in imgData) {
            if (tags[ imgData[key].tag ] == undefined)
                tags[ imgData[key].tag ] = {tot: 0, selected: 0};
            tags[ imgData[key].tag].tot ++;
        }
        var tagDiv = $('<div></div>'), tagrowDiv = $('<div></div>');
        tagrowDiv.addClass('iselector-tag-row');
        tagDiv.addClass('iselector-tag');

        tagDiv.attr('data-name', '__ALL__');
        tagDiv.text('所有人');
        tagDiv.clone().appendTo(tagrowDiv);
        for (var key in tags) {
            tagDiv.attr('data-name', key);
            tagDiv.text(key);
            tagDiv.clone().appendTo(tagrowDiv);
        }
        tagrowDiv.appendTo($(this));

        // insert div.imgs
        var imgDiv = $('<div></div>');
        imgDiv.addClass('iselector-img');

        imgDiv.css({height: defaultOption.imgHeight, width: defaultOption.imgWidth, 'background-size': defaultOption.imgHeight + ' ' + defaultOption.imgWidth});
        for (var key in imgData) {
            imgDiv.css({'background-image': 'url("'+imgData[key].img+'")'});
            imgDiv.attr('title', imgData[key].title);
            imgDiv.attr('data-id', key);
            imgDiv.clone().appendTo($(this));
        }

        // action
        function allChange(value) {
            allSelected += value;
            if (allSelected == 0) {
                $('.iselector-tag[data-name="__ALL__"]').html('所有人');
                for (var key in tags) {
                    $('.iselector-tag[data-name="' + key + '"]').html(key);
                    tags[key].selected = 0;
                }
            }   else if (allSelected == allTot) {
                $('.iselector-tag[data-name="__ALL__"]').html('<span class="glyphicon glyphicon-ok"></span> 所有人');
                for (var key in tags) {
                    $('.iselector-tag[data-name="' + key + '"]').html('<span class="glyphicon glyphicon-ok"></span> ' + key);
                    tags[key].selected = tags[key].tot;
                }
            }   else {
                $('.iselector-tag[data-name="__ALL__"]').html('<span class="glyphicon glyphicon-stop"></span> 所有人');
            }
        }

        $('.iselector-tag').click( function() {
            var tag = $(this).attr('data-name');
            if (tag == '__ALL__') {
                if (allSelected == 0) {
                    for (var key in imgData) {
                        $('.iselector-img[data-id="' + key + '"]').addClass('selected');
                        imgData[key].selected = true;
                    }
                    allChange(-allSelected);
                    allChange(allTot);
                }   else if (allSelected == allTot) {
                    for (var key in imgData){
                        $('.iselector-img[data-id="' + key + '"]').removeClass('selected');
                        imgData[key].selected = false;
                    }
                    allChange(-allSelected);
                }   else {
                    for (var key in imgData) {
                        $('.iselector-img[data-id="' + key + '"]').addClass('selected');
                        imgData[key].selected = true;
                    }
                    allChange(-allSelected);
                    allChange(allTot);
                }
            }   else {
                if (tags[tag].selected == 0) {
                    for (var key in imgData)
                        if (imgData[key].tag == tag) {
                            $('.iselector-img[data-id="' + key + '"]').addClass('selected');
                            imgData[key].selected = true;
                        }
                    $('.iselector-tag[data-name="' + tag + '"]').html('<span class="glyphicon glyphicon-ok"></span> ' + tag);
                    tags[tag].selected = tags[tag].tot;
                    allChange(tags[tag].tot);
                } else if (tags[tag].selected == tags[tag].tot) {
                    for (var key in imgData)
                        if (imgData[key].tag == tag) {
                            $('.iselector-img[data-id="' + key + '"]').removeClass('selected');
                            imgData[key].selected = false;
                        }
                    $('.iselector-tag[data-name="' + tag + '"]').html(tag);
                    tags[tag].selected = 0;
                    allChange(-tags[tag].tot);
                } else {
                    for (var key in imgData)
                        if (imgData[key].tag == tag) {
                            $('.iselector-img[data-id="' + key + '"]').addClass('selected');
                            imgData[key].selected = true;
                        }
                    $('.iselector-tag[data-name="' + tag + '"]').html('<span class="glyphicon glyphicon-ok"></span> ' + tag);
                    allChange(-tags[tag].selected);
                    tags[tag].selected = tags[tag].tot;
                    allChange(tags[tag].tot);
                }
            }
        });

        $('.iselector-img').click( function() {
            var idx = $(this).attr('data-id');
            imgData[idx].selected = !imgData[idx].selected;
            if (imgData[idx].selected) {
                $(this).addClass('selected');
                tags[ imgData[idx].tag ].selected ++;
                allChange(1);
            }   else {
                $(this).removeClass('selected');
                tags[ imgData[idx].tag ].selected --;
                allChange(-1);
            }
            if (tags[ imgData[idx].tag].selected == 0) {
                $('.iselector-tag[data-name="' + imgData[idx].tag + '"]').html(imgData[idx].tag);
            }   else if (tags[ imgData[idx].tag].selected == tags[ imgData[idx].tag].tot) {
                $('.iselector-tag[data-name="' + imgData[idx].tag + '"]').html('<span class="glyphicon glyphicon-ok"></span> '+imgData[idx].tag);
            }   else {
                $('.iselector-tag[data-name="' + imgData[idx].tag + '"]').html('<span class="glyphicon glyphicon-stop"></span> '+imgData[idx].tag);
            }
        });

        for (var key in imgData)
            if (imgData[key].selected) {
                imgData[key].selected = false;
                $('.iselector-img[data-id="' + key + '"]').click();
            }

    }
    $.fn.getImgSelector = function() {
        var ret = [];
        for (var key in imgData)
            if (imgData[key].selected == true)
                ret.push(imgData[key].data);
        return ret;
    }
})(jQuery);