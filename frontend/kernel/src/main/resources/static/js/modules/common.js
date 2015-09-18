// 这里的js会被所有页面引入

window.Module['common'] = function() {

    // 既然模板是动态渲染的，这些在document.ready运行的方法就没用了。干掉它！
    $('[data-toggle="tooltip"]').tooltip();
    $(".form_datetime").datetimepicker();
}