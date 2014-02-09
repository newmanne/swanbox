from gevent import monkey; monkey.patch_all()

from socketio import socketio_manage
from socketio.server import SocketIOServer
from socketio.namespace import BaseNamespace
from socketio.mixins import RoomsMixin, BroadcastMixin

import random


class ChatNamespace(BaseNamespace, RoomsMixin, BroadcastMixin):

    player_sessid = [];
    count = 0;
    colourSequence = list()
    colour = ['red', 'blue', 'green']
    screenSocket = None

    def on_test(self):
        print 'test works'


    def on_screen_set(self):
        ChatNamespace.screenSocket = self.socket
        

        update_sequence()
        self.emit_to_whisper('start_screen', ChatNamespace.screenSocket)
        self.emit_to_whisper('start_sequence', ChatNamespace.colourSequence)
        #self.emit('start_screen')
        print "SET SCREEN"

    def on_nickname_set(self, nickname):
        self.request['nicknames'].append(nickname)
        print self.request['nicknames']
        self.socket.session['nickname'] = nickname
        self.broadcast_event('announcement', '%s has connected' % nickname)
        self.broadcast_event('nicknames', self.request['nicknames'])
        if (ChatNamespace.count == 0):
            self.emit('player_joined', 'host')
        else:
            self.emit('player_joined', 'client')
        ChatNamespace.count = ChatNamespace.count + 1
        # Just have them join a default-named room
        self.join('main_room')

        for sessid, socket in self.socket.server.sockets.iteritems():
            if socket is self.socket:
                ChatNamespace.player_sessid = ChatNamespace.player_sessid + [[socket, nickname]]
                break

    def on_game_started(self):
        #logic code for game here
        print 'GAME HAS STARTED'
        update_sequence()
        print 'SENDING SEQUENCE', ChatNamespace.colourSequence
        emit_to_whisper('start_screen', ChatNamespace.screenSocket)
        self.emit('pattern_requested', ChatNamespace.colourSequence)


    def on_pattern_entered(self, valid):
        print valid
        if valid:
            addToSequence = random.choice(ChatNamespace.colour)
            ChatNamespace.colourSequence.append(addToSequence)
            self.emit('pattern_requested', ChatNamespace.colourSequence)
        else:
            #exit the game (send to screen to stop)
            pass


    def on_finished_sequence(self):
        #code to determine player
        self.emit('pattern_requested', ChatNamespace.colourSequence)



    def recv_disconnect(self):
        # Remove nickname from the list.
        nickname = self.socket.session['nickname']
        self.request['nicknames'].remove(nickname)
        self.broadcast_event('announcement', '%s has disconnected' % nickname)
        self.broadcast_event('nicknames', self.request['nicknames'])
        ChatNamespace.count = ChatNamespace.count - 1
        self.disconnect(silent=True)

    def on_user_message(self, msg):
        tmp = msg.split("/")
        if len(tmp) >= 2:
            tmp = tmp[1].split(" ")
            tmp2 = msg.split(" ", 2)
            if tmp[0] is "w":
                if len(tmp2) >= 3:
                    for pair in ChatNamespace.player_sessid:
                        if pair[1] == tmp2[1]:
                            self.emit_to_whisper('msg_to_room', pair[0], self.socket.session['nickname'], tmp2[2])                      
            else:
                self.emit_to_room('main_room', 'msg_to_room',
                self.socket.session['nickname'], msg)           
        else:
            self.emit_to_room('main_room', 'msg_to_room',
            self.socket.session['nickname'], msg)
        

    def recv_message(self, message):
        print "PING!!!", message



    def update_sequence():
        addToSequence = random.choice(ChatNamespace.colour)
        ChatNamespace.colourSequence.append(addToSequence)


    def emit_to_whisper(self, event, target, *args):
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
            socketio_manage(environ, {'': ChatNamespace}, self.request)
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
