#!/usr/bin/python
from BaseHTTPServer import BaseHTTPRequestHandler,HTTPServer
from urlparse import urlparse, parse_qs

USERNAME = "admin"
PASSWORD = "asdfi2jh3ij"

PORT_NUMBER = 18080

#This class will handles any incoming request from
#the browser 
class myHandler(BaseHTTPRequestHandler):
	
	#Handler for the GET requests
	def do_GET(self):
		self.send_response(200)
		self.send_header('Content-type','text/html')
		self.end_headers()
		# Send the html message
		self.wfile.write("All well !")

		query_components = parse_qs(urlparse(self.path).query)

		if 'username' in query_components:
			print query_components["password"][0];
			if query_components["username"][0] == USERNAME and query_components["password"][0] == PASSWORD:
				self.handleRequest();

		return

	def handleRequest(self):
		devicefile = "/root/getshop_door_lock/forceopenstate";
		query_components = parse_qs(urlparse(self.path).query)
		deviceid = query_components["deviceid"][0];
		forceopen = query_components["forceopen"][0];
		devicefile += deviceid;
		text_file = open(devicefile, "w")
		text_file.write(forceopen)
		text_file.close()
		

		print "HANDELING";

try:
	#Create a web server and define the handler to manage the
	#incoming request
	server = HTTPServer(('', PORT_NUMBER), myHandler)
	print 'Started httpserver on port ' , PORT_NUMBER
	
	#Wait forever for incoming htto requests
	server.serve_forever()

except KeyboardInterrupt:
	print '^C received, shutting down the web server'
	server.socket.close()
