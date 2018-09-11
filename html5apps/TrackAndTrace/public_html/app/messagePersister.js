/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


messagePersister = {
    messages: [],
    sentMessageTableSQL: 'CREATE TABLE IF NOT EXISTS SentMessages (id, data, dateCreated DATETIME, sent BOOLEAN)',
    
    persist: function(message) {
        message.sent = false;
        this.messages.push(message);
        if (db) {
            this.addMessageToDatabase(message);
            this.cleanup();
        } else {
            this.save();
        }
    },
    
    clearSentMessages: function() {
        alert("Starting deletion of database, press ok and wait for next message.");
        if (typeof(db) !== "undefined" && db) {
            var me = this;
        
            db.transaction(function(tx) {
                tx.executeSql(me.sentMessageTableSQL);
                db.executeSql("DELETE FROM SentMessages");
                alert("Deletion completed.");
            });
        }
    },
    
    addMessageToDatabase: function(data) {
        var me = this;
        db.transaction(function(tx) {
            tx.executeSql(me.sentMessageTableSQL);
            tx.executeSql('INSERT INTO SentMessages(id, data, dateCreated, sent) VALUES (?, ?,?, ?)', [data.messageId, JSON.stringify(data), 'CURRENT_TIMESTAMP', false]);
        }, function(error) {
            alert('Failed to store data, msg: ' + error.message);
        }, function() {
        });
    },
    
    cleanup: function() {
        var me = this;
        
        db.transaction(function(tx) {
            tx.executeSql(me.sentMessageTableSQL);
            db.executeSql("DELETE FROM SentMessages WHERE dateCreated <= date('now','-7 day')");
        });
    },
    
    updateRowFromDatabase: function(message) {
        db.transaction(function (tx) {
            var query = "UPDATE SentMessages SET sent = ? WHERE id = ?";

            tx.executeSql(query, [true, message.messageId], function(tx, res) {
            },
            function(tx, error) {
                console.log('UPDATE error: ' + error.message);
            });
        }, function(error) {
            console.log('transaction error: ' + error.message);
        }, function() {
        });
    },
    
    markAsSent: function(message) {
        for (var i in this.messages) {
            var msg = this.messages[i];
            if (msg.messageId === message.messageId) {
                msg.sent = true;
            }
        }
        
        this.copiedMessages = this.messages;
        this.messages = [];
        var i = 0;
        
        for (var i in this.copiedMessages) {
            var msg = this.copiedMessages[i];
            if (!msg.sent) {
                this.messages.push(msg);
            }
        }
        
        if (db) {
            this.updateRowFromDatabase(message);
        } else {
            this.save();
        }
    },
    
    getUnsetMessageCount: function() {
        var j = 0;
        
        for (var i in this.messages) {
            var msg = this.messages[i];
            if (!msg.sent) {
                j++;
            }
        }
        
        return j;
    },
    
    getAllUnsentMessages: function() {
        var retMsgs = [];
          
        for (var i in this.messages) {
            var msg = this.messages[i];
            if (!msg.sent) {
                retMsgs.push(msg);
            }
        }
      
        return retMsgs;
    },
    
    save: function() {
        if (typeof(db) === "undefined" || !db) {
            localStorage.setItem("sentMessages", JSON.stringify(this.messages));
        }
    },
    
    loadFromDb: function($api) {
        var me = this;
        
        db.transaction(function (tx) {
            var query = "SELECT id, data, dateCreated, sent FROM SentMessages where sent = ?";

            tx.executeSql(me.sentMessageTableSQL);
            
            tx.executeSql(query, [false], function (tx, resultSet) {
                
                for(var x = 0; x < resultSet.rows.length; x++) {
                    var toAdd = JSON.parse(resultSet.rows.item(x).data);
                    console.log(toAdd);
                    me.messages.push(toAdd);
                }
                
                if ($api && $api.getApi()) {
                    $api.getApi().sendUnsentMessages();
                }
            },
            function (tx, error) {
                alert('SELECT error: ' + error.message);
            });
        }, function (error) {
            alert('transaction error: ' + error.message);
        }, function () {
        });
    },
    
    load: function($api) {
        if (db) {
            this.loadFromDb($api);
        } else {
            this.messages = JSON.parse(localStorage.getItem("sentMessages"));
        }
        
        if (!this.messages) {
            this.messages = [];
        }
    }
};

messagePersister.load();