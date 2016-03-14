getshop.jstree = {
    buildList : function(id) {
        var v = $('#categorylist').jstree(true).get_json('#');
        var list = getshop.jstree.buildNodeList(v);
        return list;
    },
    guid : function() {
        function s4() {
          return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
        }
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
          s4() + '-' + s4() + s4() + s4();
    },    
    initJsTree : function(id) {
        var tree = $('#' + id);
        tree.jstree({
            "core" : {
              // so that create works
              "check_callback" : true
            },
            "plugins" : [ "dnd","contextmenu"]
        });
        tree.bind("create_node.jstree", function (node, obj, position) {
            tree.jstree(true).set_id(obj.node,getshop.jstree.guid());
        });
    },
    buildNodeList : function(list) {
        var entries = [];
        for(var key in list) {
            var entry = {};
            var node = list[key];
            entry.id = node.id;
            entry.text = node.text;
            entry.parent = node.parent;
            if(node.children.length > 0) {
                entry.children = getshop.jstree.buildNodeList(node.children);
            }
            entries.push(entry);
        }
        return entries;
    }
};