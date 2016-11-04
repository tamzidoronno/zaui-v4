app.CompanyOwnerChangeUserList = {
    init: function() {
        $(document).on('click', '.CompanyOwnerChangeUserList .changeuserbutton', app.CompanyOwnerChangeUserList.changeUser);
    },
    
    changeUser: function() {
        var pageId = $('.gsbody_inner').attr('pageId');
        var userId = $('#changetouserid').val();
        window.location = "/impersonate.php?userId=" + userId + "&page=" + pageId;
    }
}

app.CompanyOwnerChangeUserList.init();