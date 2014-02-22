from gevent import monkey; monkey.patch_all()

from socketio import socketio_manage
from socketio.server import SocketIOServer
from socketio.namespace import BaseNamespace
from socketio.mixins import RoomsMixin, BroadcastMixin

import random


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

#Connection/Disconnection Code
################################################################################################
################################################################################################
################################################################################################
    def on_screen_set(self):
        print 'Screen has connected'
       # self.request['nicknames'].append('Screen')
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
            print 'Host is', nickname
        else:
            print 'client is', nickname
            self.emit('elected_client')
        
        SwanNamespace.count = SwanNamespace.count + 1
        # Just have them join a default-named room
        self.join('main_room')

        SwanNamespace.player_sessid.append([self.socket, nickname])
        print SwanNamespace.player_sessid

    def recv_disconnect(self):
        # Remove nickname from the list.
        nickname = self.socket.session['nickname']
        SwanNamespace.player_sessid.remove([self.socket, nickname])
        #broadcast to everyone that someone has disconnected
        self.broadcast_event('announcement', '%s has disconnected' % nickname)
        self.broadcast_event('nicknames', self.request['nicknames'])
        
        if nickname != 'Screen':
            self.request['nicknames'].remove(nickname)
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

################################################################################################
################################################################################################
################################################################################################




#Definition of Events for Applications to start
################################################################################################
################################################################################################
################################################################################################

    def on_start_chatroom (self):
        print 'Starting Chatroom'
        self.broadcast_event('playing_chatroom')

    def on_start_patterns (self):
        print 'Starting Patterns'
        self.broadcast_event('playing_patterns')


################################################################################################
################################################################################################
################################################################################################


# Patterns Game Related Events:
################################################################################################
################################################################################################
################################################################################################

    def on_game_started(self):
        #logic code for game here
        #game_player_sessid = list(player_sessid)
        print 'GAME HAS STARTED'
        self.emit_to_socket('game_begin', SwanNamespace.screenSocket, self.request['nicknames'])
        

    def on_pattern_entered(self, valid):
        if valid:
            self.emit_to_socket('update_sequence', SwanNamespace.screenSocket)
        if not valid[0]:
            print 'Wrong sequence from:', self.socket.session['nickname']
            self.emit_to_socket('invalid_pattern',SwanNamespace.screenSocket, self.socket.session['nickname'])
            #self.broadcast_event('game_over')

            
    def on_finished_sequence(self, player_name, colour_sequence):
        print 'Finished Sequence'
        for index, nickname in enumerate(SwanNamespace.player_sessid):
            print nickname[1]
            print player_name
            #check if nickname is the same as in the list
            if nickname[1] == player_name:
                print 'Sending Sequence', colour_sequence, 'to', SwanNamespace.player_sessid[index][0]
                self.emit_to_socket('pattern_requested', SwanNamespace.player_sessid[index][0], colour_sequence)

    def on_game_over(self):
        self.broadcast_event('game_over')

################################################################################################
################################################################################################
################################################################################################
    

#Chatroom Related Events
################################################################################################
################################################################################################
################################################################################################
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


################################################################################################
################################################################################################
################################################################################################        


#Mailbox Implementation
################################################################################################
################################################################################################
################################################################################################

    def on_swan_broadcast(self, event, args):
        print "BROADCASTING EVENT ", event, " WITH ARGS ", args 
        self.broadcast_event('event', args)

    def on_swan_emit(self, nickname, event, args):
        print "SENDING TO ", nickname, " EVENT ", event, " WITH ARGS ", args
        emit_to_nickname(nickname, event, args)


################################################################################################
################################################################################################
################################################################################################



#Misc Functions
################################################################################################
################################################################################################
################################################################################################ 

    def recv_message(self, message):
        print "PING!!!", message



    def emit_to_nickname(self, name, event, args):
        if name == "Screen":
            self.emit_to_socket(event, SwanNamespace.screenSocket, args)
        else:
            for index, nickname in enumerate(SwanNamespace.player_sessid):
                #check if nickname is the same as in the list
                if nickname[1] == name:
                    self.emit_to_socket(event, SwanNamespace.player_sessid[index][0], args)

    def emit_to_socket(self, event, target, *args):
        pkt = dict(type="event",
                    name=event,
                    args=args,
                    endpoint=self.ns_name)
        
        for sessid, socket in self.socket.server.sockets.iteritems():
            if socket is target:
                socket.send_packet(pkt)
                break


################################################################################################
################################################################################################
################################################################################################ 

class Application(object):
    def __init__(self):
        self.buffer = []
        # Dummy request object to maintain state between Namespace
        # initialization.
        self.request = {
            'nicknames': [], 
        }

        self.config = {
            'heartbeat_timeout': 5,
            'close_timeout': 6,
            'heartbeat_interval': 1,
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




#Notes: 
#- Should the game server determine the next player or the server?
