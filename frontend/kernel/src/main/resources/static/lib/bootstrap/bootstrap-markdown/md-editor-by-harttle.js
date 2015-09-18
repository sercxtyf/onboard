/**
 * Created by harttle on 15/4/28.
 */
!function() {

    $.fn.mdEditor = function() {
        this.each(function(idx, ele) {
            var preview = null, $ele = $(ele);

            $ele.markdown({
                onPreview: function(e) {
                    if(!e.isDirty() && preview !== null) return preview;

                    var md = e.getContent();
                    $.post('/api/markdown', { content: md })
                        .done(function(data) {
                            $ele.closest('.md-editor').find('.md-preview').html(preview = data);
                        });
                    return '<i class="fa fa-spinner fa-spin"></i> 正在载入……';
                },
                onChange : onChange
            });

            function onChange(e) {
                // avoid endless loop
                e.$options.onChange = function() {
                };
                e.$element.focus().change();
                e.$options.onChange = onChange;
            }

            $ele.attr('placeholder', 'Markdown 提示：使用两个回车换行，通过 ``` 来引用代码块');

            var $previewBtnGroup = $ele.closest('.md-editor').find('.btn-group:last');
            $('<a>').addClass('btn btn-default btn-sm')
                .attr('href', "http://tianmaying.com/blog/8ab3eda84ce001ff014ce4ec79060095")
                .attr('target', '_blank')
                .html('<i class="fa fa-question-circle"></i> 帮助')
                .appendTo($previewBtnGroup);

            $ele.on('paste', handlePaste.bind($ele));
            $ele.bind('keydown', 'tab', function(event) {
                var e = $ele.data('markdown'),
                    selected = e.getSelection();

                e.replaceSelection('    ');
                e.setSelection(selected.start + 4, selected.start + 4);
                return false;
            });
        });
    };

    function handlePaste(event) {
        var clipboardData = event.clipboardData || event.originalEvent.clipboardData,
            items = clipboardData.items,
            imgType = /image\/.+/,
            formData = new FormData(),
            item,
            $editor = this,
            $markdown = $editor.data('markdown');

        for(var i in items) {
            if(imgType.test(items[i].type)) {
                item = items[i];
                break;
            }
        }
        if(!item) return true;

        var blob = item.getAsFile(),
            name = clipboardData.getData('Text') || "alter-text";
        formData.append("file", blob, name);

        upload({
            formData: formData,
            url     : '/api/upload/md-image',
            busy    : function() {
                $editor.attr('disabled', true);
            },
            success : function(url, name) {
                name = name || url;
                var selected = $markdown.getSelection(),
                    chunk = '![' + name + '](' + url + ')\n';

                $editor.attr('disabled', false);
                $markdown.replaceSelection(chunk);
                $editor.focus().change();

                $markdown.setSelection(selected.start + 2, selected.start + 2 + name.length);
            }, error: function() {
                $editor.attr('disabled', false);
            }
        });

        return false;
    }

    $.extend($.fn.markdown.defaults, {
        iconlibrary        : 'fa',
        language           : 'zh',
        resize             : 'vertical',
        reorderButtonGroups: ['groupFont', 'groupLink', 'groupMisc', 'groupUtil']
    });

    var toolbarBtnGroups = $.fn.markdown.defaults.buttons[0],
        params1 = /\/teams\/(\d+)/.exec(window.location.href),
        params2 = /\/teams\/(\d+)\/projects\/(\d+)/.exec(window.location.href),
        companyId, projectId, projectURI, uploadURL, imgBaseurl;

    if(params1 && params1.length >= 1) {
        companyId = params1[1];
    }
    if(params2 && params2.length >= 2) {
        projectId = params2[2];
    }
    projectURI = ['/api/', companyId, '/projects/', projectId].join('');
    uploadURL = [projectURI, '/attachments/stageUploadedImage'].join('');
    imgBaseurl = [projectURI, '/attachments/image/'].join("");

    toolbarBtnGroups[1].data.splice(0, 2, {
        name    : 'link',
        title   : "链接",
        hotkey  : 'Ctrl+L',
        icon    : "fa fa-link",
        callback: function(e) {
            var $editor = e.$element;

            selectFile({
                callFrom: 'file',
                success : function(url, name) {
                    name = name || url;
                    var selected = e.getSelection(),
                        chunk = '[' + name + '](' + url + ')\n';

                    $editor.attr('disabled', false);
                    $editor.focus();

                    e.replaceSelection(chunk);

                    $editor.change();

                    e.setSelection(selected.start + 1, selected.start + 1 + name.length);
                },
                busy    : function() {
                    $editor.attr('disabled', true).focus();
                },
                error   : function() {
                    $editor.attr('disabled', false).focus();
                }
            });
        }
    }, {
        name    : 'image',
        title   : "图片",
        hotkey  : 'Ctrl+G',
        icon    : "fa fa-image",
        callback: function(e) {
            var $editor = e.$element;

            selectFile({
                accept  : 'image/*',
                callFrom: 'image',
                success : function(url, name) {
                    name = name || 'alter text';
                    var selected = e.getSelection(),
                        chunk = '![' + name + '](' + url + ')\n';

                    $editor.attr('disabled', false);
                    $editor.focus();

                    e.replaceSelection(chunk);
                    $editor.change();

                    e.setSelection(selected.start + 2, selected.start + 2 + name.length);
                },
                error   : function() {
                    $editor.attr('disabled', false).focus();
                },
                busy    : function() {
                    $editor.attr('disabled', true);
                }
            });
        }
    });
    toolbarBtnGroups[2].data.splice(2, 1, {
        name    : 'code block',
        title   : "代码",
        hotkey  : 'Ctrl+K',
        icon    : "fa fa-code",
        callback: function(e) {
            // Replace selection with code block
            var chunk, cursor,
                selected = e.getSelection();

            chunk = '``` language\n' + selected.text + '\n```';

            // transform selection and set the cursor into chunked text
            e.replaceSelection(chunk);

            // Set the cursor
            cursor = selected.start;
            e.setSelection(cursor + 4, cursor + 12);
        }
    });

    function upload(options) {
        var xhr = new XMLHttpRequest(),
            formData = options.formData,
            uploadUrl = options.url,
            uploadSuccess = options.success,
            uploadError = options.error,
            onBusy = options.busy;

        xhr.open("POST", uploadUrl);

        xhr.upload.onprogress = function(e) {
            var percentComplete = (e.loaded / e.total) * 100;
        };

        xhr.onload = function() {
            if(xhr.status == 200 || xhr.status == 201) {
                var file = JSON.parse(xhr.response),
                    file_url;
                if(options.callFrom === 'file') {
                    file_url = [projectURI, '/attachments/', file.id, '/download'].join("");
                } else if(options.callFrom === 'image') {
                    file_url = imgBaseurl + file.id;
                }
                uploadSuccess(file_url, file.name);
            } else {
                xhr.onerror();
            }
        };
        xhr.onerror = function() {
            uploadError();
        };

        onBusy();
        xhr.send(formData);
        return false;
    }

    function selectFile(options) {

        options = options || {};
        options = {
            titles   : options.titles || ['已有链接', '上传附件'],
            accept   : options.accept || '*/*',
            uploadUrl: uploadURL,
            success  : options.success,
            error    : options.error,
            busy     : options.busy,
            callFrom : options.callFrom
        };

        var modalHTML = '<div class="modal fade">' +
                '<div class="modal-dialog">' +
                '<div class="modal-content"> ' +
                '<div class="modal-body">' +
                '<div class="tmy-tabpanel tmy-tabpanel-sm"> ' +
                '<ul class="nav nav-tabs" style="margin-bottom:15px"> ' +
                '<li class="active"><a href="#image-outlink" data-toggle="tab">' + options.titles[0] + '</a></li>' +
                '<li><a href="#image-local" data-toggle="tab">' + options.titles[1] + '</a></li>' +
                '</ul> ' +
                '<div class="tab-content" style="padding-bottom: 0;"> ' +
                '<div class="tab-pane active in outlink-tab" id="image-outlink">' +
                '<input class="form-control url">' +
                '<div class="pull-right" style="margin:10px 0">' +
                '<button type="button" class="btn btn-default btn-sm" data-dismiss="modal">取消</button> ' +
                '<button type="button" class="btn btn-primary btn-ok btn-sm">确定</button> ' +
                '</div>' +
                '<div class="clearfix"></div>' +
                '</div>' +
                '<div class="tab-pane local-tab" id="image-local">' +
                '<button type="button" class="btn btn-default btn-sm" data-dismiss="modal">取消</button> ' +
                '<a class="btn btn-primary btn-select-file btn-sm">选择文件</a>' +
                '</div> </div> </div></div></div></div></div>',
            $modal = $(modalHTML).appendTo('body').on('hidden.bs.modal', function() {
                $modal.remove();
            }),
            $url = $modal.find('input.url').val('http://'),
            $linkBtn = $modal.find('.btn-ok'),
            $selectBtn = $modal.find('.btn-select-file'),
            status;

        $linkBtn.click(function() {
            var url = $url.val().trim();

            if(!url || url === 'http://') {
                return Message.error('URL 不能为空');
            }
            status = 'success';
            $modal.modal('hide');
            options.success(url);
        });

        $selectBtn.click(function() {
            status = 'uploading';
            $modal.modal('hide');

            var $form = $('<form enctype="multipart/form-data" style="display:none">'),
                $input = $('<input name="file" type="file" accept="' + options.accept + '"/>').appendTo($form);

            $input.on('change', function() {
                var formData = new FormData($form[0]);
                upload({
                    formData: formData,
                    url     : options.uploadUrl,
                    busy    : options.busy,
                    success : options.success,
                    error   : options.error,
                    callFrom: options.callFrom
                });

                $form.remove();
            });

            $form.appendTo('body');
            $input.click();
        });
        $modal.modal('show');
    }
}();
