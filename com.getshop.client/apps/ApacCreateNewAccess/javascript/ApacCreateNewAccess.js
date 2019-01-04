app.ApacCreateNewAccess = {
    userCreated: function(res) {
        thundashop.Ajax.openModal("apac_access_user_view", { userid: res });
    }
};