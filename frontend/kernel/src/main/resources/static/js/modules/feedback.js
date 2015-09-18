
// required by feedback modal in html footer

window.Module['feedback'] = function() {

    $('#suggestion-form').draggable();
    $('.create_suggestion').click(function() {
    	$('#suggestion-form').show();
        $('#suggestion-form').modal('show');
        return false;
    });
    $("#suggestion-submit-button").click(function () {
        var content = encodeURI($("#content").val());
        $('#notification').show(0).delay(2000).hide(0);
        $.ajax({
            type:"post",
            url:"/api/suggestion",
            async: false,
            data:$("#suggestionForm").serialize(),
            success: function(data, statusText, xhr, $form) {
                $('#suggestion-form').hide();
                $('#suggestion-form').find('textarea').val('');
                $('#suggestion-notification').modal('show').delay(2000).hide();
            }
        });

    });
};
