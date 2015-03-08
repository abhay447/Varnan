from BaseHTTPServer import BaseHTTPRequestHandler
import cgi,datetime
from database import updater

class PostHandler(BaseHTTPRequestHandler):
    
    def do_POST(self):
        # Parse the form data posted
        form = cgi.FieldStorage(
            fp=self.rfile, 
            headers=self.headers,
            environ={'REQUEST_METHOD':'POST',
                     'CONTENT_TYPE':self.headers['Content-Type'],
                     })

        # Begin the response
        self.send_response(200)
        self.end_headers()
        fixer=""
        
        # Echo back information about what was posted in the form
        for field in form.keys():
            field_item = form[field]
            fixer+="%s%s"%(field,form[field].value)
        print fixer
        response=updater(fixer)
        self.wfile.write(response)
        return
		
if __name__ == '__main__':
    from BaseHTTPServer import HTTPServer
    server = HTTPServer(('0.0.0.0', 8090), PostHandler)
    print 'Starting server, use <Ctrl-C> to stop'
    server.serve_forever()

