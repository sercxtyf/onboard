(function($) {
    $.fn.memberselector = function(input) {

        /**** SELF-FUNCTION ****/

        function checkTags(data) {
            var uTag = true;
            for (var i in data)
                if (data[i].tag == undefined) {
                    uTag = false;
                    break;
                }
            return uTag;
        }


        function setTagStatus(key, tot, selected) {
            var tagName;
            if (key == '__ALL__') tagName = '所有人';
            else tagName = key;
            if (selected == 0) {
                $('.memberSelector-tag[data-name=' + key + ']').html(tagName);
            }   else if (selected == tot) {
                $('.memberSelector-tag[data-name=' + key + ']').html('<span class="glyphicon glyphicon-ok"></span>' + tagName);
            }   else {
                $('.memberSelector-tag[data-name=' + key + ']').html('<span class="glyphicon glyphicon-stop"></span>' + tagName);
            }
        }

        function flush(faDiv) {
            if (faDiv.data('useTag')) {
                $('.memberSelector-tag', faDiv).each( function() {
                    var tagName = $(this).attr('data-name'), tot = 0, cnt = 0;
                    var findName;
                    if (tagName == "__ALL__") findName = 'input';
                    else findName = 'input[data-tag=' + tagName + ']';
                    $(findName, faDiv).each(function () {
                        if ($(this).prop('checked')) cnt++;
                        tot++;
                    });
                    setTagStatus(tagName, tot, cnt);
                })
            }
            if (faDiv.data('useImage')) {
                $('.memberSelector-img').each( function() {
                    if ($(this).find('input').prop('checked'))
                        $(this).addClass('selected');
                    else $(this).removeClass('selected');
                });
            }   else {

            }
        }
        /**** MAIN ****/

        if (jQuery.type(input) == 'string') {
            /**** command ****/

            if (input == 'getData' || input == 'getdata') {
                var ret = [];
                $('input', $(this)).each( function() {
                    if ($(this).prop('checked'))
                        ret.push($(this).data('data'));
                });
                return ret;
            }   else {
                console.error('[memberselector] command not found!');
            }
        }   else if (jQuery.type(input) == 'object') {
            /**** data ****/
            if (input.data == undefined || input.data.length < 1) {
                console.error('[memberselector] have no "data" array or "data" array is null');
                return null;
            }

            // get data type
            if (input.type == 'image') $(this).data('useImage',true);
            // get columns
            if (input.columns != undefined) $(this).data('columns', input.columns);
            else $(this).data('columns', 1);

            console.log($(this).data('useImage'));
            console.log($(this).data('columns'));

            //// set tags
            var tags = [];
            $(this).data('useTag', checkTags(input.data));
            if ($(this).data('useTag'))
                tags = $.unique(input.data.map( function(item) {return item.tag}));

            // set selected
            var data = $.extend(true, [], input.data);
            for (var i in data)
                if (data[i].selected == undefined || data[i].selected == 'false')
                    data[i].selected = false;
                else if (data[i].selected == 'true' || data[i].selected == true)
                    data[i].selected = true;
                else data[i].selected = false;

            // set image size
            var imageSize = {height: '40px', width: '40px'};
            if (input.imageHeight != undefined) imageSize.height = input.imageHeight;
            if (input.imgHeight != undefined) imageSize.height = input.imgHeight;
            if (input.imageWidth  != undefined) imageSize.width  = input.imageWidth;
            if (input.imgWidth  != undefined) imageSize.width  = input.imgWidth;

            // set div size
            if (input.height != undefined) $(this).css({height: input.height});
            if (input.width  != undefined) $(this).css({width : input.width });

            /*** create member selector ****/
            $(this).html('').addClass('memberSelector');

            if ($(this).data('useTag')) {
                var tagDiv = $('<div class="memberSelector-tag"></div>'), tagsRowDiv = $('<div class="memberSelector-tags-row"></div>');
                tagDiv.attr('data-name', '__ALL__').html('所有人').clone().appendTo(tagsRowDiv);
                for (var i in tags) {
                   tagDiv.attr('data-name', tags[i]).html(tags[i]).clone().appendTo(tagsRowDiv);
                }
                tagsRowDiv.appendTo($(this));
            }

            if ($(this).data('useImage')) {
                var member = $('<div class="memberSelector-img"><input type="checkbox"/><img></div>');
                member.find('img').css(imageSize).css({'background-size': imageSize.height + ' ' + imageSize.width});
                for (var i in data) {
                    var tMember = member.clone();
                    if (data[i].img != undefined) tMember.find('img').css({'background-image': 'url("' + data[i].img + '")'});
                    else if (data[i].image != undefined) tMember.find('img').css({'background-image': 'url("' + data[i].image + '")'});
                    else {
                        console.error('"data[' + i + ']" has no "img" or "image"');
                        return ;
                    }
                    if (data[i].selected) tMember.find('input').prop('checked', true);
                    if (data[i].lock) tMember.addClass('locked').find('input').prop('disabled', true);
                    if (data[i].title != undefined) tMember.attr('title', data[i].title);
                    if (data[i].tag != undefined) tMember.find('input').attr('data-tag', data[i].tag);
                    tMember.find('input').data('data', data[i].data);
                    tMember.appendTo($(this));
                }
            }   else {
                var member = $('<div class="checkbox checkbox-info checkbox-circle checkbox-inline memberSelector-text"><input type="checkbox" /><label></label></div>');
                member.css({width: (100.0 / $(this).data('columns')) + '%', 'margin-left': '0px'});
                for (var i in data) {
                    var tpMember = member.clone();
                    tpMember.find('label').html(data[i].title);
                    if (data[i].selected) tpMember.find('input').prop('checked', true);
                    if (data[i].lock) tpMember.find('input').prop('disabled', true);
                    if (data[i].tag != undefined) tpMember.find('input').attr('data-tag', data[i].tag);
                    tpMember.find('input').data('data', data[i].data);
                    tpMember.appendTo($(this));
                }
            }


            // register

            if ($(this).data('useTag')) {
                $('.memberSelector-tag', $(this)).click( function() {
                    var faDiv = $(this).parent().parent();
                    var cnt = 0, tot = 0;
                    if ($(this).attr('data-name') == "__ALL__") {
                        $('input', faDiv).each( function() {
                            if ($(this).prop('checked')) cnt++;
                            tot++;
                        });
                        if (cnt == 0) $('input', faDiv).click();
                        else if (cnt == tot) $('input', faDiv).click();
                        else $('input', faDiv).each( function() {
                                if ($(this).prop('checked') == false)
                                $(this).click();
                            });
                    }   else {
                        var tagName = $(this).attr('data-name');
                        $('input[data-tag=' + tagName + ']', faDiv).each( function() {
                            if ($(this).prop('checked')) cnt++;
                            tot++;
                        });
                        if (cnt == 0) $('input[data-tag=' + tagName + ']', faDiv).click();
                        else if (cnt == tot) $('input[data-tag=' + tagName + ']', faDiv).click();
                        else $('input[data-tag=' + tagName + ']', faDiv).each( function() {
                                if ($(this).prop('checked') == false)
                                    $(this).click();
                            });
                    }
                });
            }

            if ($(this).data('useImage')) {
                $('.memberSelector-img', $(this)).click( function() {
                    $(this).find('input').click();
                    flush($(this).parent());
                });
            }   else {
                $('.memberSelector-text input', $(this)).click( function() {
                    flush($(this).parent().parent());
                });
                $('.memberSelector-text label', $(this)).click( function() {
                    $(this).prev().click();
                });
            }

            flush( $(this) );
        }   else {
            console.log('Argument Error');
            return null;
        }
    }
})(jQuery);