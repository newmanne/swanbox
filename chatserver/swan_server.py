from gevent import monkey; monkey.patch_all()

from socketio import socketio_manage
from socketio.server import SocketIOServer
from socketio.namespace import BaseNamespace
from socketio.mixins import RoomsMixin, BroadcastMixin

import random
import webbrowser


class SwanNamespace(BaseNamespace, RoomsMixin, BroadcastMixin):

    player_sessid = [];
    game_player_sessid = [];
    count = 0;
    colourSequence = list()
    colour = ['red', 'blue', 'green']
    screenSocket = None
    hostSocket = None
    roundrobin = 0;

    def on_test(self):
        print 'test works'

    def on_screen_set(self):
        print 'Screen has connected'
        self.request['nicknames'].append('Screen')
        self.socket.session['nickname'] = 'Screen'
        SwanNamespace.screenSocket = self.socket
    

    def on_nickname(self, nickname):
        self.request['nicknames'].append(nickname)
        self.socket.session['nickname'] = nickname
        self.broadcast_event('announcement', '%s has connected' % nickname)
        self.broadcast_event('nicknames', self.request['nicknames'])
        # Just have them join a default-named room
        self.join('main_room')

    def on_nickname_set(self, nickname):
        self.request['nicknames'].append(nickname)
        print self.request['nicknames']
        self.socket.session['nickname'] = nickname
        
        self.broadcast_event('announcement', '%s has connected' % nickname)
        self.broadcast_event('nicknames', self.request['nicknames'])
        
        if (SwanNamespace.count == 0):
            SwanNamespace.hostSocket = self.socket
            self.emit('elected_host')
            webbrowser.open('http://localhost:8080/chat.html')
            print 'Host is', nickname
        else:
            print 'client is', nickname
            self.emit('elected_client')
        
        SwanNamespace.count = SwanNamespace.count + 1
        # Just have them join a default-named room
        self.join('main_room')

        SwanNamespace.player_sessid.append([self.socket, nickname])
        print SwanNamespace.player_sessid

    def on_start_chatroom (self):
        print 'Starting Chatroom'
        self.broadcast_event('playing_chatroom')

    def on_start_patterns (self):
        print 'Starting Patterns'
        self.broadcast_event('playing_patterns')


    #start the pattern game
    def on_game_started(self):
        #logic code for game here
        #game_player_sessid = list(player_sessid)
        print 'GAME HAS STARTED'
        self.emit_to_socket('start_screen', SwanNamespace.screenSocket)
        print 'UPDATE_SEQUENCE'
        self.update_sequence()
        self.emit_to_socket('start_sequence', SwanNamespace.screenSocket,SwanNamespace.colourSequence)


    def on_pattern_entered(self, valid):
        print valid
        if valid[0]:
            self.update_sequence()
            self.emit_to_socket('start_sequence', SwanNamespace.screenSocket,SwanNamespace.colourSequence)
        else:
            print 'Wrong sequence from:', self.socket.session['nickname']
            self.broadcast_event('game_over')

            # del SwanNamespace.player_sessid[SwanNamespace.roundrobin]
            # if SwanNamespace.roundrobin == len(SwanNamespace.player_sessid):
            #     SwanNamespace.roundrobin = 0;
            # print 'SENDING PATTERN TO', SwanNamespace.player_sessid[SwanNamespace.roundrobin][1]
            # self.emit_to_socket('pattern_requested', Chatamespace.player_sessid[SwanNamespace.roundrobin][0], SwanNamespace.colourSequence)
            
            # SwanNamespace.roundrobin = SwanNamespace.roundrobin + 1
            

    def on_finished_sequence(self):
        #code to determine player
        
        if SwanNamespace.roundrobin >= len(SwanNamespace.player_sessid):
            SwanNamespace.roundrobin = 0;
        print 'SENDING PATTERN TO', SwanNamespace.player_sessid[SwanNamespace.roundrobin][1]
        self.emit_to_socket('pattern_requested', SwanNamespace.player_sessid[SwanNamespace.roundrobin][0], SwanNamespace.colourSequence)
        
        SwanNamespace.roundrobin = SwanNamespace.roundrobin + 1

    def recv_disconnect(self):
        # Remove nickname from the list.
        nickname = self.socket.session['nickname']
        self.request['nicknames'].remove(nickname)
        SwanNamespace.player_sessid.remove([self.socket, nickname])
        #broadcast to everyone that someone has disconnected
        self.broadcast_event('announcement', '%s has disconnected' % nickname)
        self.broadcast_event('nicknames', self.request['nicknames'])
        if nickname != 'Screen':
            SwanNamespace.count = SwanNamespace.count - 1

        print "%s Has Disconnected" % nickname
        print SwanNamespace.player_sessid

        #elect a new host
        if self.socket == SwanNamespace.hostSocket:
            if SwanNamespace.count != 0:
                self.emit_to_socket('elected_host', SwanNamespace.player_sessid[0][0])
                SwanNamespace.hostSocket = SwanNamespace.player_sessid[0][0]
                self.broadcast_event('announcement', '%s is now the host' % SwanNamespace.player_sessid[0][1])
            else:
                print "Waiting for Host to Connect"
        self.disconnect(silent=True)

    def on_user_message(self, msg):
        # tmp = msg.split("/")
        # if len(tmp) >= 2:
        #     tmp = tmp[1].split(" ")
        #     tmp2 = msg.split(" ", 2)
        #     if tmp[0] is "w":
        #         if len(tmp2) >= 3:
        #             for pair in SwanNamespace.player_sessid:
        #                 if pair[1] == tmp2[1]:
        #                     self.emit_to_socket('msg_to_room', pair[0], self.socket.session['nickname'], tmp2[2])                      
        #     else:
        #         self.emit_to_room('main_room', 'msg_to_room',
        #         self.socket.session['nickname'], msg)           
        # else:
        print msg[0]
        self.broadcast_event('msg_to_room',self.socket.session['nickname'], msg[0])
        #self.emit_to_room(self, 'main_room' 'msg_to_room',
        #self.socket.session['nickname'], msg)
        

    def recv_message(self, message):
        print "PING!!!", message


    def update_sequence(self):
        addToSequence = random.choice(SwanNamespace.colour)
        SwanNamespace.colourSequence.append(addToSequence)
        print SwanNamespace.colourSequence


    def emit_to_socket(self, event, target, *args):
        pkt = dict(type="event",
                    name=event,
                    args=args,
                    endpoint=self.ns_name)
        
        for sessid, socket in self.socket.server.sockets.iteritems():
            if socket is target:
                socket.send_packet(pkt)
                break


class Application(object):
    def __init__(self):
        self.buffer = []
        # Dummy request object to maintain state between Namespace
        # initialization.
        self.request = {
            'nicknames': [], 
        }

    def __call__(self, environ, start_response):
        path = environ['PATH_INFO'].strip('/')

        if not path:
            start_response('200 OK', [('Content-Type', 'text/html')])
            return ['<h1>Welcome. '
                'Try the <a href="/chat.html">chat</a> example.</h1>']

        if path.startswith('static/') or path == 'chat.html':
            try:
                data = open(path).read()
            except Exception:
                return not_found(start_response)

            if path.endswith(".js"):
                content_type = "text/javascript"
            elif path.endswith(".css"):
                content_type = "text/css"
            elif path.endswith(".swf"):
                content_type = "application/x-shockwave-flash"
            else:
                content_type = "text/html"

            start_response('200 OK', [('Content-Type', content_type)])
            return [data]

        if path.startswith("socket.io"):
            socketio_manage(environ, {'': SwanNamespace}, self.request)
        else:
            return not_found(start_response)


def not_found(start_response):
    start_response('404 Not Found', [])
    return ['<h1>Not Found</h1>']


if __name__ == '__main__':
    print 'Listening on port 8080 and on port 843 (flash policy server)'
    SocketIOServer(('0.0.0.0', 8080), Application(),
        resource="socket.io", policy_server=True,
        policy_listener=('0.0.0.0', 10843)).serve_forever()
