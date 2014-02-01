/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.apigenerator;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ktonder
 */
class PythonApiBuilder {
    private List<Class> messageClasses;
    private List<Class> allManager;
    private GenerateApi generator;

    PythonApiBuilder(GenerateApi generator, List<Class> allManagers, List<Class> messageClasses) {
        this.generator = generator;
        this.allManager = allManagers;
        this.messageClasses = messageClasses;
    }

    private String getCommunicationHelper() {
        String comHelper = 
            "class CommunicationHelper:\n" +
            "  def __init__(self, address):\n" +
            "    self.host = address\n" +
            "    self.port = 25554 \n" +
            "    self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)\n" +
            "    self.socket.connect((self.host, self.port))\n" +
            "    self.sessionId = str(uuid.uuid4())\n" +
            "\n" +
            "  def sendMessage(self, message):\n" +
            "    message.sessionId = self.sessionId\n" +
            "    jsonMessage = json.dumps(message.__dict__)\n" +
            "    self.socket.send(jsonMessage+\"\\n\")\n" +
            "    data = \"\"\n" +
            "    chars = []\n" +
            "    while True:\n" +
            "        a = self.socket.recv(1)\n" +
            "        chars.append(a)\n" +
            "        if a == \"\\n\" or a == \"\":\n" +
            "           data = \"\".join(chars)\n" +
            "           break\n" +
            "    data = json.loads(data)\n" +
            "    if \"errorCode\" in data:\n" +
            "      raise Exception(\"Server responded with errorcode \"+str(data[\"errorCode\"])+\" \")\n" +
            "    return data\n" +
            "\n" +
            "  def close(self):\n" +
            "    self.socket.close();\n" +
            "\n";
        return comHelper;
    }
    
    void generate() {
        String content = 
            "import socket\n" +
            "import json\n" + 
            "import collections\n" +
            "import uuid\n" +
            "\n" +
            "class GetShopBaseClass(object):\n" +
            "  pass\n" +
            "class EmptyClass(object):\n" +
            "  pass\n" +
            "\n";
        
        content += getCommunicationHelper();
        
        for (Class dataobject : allManager) {
            String dataPath = dataobject.getSimpleName().substring(1);
            String clazzData = getClassData(dataobject, dataPath);
            content += clazzData; 
        }
        
        content += getGetShopApi();
        
        try {
            this.generator.writeFile(content, "PythonGetshopApi.py");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private String getGetShopApi() {
        String api = "class GetShopApi(object):\n";
        api += "  def __init__(self, address):\n";
        api += "    self.communicationHelper = CommunicationHelper(address)\n";
        for (Class manager : allManager) {
            String managerName = manager.getSimpleName().substring(1);
            api += "    self."+managerName+" = " + managerName+"(self.communicationHelper)\n";
        }
        api += "    self.StoreManager.initializeStore(address, self.communicationHelper.sessionId)\n";
        
        return api;
    }

    private String getClassData(Class manager, String dataPath) {
        String data = "class " + dataPath + "(object):\n";
        data += "  def __init__(self, communicationHelper):\n";
        data += "    self.communicationHelper = communicationHelper";
        data += "\n";
        LinkedList<GenerateApi.ApiMethod> methods = generator.getMethods(manager);
        for (GenerateApi.ApiMethod method : methods) {
            String arguments = "";
            for (String argument : method.arguments.keySet()) {
                arguments += ", " + checkReserved(argument);
            }
            data += "  def " + method.methodName+"(self"+arguments+"):\n";
            data += "    args = collections.OrderedDict()\n";
            for (String argument : method.arguments.keySet()) {
                data += "    if isinstance("+checkReserved(argument)+",GetShopBaseClass): \n";
                data += "      args[\""+argument+"\"]=json.dumps("+checkReserved(argument)+".__dict__)\n";
                data += "    else:\n";
                data += "      try:\n";
                data += "        args[\""+argument+"\"]=json.dumps("+checkReserved(argument)+")\n";
                data += "      except (ValueError, AttributeError):\n";
                data += "        args[\""+argument+"\"]="+checkReserved(argument)+"\n";
            }
            data += "    data = EmptyClass()\n";
            data += "    data.args = args\n";
            data += "    data.method = \""+method.methodName+"\"\n";
            data += "    data.interfaceName = \""+method.manager.getCanonicalName().replace("com.thundashop.","")+"\"\n";
            data += "    return self.communicationHelper.sendMessage(data)\n";
            data += "\n";
        }
        return data;
    }

    private String checkReserved(String argument) {
        if (argument.equals("from")) {
            return "fromm";
        }
        
        return argument;
    }
}