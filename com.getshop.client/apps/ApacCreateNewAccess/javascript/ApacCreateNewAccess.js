app.ApacCreateNewAccess = {
    userCreated: function(res) {
        thundashop.common.showModal("apac_access_user_view", { userid: res });
    }
};