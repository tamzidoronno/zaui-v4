#!/usr/bin/env python
 
import logging
from urlparse import urlparse, parse_qs
from BaseHTTPServer import HTTPServer
from BaseHTTPServer import BaseHTTPRequestHandler

class HttpServerHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        content_length = int(self.headers.getheader("Content-Length"))
        request = self.rfile.read(content_length)
        logging.info("Request: %s" % request)
        # BaseHTTPRequestHandler has a property called server and because
        # we create MyHTTPServer, it has a handler property
        response = self.server.handler(request)
        logging.info("Response: %s" % response)
        self.send_response(200)
        self.end_headers()
        self.wfile.write(response)
        
    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type','text/html')
        self.end_headers()
        # Send the html message

        query_components = parse_qs(urlparse(self.path).query)
        
        if 'token' not in query_components:
            self.wfile.write("Access denied (Please specify token)")    
            return "Access denied (please specify token)";

        if query_components['token'][0] != self.server.token:
            self.wfile.write("Invalid token")
            return;
        
        response = self.server.handler.doGet(query_components)
        
        self.wfile.write(response)

        return
     
class MyHTTPServer(HTTPServer):
    
    def __init__(self, server_address, RequestHandlerClass, handler, token):
        HTTPServer.__init__(self, server_address, RequestHandlerClass)
        self.handler = handler
        self.token = token
             
class HttpServer:
    def __init__(self, name, host, port, handler, token):
        self.name = name
        self.host = host
        self.port = port
        self.handler = handler
        self.token = token
        self.server = None
         
    def start(self):
        logging.info('Starting %s at %s:%d' % (self.name, self.host, self.port))
        # we need use MyHttpServer here
        self.server = MyHTTPServer((self.host, self.port), HttpServerHandler, self.handler, self.token)
        self.server.serve_forever()
     
    def stop(self):
        if self.server:
            logging.info('Stopping %s at %s:%d' % (self.name, self.host,
                                                   self.port))
            self.server.shutdown()
 
class ServerHandler:
    def __init__(self):
        self.handlers = []

    def add(self, handler):
        self.handlers.append(handler)
        
    def doGet(self, query_components):
        if "route" not in query_components:
            return "route not specified"
        
        for x in self.handlers:
            route = query_components['route'][0]
            
            if (x.route == route):
                result = x.doGet(query_components)
                return result
            
        return "route not found";
  
class WebServer:
    def __init__(self, config):
        self.handler = ServerHandler();
        self.token = config.token
        self.server = HttpServer("GetShop Server", "0.0.0.0", 9999, self.handler, self.token)
        
    def addHandler(self, handler):
        self.handler.add(handler)
        
    def start(self):
        self.server.start()
        